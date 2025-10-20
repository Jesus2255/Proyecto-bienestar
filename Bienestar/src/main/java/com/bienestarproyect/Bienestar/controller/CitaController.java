package com.bienestarproyect.Bienestar.controller;

import com.bienestarproyect.Bienestar.entity.Cita;
import com.bienestarproyect.Bienestar.service.CitaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {
    private final CitaService service;
    public CitaController(CitaService service){ this.service = service; }

    @PostMapping
    public Cita agendar(@RequestBody Cita c){ return service.agendar(c); }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable("id") Long id, @RequestBody Cita c){ c.setId(id); return service.actualizar(c); }

    @DeleteMapping("/{id}")
    public void cancelar(@PathVariable("id") Long id){ service.cancelar(id); }

    @GetMapping("/cliente/{clienteId}")
    public List<Cita> historial(@PathVariable("clienteId") Long clienteId){
        return service.historialPorCliente(clienteId);
    }
}