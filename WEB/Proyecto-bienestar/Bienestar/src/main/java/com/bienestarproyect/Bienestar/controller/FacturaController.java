package com.bienestarproyect.Bienestar.controller;

import com.bienestarproyect.Bienestar.dto.FacturaDTO;
import com.bienestarproyect.Bienestar.entity.Cliente;
import com.bienestarproyect.Bienestar.entity.Factura;
import com.bienestarproyect.Bienestar.mapper.DTOMapper;
import com.bienestarproyect.Bienestar.repository.ClienteRepository;
import com.bienestarproyect.Bienestar.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    private final FacturaService service;
    private final ClienteRepository clienteRepo;

    public FacturaController(FacturaService service, ClienteRepository clienteRepo){ this.service = service; this.clienteRepo = clienteRepo; }

    @GetMapping
    public List<Factura> listarTodas() {
        return service.listarTodas();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody FacturaDTO dto){
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElse(null);
        if (cliente == null) return ResponseEntity.badRequest().body("cliente no encontrado");
        Factura f = DTOMapper.toEntity(dto, cliente);
        return ResponseEntity.ok(service.crear(f));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Factura> porCliente(@PathVariable Long clienteId){ return service.porCliente(clienteId); }
}