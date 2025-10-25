package com.bienestarproyect.Bienestar.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple migration runner used when Flyway is not available/disabled for the target DB.
 * It loads SQL files from classpath:db/migration (pattern V*.sql), executes them in order
 * and inserts a record into flyway_schema_history to avoid reapplying the same migration.
 *
 * Enable by setting either spring.flyway.enabled=false OR app.simple-migrations.enabled=true
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleMigrationRunner implements ApplicationRunner {

    private final Logger log = LoggerFactory.getLogger(SimpleMigrationRunner.class);
    private final JdbcTemplate jdbc;
    private final DataSource dataSource;
    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final Environment env;

    @Autowired
    public SimpleMigrationRunner(DataSource dataSource, JdbcTemplate jdbc, Environment env) {
        this.dataSource = dataSource;
        this.jdbc = jdbc;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean flywayEnabled = Boolean.parseBoolean(env.getProperty("spring.flyway.enabled", "true"));
        boolean simpleEnabled = Boolean.parseBoolean(env.getProperty("app.simple-migrations.enabled", "false"));

        if (flywayEnabled && !simpleEnabled) {
            log.info("Flyway is enabled and simple migrations are disabled -> skipping SimpleMigrationRunner");
            return;
        }

        // Check DB product to give a helpful log message
        try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
            String product = conn.getMetaData().getDatabaseProductName();
            String version = conn.getMetaData().getDatabaseProductVersion();
            log.info("SimpleMigrationRunner: connected to DB: {} {}", product, version);
        } catch (SQLException e) {
            log.warn("SimpleMigrationRunner: could not obtain DB metadata, aborting migrations", e);
            return;
        }

        Resource[] resources = resolver.getResources("classpath:db/migration/V*.sql");
        if (resources == null || resources.length == 0) {
            log.info("No migration resources found in classpath:db/migration");
            return;
        }

        // Sort by filename so V1, V2, V10 order is preserved (lexicographic with V prefix usually works)
        Arrays.sort(resources, Comparator.comparing(r -> {
            try {
                return r.getFilename();
            } catch (Exception ex) {
                return "";
            }
        }));

        ensureFlywayTableExists();

        List<String> applied = new ArrayList<>();
        try {
            applied = jdbc.queryForList("select version from flyway_schema_history where version is not null", String.class);
        } catch (Exception e) {
            log.debug("Could not read flyway_schema_history (may be empty): {}", e.getMessage());
        }

        int nextRank = jdbc.queryForObject("select coalesce(max(installed_rank),0) from flyway_schema_history", Integer.class);

        for (Resource r : resources) {
            String filename = r.getFilename();
            if (filename == null) continue;
            String version = extractVersionFromFilename(filename);
            if (version == null) {
                log.warn("Skipping migration with unexpected filename format: {}", filename);
                continue;
            }
            if (applied.contains(version)) {
                log.info("Migration {} already applied, skipping", filename);
                continue;
            }

            log.info("Applying migration {}", filename);
            String sql = readResourceToString(r);
            if (sql == null || sql.trim().isEmpty()) {
                log.warn("Migration {} is empty, skipping execution", filename);
                continue;
            }

        // Remove SQL comments (block comments and single-line -- comments) before splitting
        String cleaned = removeSqlComments(sql);

        // Split statements by semicolon. This is best-effort but now comments won't break statements.
        List<String> statements = Arrays.stream(cleaned.split(";\n|;\r?\n|;"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

            try {
                for (String stmt : statements) {
                    log.debug("Executing SQL statement (truncated): {}", stmt.length() > 200 ? stmt.substring(0, 200) + "..." : stmt);
                    jdbc.execute(stmt);
                }

                nextRank++;
                // Insert a minimal record into flyway_schema_history
                jdbc.update("INSERT INTO flyway_schema_history(installed_rank, version, description, type, script, installed_by, success) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        nextRank, version, descriptionFromFilename(filename), "SQL", filename, "simple-migrations", true);

                log.info("Migration {} applied and registered as version {}", filename, version);
            } catch (Exception ex) {
                log.error("Failed to apply migration {}: {}", filename, ex.getMessage(), ex);
                throw ex;
            }
        }
    }

    /**
     * Remove SQL comments: block comments /* ... *-/ and single-line comments starting with --
     */
    private String removeSqlComments(String sql) {
        if (sql == null) return null;
        // remove block comments
        String noBlock = sql.replaceAll("(?s)/\\*.*?\\*/", " ");
        // remove single-line -- comments
        StringBuilder sb = new StringBuilder();
        String[] lines = noBlock.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) continue;
            // also remove inline -- comments
            int idx = line.indexOf("--");
            if (idx >= 0) {
                line = line.substring(0, idx);
            }
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private void ensureFlywayTableExists() {
        String createSql = "CREATE TABLE IF NOT EXISTS flyway_schema_history (" +
                "installed_rank INT NOT NULL, " +
                "version VARCHAR(50), " +
                "description VARCHAR(200), " +
                "type VARCHAR(20), " +
                "script VARCHAR(1000), " +
                "installed_by VARCHAR(100), " +
                "installed_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "success BOOLEAN" +
                ")";
        try {
            jdbc.execute(createSql);
        } catch (Exception e) {
            log.warn("Could not create or verify flyway_schema_history table: {}", e.getMessage());
        }
    }

    private String extractVersionFromFilename(String filename) {
        // Expecting filenames like V1__init.sql or V2__create_demo_tables.sql
        if (!filename.startsWith("V")) return null;
        int idx = filename.indexOf("__");
        if (idx < 2) return null;
        return filename.substring(1, idx);
    }

    private String descriptionFromFilename(String filename) {
        int idx = filename.indexOf("__");
        if (idx < 0) return filename;
        String rest = filename.substring(idx + 2);
        if (rest.endsWith(".sql")) rest = rest.substring(0, rest.length() - 4);
        return rest.replace('_', ' ');
    }

    private String readResourceToString(Resource r) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(r.getInputStream(), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            log.warn("Could not read resource {}: {}", r.getDescription(), e.getMessage());
            return null;
        }
    }
}
