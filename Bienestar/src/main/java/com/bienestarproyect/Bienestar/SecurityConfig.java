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
        return new BCryptPasswordEncoder();
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
              .permitAll()
          )
          .httpBasic()
          .and()
          .logout(logout -> logout.permitAll());

        // H2 console
        http.csrf(csrf -> csrf.ignoringRequestMatchers(request -> request.getRequestURI().startsWith("/h2-console")));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}