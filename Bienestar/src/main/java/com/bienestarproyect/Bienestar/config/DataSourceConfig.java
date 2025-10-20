package com.bienestarproyect.Bienestar.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Configuración de DataSource para conectar a Supabase u otras bases de datos PostgreSQL.
 * 
 * Soporta dos formatos de configuración:
 * 1. DATABASE_URL en formato Heroku/Supabase: postgres://user:pass@host:port/db
 * 2. Variables JDBC específicas: JDBC_DATABASE_URL, JDBC_DATABASE_USERNAME, JDBC_DATABASE_PASSWORD
 * 
 * Las variables JDBC tienen prioridad sobre DATABASE_URL para mantener compatibilidad
 * con la configuración existente en application-prod.properties.
 * 
 * Compatible con Supabase Session Pooler (puerto 6543) y Transaction Pooler (puerto 5432).
 */
@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Priorizar variables JDBC específicas (compatibilidad con application-prod.properties)
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("JDBC_DATABASE_USERNAME");
        String password = System.getenv("JDBC_DATABASE_PASSWORD");

        // Si no existen las variables JDBC, intentar parsear DATABASE_URL
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            String databaseUrl = System.getenv("DATABASE_URL");
            if (databaseUrl != null && !databaseUrl.isEmpty()) {
                if (databaseUrl.startsWith("postgres://")) {
                    // Parsear formato Heroku/Supabase: postgres://user:pass@host:port/db
                    try {
                        URI dbUri = new URI(databaseUrl);
                        String userInfo = dbUri.getUserInfo();
                        if (userInfo != null) {
                            String[] credentials = userInfo.split(":");
                            username = credentials[0];
                            if (credentials.length > 1) {
                                password = credentials[1];
                            }
                        }
                        
                        // Construir JDBC URL
                        jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                                dbUri.getHost(),
                                dbUri.getPort() == -1 ? 5432 : dbUri.getPort(),
                                dbUri.getPath());
                        
                    } catch (URISyntaxException e) {
                        throw new IllegalArgumentException("DATABASE_URL inválida: " + e.getMessage(), e);
                    }
                } else if (databaseUrl.startsWith("jdbc:postgresql://")) {
                    // Ya es una JDBC URL válida
                    jdbcUrl = databaseUrl;
                }
            }
        }

        // Validar que tenemos la configuración necesaria
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            throw new IllegalStateException(
                "No se encontró configuración de base de datos. " +
                "Proporciona DATABASE_URL o JDBC_DATABASE_URL en las variables de entorno."
            );
        }

        // Configurar HikariCP
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Configuración de pool para producción
        // Mínimo para entornos con recursos limitados (Heroku free tier, etc.)
        config.setMaximumPoolSize(10);  // Máximo de conexiones en el pool
        config.setMinimumIdle(2);       // Mínimo de conexiones idle
        config.setConnectionTimeout(30000);  // 30 segundos timeout
        config.setIdleTimeout(600000);  // 10 minutos antes de cerrar conexión idle
        config.setMaxLifetime(1800000); // 30 minutos tiempo de vida máximo de conexión

        // Pool name para diagnóstico
        config.setPoolName("BienestarHikariPool");

        // Habilitar métricas y logging
        config.setLeakDetectionThreshold(60000); // Detectar conexiones que no se liberan (60 seg)

        // Para Supabase Session Pooler, ajustar estas propiedades si es necesario:
        // config.setConnectionTestQuery("SELECT 1");
        // config.setValidationTimeout(5000);

        return new HikariDataSource(config);
    }
}
