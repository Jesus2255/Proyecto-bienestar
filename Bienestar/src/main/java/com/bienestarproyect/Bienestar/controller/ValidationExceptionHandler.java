package com.bienestarproyect.Bienestar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        // Log full stacktrace for debugging (include cause). Use logger exception param so stacktrace is visible
        log.error("Unhandled exception caught in ValidationExceptionHandler", ex);
        // Also print to stderr to make sure it appears in any consoles that don't show the logger stacktrace
        ex.printStackTrace(System.err);
        Throwable cause = ex.getCause();
        if (cause != null) {
            log.error("Root cause:", cause);
            cause.printStackTrace(System.err);
        }
        // devolver mensaje simple para debugging; en producción registrar y retornar mensaje genérico
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}