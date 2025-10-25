package com.bienestarproyect.Bienestar;

import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    private final Environment env;

    public DebugController(Environment env) {
        this.env = env;
    }

    @GetMapping("/debug/session")
    public Map<String, Object> sessionInfo(Authentication auth) {
        Map<String, Object> out = new HashMap<>();
        out.put("authenticated", auth != null && auth.isAuthenticated());
        if (auth != null) {
            out.put("name", auth.getName());
            out.put("roles", auth.getAuthorities().toString());
        }

        // Datasource and profiles for debugging
        out.put("spring.datasource.url", env.getProperty("spring.datasource.url"));
        out.put("active.profiles", String.join(",", env.getActiveProfiles()));

        return out;
    }
}
