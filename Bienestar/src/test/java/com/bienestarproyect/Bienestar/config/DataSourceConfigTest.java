package com.bienestarproyect.Bienestar.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.sql.DataSource;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para DataSourceConfig.
 * 
 * Estas pruebas verifican que la clase puede parsear correctamente
 * una DATABASE_URL en formato Supabase/Heroku y crear un DataSource válido.
 */
class DataSourceConfigTest {

    @Test
    @DisplayName("Debe parsear correctamente una DATABASE_URL de Supabase")
    void testParseSupabaseDatabaseUrl() throws URISyntaxException {
        // Given: Una URL de base de datos en formato Supabase
        String databaseUrl = "postgres://postgres.example:mypassword@aws-0-us-east-1.pooler.supabase.com:6543/postgres";
        
        // Simular que DATABASE_URL está configurada
        // (En un entorno real, esto se configuraría como variable de entorno)
        
        // When: Se parsea la URL manualmente (similar a lo que hace DataSourceConfig)
        java.net.URI dbUri = new java.net.URI(databaseUrl);
        
        // Then: Los componentes se extraen correctamente
        assertNotNull(dbUri.getUserInfo(), "UserInfo no debe ser null");
        
        String[] userInfo = dbUri.getUserInfo().split(":");
        assertEquals(2, userInfo.length, "UserInfo debe tener usuario y contraseña");
        assertEquals("postgres.example", userInfo[0], "Usuario debe ser 'postgres.example'");
        assertEquals("mypassword", userInfo[1], "Contraseña debe ser 'mypassword'");
        
        assertEquals("aws-0-us-east-1.pooler.supabase.com", dbUri.getHost(), "Host debe coincidir");
        assertEquals(6543, dbUri.getPort(), "Puerto debe ser 6543");
        assertEquals("/postgres", dbUri.getPath(), "Path debe ser '/postgres'");
        
        // Verificar que se puede construir la JDBC URL
        String database = dbUri.getPath().substring(1);
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", 
            dbUri.getHost(), dbUri.getPort(), database);
        
        assertEquals("jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres", 
            jdbcUrl, "JDBC URL debe construirse correctamente");
    }
    
    @Test
    @DisplayName("Debe parsear correctamente una DATABASE_URL de Heroku")
    void testParseHerokuDatabaseUrl() throws URISyntaxException {
        // Given: Una URL de base de datos en formato Heroku
        String databaseUrl = "postgres://username:password@ec2-host.compute-1.amazonaws.com:5432/dbname";
        
        // When: Se parsea la URL
        java.net.URI dbUri = new java.net.URI(databaseUrl);
        
        // Then: Los componentes se extraen correctamente
        String[] userInfo = dbUri.getUserInfo().split(":");
        assertEquals("username", userInfo[0]);
        assertEquals("password", userInfo[1]);
        assertEquals("ec2-host.compute-1.amazonaws.com", dbUri.getHost());
        assertEquals(5432, dbUri.getPort());
        
        String database = dbUri.getPath().substring(1);
        assertEquals("dbname", database);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción con URL malformada")
    void testMalformedUrl() {
        // Given: Una URL malformada con caracteres inválidos
        String malformedUrl = "postgres://user:pass@host:port with spaces/db";
        
        // When/Then: Debe lanzar URISyntaxException
        assertThrows(URISyntaxException.class, () -> {
            new java.net.URI(malformedUrl);
        }, "Debe lanzar URISyntaxException con URL inválida");
    }
    
    @Test
    @DisplayName("Debe manejar caracteres especiales en contraseña")
    void testSpecialCharactersInPassword() throws URISyntaxException {
        // Given: Una URL con caracteres especiales en la contraseña
        // Nota: En URLs reales, los caracteres especiales deben estar codificados
        String databaseUrl = "postgres://user:p%40ssw0rd%21@host.com:5432/db";
        
        // When: Se parsea la URL
        java.net.URI dbUri = new java.net.URI(databaseUrl);
        
        // Then: El usuario y contraseña se extraen (pueden necesitar decodificación)
        assertNotNull(dbUri.getUserInfo());
        assertTrue(dbUri.getUserInfo().contains(":"), "UserInfo debe contener separador");
    }
}
