package com.bienestarproyect.Bienestar;

import com.bienestarproyect.Bienestar.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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

        // Habilitar CORS para permitir requests desde el frontend React
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

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
          .logout(logout -> logout
              .logoutUrl("/logout") // La URL que escuchará para el logout
              .permitAll()
              // Handler para API/Android: devuelve 200 OK sin redirección
              .logoutSuccessHandler((request, response, authentication) -> {
                  response.setStatus(200); // HTTP 200 OK
                  response.setContentType("application/json");
                  response.setCharacterEncoding("UTF-8");
                  response.getWriter().write("{\"message\":\"Logout successful\"}");
                  response.getWriter().flush();
              })
              // Invalidar la sesión y limpiar la cookie
              .invalidateHttpSession(true)
              .deleteCookies("JSESSIONID")
          );

        // H2 console and REST API endpoints: disable CSRF for these request paths so API clients (Postman/Swagger) can POST
        // NOTE: Disabling CSRF for API endpoints is acceptable for stateless API clients but evaluate for your threat model.
        // IMPORTANTE: También deshabilitamos CSRF para /login y /logout cuando viene de una app móvil/API
        http.csrf(csrf -> csrf.ignoringRequestMatchers(request -> {
            String uri = request.getRequestURI();
            return uri.startsWith("/h2-console") || uri.startsWith("/api/") 
                || uri.equals("/login") || uri.equals("/logout");
        }));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir requests desde el frontend React en desarrollo y producción
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite dev server
            "http://localhost:4173",  // Vite preview
            "http://localhost:3000"   // Otros frameworks comunes
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        
        // CRÍTICO: Permitir cookies y headers de autenticación
        configuration.setAllowCredentials(true);
        
        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "Set-Cookie",
            "Authorization"
        ));
        
        // Tiempo de cache de la configuración CORS (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}