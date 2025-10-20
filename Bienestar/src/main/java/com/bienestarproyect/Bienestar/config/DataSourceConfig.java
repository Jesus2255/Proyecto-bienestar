package com.bienestarproyect.Bienestar.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuración del DataSource para Supabase (PostgreSQL).
 * 
 * Esta clase lee la variable de entorno DATABASE_URL (formato Heroku/Supabase)
 * y crea un DataSource configurado con HikariCP.
 * 
 * Formato esperado de DATABASE_URL:
 *   postgres://username:password@host:port/database
 *   
 * Ejemplo de Supabase:
 *   postgres://postgres.xyzcompany:[password]@aws-0-us-east-1.pooler.supabase.com:6543/postgres
 *   
 * Si DATABASE_URL no está presente, Spring Boot usará la configuración
 * por defecto de application.properties (H2, etc.).
 * 
 * @author Jesus2255
 */
@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    /**
     * Crea un DataSource basado en la variable de entorno DATABASE_URL.
     * 
     * Solo se activa si DATABASE_URL está definida como variable de entorno.
     * Parsea la URL en formato postgres://user:pass@host:port/db y
     * construye una URL JDBC jdbc:postgresql://host:port/db compatible con el driver de PostgreSQL.
     * 
     * @return DataSource configurado con HikariCP
     * @throws URISyntaxException si DATABASE_URL tiene un formato inválido
     */
    @Bean
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource dataSource() throws URISyntaxException {
        String databaseUrl = System.getenv("DATABASE_URL");
        logger.info("DATABASE_URL detectada. Configurando DataSource para Supabase/PostgreSQL...");

        // Parsear la URI de la base de datos
        URI dbUri = new URI(databaseUrl);
        
        // Extraer componentes de la URI
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String host = dbUri.getHost();
        int port = dbUri.getPort();
        String database = dbUri.getPath().substring(1); // Remover el '/' inicial
        
        // Construir JDBC URL en formato jdbc:postgresql://host:port/database
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        
        logger.info("JDBC URL construida: {}", jdbcUrl);
        logger.info("Database host: {}", host);
        logger.info("Database port: {}", port);
        logger.info("Database name: {}", database);
        logger.info("Database username: {}", username);

        // Configurar HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración del pool de conexiones
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30 segundos
        config.setIdleTimeout(600000); // 10 minutos
        config.setMaxLifetime(1800000); // 30 minutos
        config.setPoolName("BienestarSupabaseHikariPool");
        
        // Propiedades adicionales para PostgreSQL/Supabase
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        // Habilitar comprobación de conectividad
        config.setConnectionTestQuery("SELECT 1");

        logger.info("DataSource configurado exitosamente para Supabase/PostgreSQL");
        
        return new HikariDataSource(config);
    }
}
