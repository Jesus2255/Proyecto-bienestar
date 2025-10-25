package com.bienestarproyect.Bienestar;

import com.bienestarproyect.Bienestar.entity.Role;
import com.bienestarproyect.Bienestar.entity.Usuario;
import com.bienestarproyect.Bienestar.repository.RoleRepository;
import com.bienestarproyect.Bienestar.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepo, UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Role admin = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(new Role("ADMIN")));
        Role recep = roleRepo.findByName("RECEPTIONIST").orElseGet(() -> roleRepo.save(new Role("RECEPTIONIST")));
        Role client = roleRepo.findByName("CLIENT").orElseGet(() -> roleRepo.save(new Role("CLIENT")));

        if (usuarioRepo.findByUsername("admin").isEmpty()) {
            Usuario u = new Usuario("admin", passwordEncoder.encode("1234"));
            u.setRoles(Set.of(admin, recep));
            usuarioRepo.save(u);
        }
        if (usuarioRepo.findByUsername("client").isEmpty()) {
            Usuario u2 = new Usuario("client", passwordEncoder.encode("1234"));
            u2.setRoles(Set.of(client));
            usuarioRepo.save(u2);
        }
    }
}