package com.bienestarproyect.Bienestar;

import com.bienestarproyect.Bienestar.entity.Role;
import com.bienestarproyect.Bienestar.entity.Usuario;
import com.bienestarproyect.Bienestar.repository.RoleRepository;
import com.bienestarproyect.Bienestar.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(RoleRepository roleRepo, UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.roleRepo = roleRepo;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Role admin = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(new Role("ADMIN")));
        Role recep = roleRepo.findByName("RECEPTIONIST").orElseGet(() -> roleRepo.save(new Role("RECEPTIONIST")));
        Role client = roleRepo.findByName("CLIENT").orElseGet(() -> roleRepo.save(new Role("CLIENT")));
        // Ensure admin user exists and has demo password
        var adminOpt = usuarioRepo.findByUsername("admin");
        if (adminOpt.isEmpty()) {
            // store demo password in plain text for easier testing
            Usuario u = new Usuario("admin", "1234");
            u.setRoles(Set.of(admin, recep));
            usuarioRepo.save(u);
        } else {
            Usuario existing = adminOpt.get();
            existing.setPassword("1234");
            existing.setRoles(Set.of(admin, recep));
            usuarioRepo.save(existing);
        }

        // Ensure client user exists and has demo password
        var clientOpt = usuarioRepo.findByUsername("client");
        if (clientOpt.isEmpty()) {
            Usuario u2 = new Usuario("client", "1234");
            u2.setRoles(Set.of(client));
            usuarioRepo.save(u2);
        } else {
            Usuario existing2 = clientOpt.get();
            existing2.setPassword("1234");
            existing2.setRoles(Set.of(client));
            usuarioRepo.save(existing2);
        }

    // Ensure demo tables exist (useful when Flyway is disabled)
    // clientes
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS clientes (\n"
        + "id BIGSERIAL PRIMARY KEY,\n"
        + "nombre VARCHAR(255),\n"
        + "email VARCHAR(255),\n"
        + "telefono VARCHAR(100)\n"
        + ");");

    // servicios
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS servicios (\n"
        + "id BIGSERIAL PRIMARY KEY,\n"
        + "nombre VARCHAR(255),\n"
        + "descripcion TEXT,\n"
        + "precio NUMERIC(12,2)\n"
        + ");");

    // citas
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS citas (\n"
        + "id BIGSERIAL PRIMARY KEY,\n"
        + "cliente_id BIGINT,\n"
        + "servicio_id BIGINT,\n"
        + "fecha_hora TIMESTAMP,\n"
        + "estado VARCHAR(50),\n"
        + "CONSTRAINT fk_cita_cliente FOREIGN KEY(cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,\n"
        + "CONSTRAINT fk_cita_servicio FOREIGN KEY(servicio_id) REFERENCES servicios(id) ON DELETE SET NULL\n"
        + ");");

    // Ensure flyway_schema_history exists and register V2 if missing (useful when Flyway was disabled)
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS flyway_schema_history (\n"
        + "installed_rank INT PRIMARY KEY,\n"
        + "version VARCHAR(50),\n"
        + "description VARCHAR(200),\n"
        + "type VARCHAR(20),\n"
        + "script VARCHAR(200),\n"
        + "installed_by VARCHAR(100),\n"
        + "installed_on TIMESTAMP DEFAULT now(),\n"
        + "success BOOLEAN\n"
        + ");");

    Integer exists = jdbcTemplate.queryForObject(
        "SELECT COUNT(1) FROM flyway_schema_history WHERE version = '2'",
        Integer.class
    );
    if (exists == null || exists == 0) {
        jdbcTemplate.update(
            "INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, installed_by, success) "
                + "VALUES ((SELECT COALESCE(MAX(installed_rank),0)+1 FROM flyway_schema_history), ?, ?, ?, ?, current_user, true)",
            "2",
            "Create demo tables (V2__create_demo_tables.sql)",
            "SQL",
            "V2__create_demo_tables.sql"
        );
    }
    }
}