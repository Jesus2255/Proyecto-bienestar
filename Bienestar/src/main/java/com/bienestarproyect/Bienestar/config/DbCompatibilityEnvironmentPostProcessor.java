package com.bienestarproyect.Bienestar.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Detects the remote PostgreSQL server version early and disables Flyway automatically
 * if the server major version is 17 or greater. This is a pragmatic fallback to avoid
 * Flyway failing the whole app at startup when an unsupported DB minor/major version
 * is encountered. For production, prefer upgrading Flyway.
 */
public class DbCompatibilityEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dbCompatibilityOverride";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // if flyway explicitly disabled, do nothing
        String flywayEnabled = environment.getProperty("spring.flyway.enabled");
        if ("false".equalsIgnoreCase(flywayEnabled)) {
            return;
        }

        String url = environment.getProperty("spring.datasource.url");
        String user = environment.getProperty("spring.datasource.username");
        String pass = environment.getProperty("spring.datasource.password");

        if (url == null || user == null) {
            // nothing to probe
            return;
        }

        Connection c = null;
        try {
            c = DriverManager.getConnection(url, user, pass);
            DatabaseMetaData md = c.getMetaData();
            int major = md.getDatabaseMajorVersion();
            if (major >= 17) {
                // disable Flyway to avoid unsupported DB exceptions at startup
                Map<String, Object> overrides = new HashMap<>();
                overrides.put("spring.flyway.enabled", "false");
                MutablePropertySources sources = environment.getPropertySources();
                if (!sources.contains(PROPERTY_SOURCE_NAME)) {
                    sources.addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, overrides));
                }
                System.err.println("[DbCompatibility] Detected PostgreSQL " + major + " - temporarily disabling Flyway. Apply migrations manually or update Flyway.");
            }
        } catch (Exception ex) {
            // ignore - cannot probe DB, leave behavior unchanged
            System.err.println("[DbCompatibility] Could not probe DB version: " + ex.getMessage());
        } finally {
            if (c != null) try { c.close(); } catch (Exception ignored) {}
        }
    }
}
