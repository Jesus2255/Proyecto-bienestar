package com.bienestarproyect.Bienestar.controller;

import com.bienestarproyect.Bienestar.entity.Servicio;
import com.bienestarproyect.Bienestar.service.ServicioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    private final ServicioService service;
    public ServicioController(ServicioService service){ this.service = service; }

    @GetMapping
    public List<Servicio> listar(){ return service.listar(); }

    @PostMapping
    public Servicio crear(@RequestBody Servicio s){ return service.guardar(s); }

    @PutMapping("/{id}")
    public Servicio actualizar(@PathVariable Long id, @RequestBody Servicio s){ s.setId(id); return service.guardar(s); }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id){ service.eliminar(id); }
}