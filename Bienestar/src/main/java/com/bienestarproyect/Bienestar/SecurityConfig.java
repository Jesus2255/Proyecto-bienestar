package com.bienestarproyect.Bienestar;

import com.bienestarproyect.Bienestar.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // WARNING: NoOpPasswordEncoder allows plain-text passwords and MUST NOT be used in production.
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(usuarioDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/swagger-ui/**",
                  "/v3/api-docs/**",
                  "/swagger-ui.html",
                  "/login", "/login.html",
                  "/css/**", "/js/**", "/images/**", "/favicon.ico",
                  "/h2-console/**"
              ).permitAll()
              .requestMatchers("/api/admin/**").hasRole("ADMIN")
              .requestMatchers("/api/**").hasAnyRole("ADMIN","RECEPTIONIST","CLIENT")
              .anyRequest().authenticated()
          )
          .formLogin(form -> form
              .loginPage("/login")
              .loginProcessingUrl("/login") // Procesa el login en /login
              .permitAll()
              // ================== SOLUCIÓN PARA API/ANDROID ==================
              // CRÍTICO: Deshabilitar TODAS las redirecciones
              .successHandler((request, response, authentication) -> {
                  // NO hacer nada más que escribir la respuesta
                  response.setStatus(200);
                  response.setContentType("application/json");
                  response.setCharacterEncoding("UTF-8");
                  response.getWriter().write("{\"message\":\"Login successful\",\"username\":\"" 
                      + authentication.getName() + "\"}");
                  response.getWriter().flush();
              })
              .failureHandler((request, response, exception) -> {
                  response.setStatus(401);
                  response.setContentType("application/json");
                  response.setCharacterEncoding("UTF-8");
                  response.getWriter().write("{\"error\":\"Authentication failed\",\"message\":\""
                      + exception.getMessage() + "\"}");
                  response.getWriter().flush();
              })
              // Deshabilitar la URL de éxito por defecto que causa redirecciones
              .defaultSuccessUrl("/", false) // false = no forzar redirección
              // ================================================================
          )
          .logout(logout -> logout.permitAll());

        // H2 console and REST API endpoints: disable CSRF for these request paths so API clients (Postman/Swagger) can POST
        // NOTE: Disabling CSRF for API endpoints is acceptable for stateless API clients but evaluate for your threat model.
        // IMPORTANTE: También deshabilitamos CSRF para /login cuando viene de una app móvil/API
        http.csrf(csrf -> csrf.ignoringRequestMatchers(request -> {
            String uri = request.getRequestURI();
            return uri.startsWith("/h2-console") || uri.startsWith("/api/") || uri.equals("/login");
        }));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}