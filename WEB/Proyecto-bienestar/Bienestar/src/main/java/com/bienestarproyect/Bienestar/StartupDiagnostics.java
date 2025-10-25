package com.bienestarproyect.Bienestar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupDiagnostics implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupDiagnostics.class);

    @Override
    public void run(String... args) throws Exception {
        printClassSource("org.springframework.web.method.ControllerAdviceBean");
        printClassSource("org.springframework.web.servlet.DispatcherServlet");
        printClassSource("org.springdoc.webmvc.api.OpenApiWebMvcResource");
    }

    private void printClassSource(String className) {
        try {
            Class<?> c = Class.forName(className);
            var src = c.getProtectionDomain().getCodeSource();
            String loc = (src != null ? src.getLocation().toString() : "<unknown>");
            log.info("DIAG: {} loaded from: {}", className, loc);
            System.err.println("DIAG: " + className + " loaded from: " + loc);
        } catch (ClassNotFoundException e) {
            log.info("DIAG: {} NOT found on classpath", className);
            System.err.println("DIAG: " + className + " NOT found on classpath");
        } catch (Exception e) {
            log.info("DIAG: error checking {}: {}", className, e.toString());
            System.err.println("DIAG: error checking " + className + ": " + e.toString());
        }
    }
}
