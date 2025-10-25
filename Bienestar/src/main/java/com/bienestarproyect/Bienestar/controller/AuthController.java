package com.bienestarproyect.Bienestar.controller;

import com.bienestarproyect.Bienestar.dto.UserInfoResponse;
import com.bienestarproyect.Bienestar.entity.Role;
import com.bienestarproyect.Bienestar.entity.Usuario;
import com.bienestarproyect.Bienestar.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Endpoint para obtener la información del usuario autenticado
     * GET /api/auth/user-info
     * 
     * Retorna el nombre de usuario y su rol principal.
     * Prioriza ADMIN > RECEPTIONIST > CLIENT
     */
    @GetMapping("/user-info")
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401)
                .body(new UserInfoResponse(false, null, null));
        }

        String username = authentication.getName();
        
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(404)
                .body(new UserInfoResponse(false, username, null));
        }

        // Obtener el rol principal (prioridad: ADMIN > RECEPTIONIST > CLIENT)
        String rolePrincipal = determineMainRole(usuario.getRoles());

        return ResponseEntity.ok(new UserInfoResponse(true, username, rolePrincipal));
    }

    /**
     * Determina el rol principal del usuario basado en prioridad:
     * 1. ADMIN (más alto)
     * 2. RECEPTIONIST
     * 3. CLIENT (más bajo)
     * 
     * Si el usuario tiene múltiples roles, retorna el de mayor prioridad.
     */
    private String determineMainRole(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return "ROLE_CLIENT"; // Por defecto, si no tiene roles
        }

        // Buscar ADMIN primero (mayor prioridad)
        for (Role role : roles) {
            if (role.getName().toUpperCase().contains("ADMIN")) {
                return "ROLE_ADMIN";
            }
        }

        // Buscar RECEPTIONIST segundo
        for (Role role : roles) {
            if (role.getName().toUpperCase().contains("RECEPTIONIST")) {
                return "ROLE_RECEPTIONIST";
            }
        }

        // Buscar CLIENT tercero
        for (Role role : roles) {
            if (role.getName().toUpperCase().contains("CLIENT")) {
                return "ROLE_CLIENT";
            }
        }

        // Si no coincide con ninguno, retornar el primer rol o CLIENT por defecto
        return roles.isEmpty() ? "ROLE_CLIENT" : "ROLE_" + roles.iterator().next().getName().toUpperCase();
    }
}
