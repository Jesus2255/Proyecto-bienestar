package com.bienestarproyect.Bienestar.controller;

import com.bienestarproyect.Bienestar.entity.Factura;
import com.bienestarproyect.Bienestar.service.FacturaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    private final FacturaService service;
    public FacturaController(FacturaService service){ this.service = service; }

    @PostMapping
    public Factura crear(@RequestBody Factura f){ return service.crear(f); }

    @GetMapping("/cliente/{clienteId}")
    public List<Factura> porCliente(@PathVariable Long clienteId){ return service.porCliente(clienteId); }
}