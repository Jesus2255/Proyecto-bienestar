package com.bienestarproyect.Bienestar.controller;

import com.bienestarproyect.Bienestar.dto.CitaDTO;
import com.bienestarproyect.Bienestar.entity.*;
import com.bienestarproyect.Bienestar.mapper.DTOMapper;
import com.bienestarproyect.Bienestar.repository.ClienteRepository;
import com.bienestarproyect.Bienestar.repository.ServicioRepository;
import com.bienestarproyect.Bienestar.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {
    private final CitaService service;
    private final ClienteRepository clienteRepo;
    private final ServicioRepository servicioRepo;

    public CitaController(CitaService service, ClienteRepository clienteRepo, ServicioRepository servicioRepo){
        this.service = service;
        this.clienteRepo = clienteRepo;
        this.servicioRepo = servicioRepo;
    }


    @GetMapping
    public List<Cita> listarTodas() {
        return service.listarTodas();
    }

    @PostMapping
    public ResponseEntity<?> agendar(@Valid @RequestBody CitaDTO dto){
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElse(null);
        Servicio servicio = servicioRepo.findById(dto.getServicioId()).orElse(null);
        if (cliente == null || servicio == null) {
            return ResponseEntity.badRequest().body("cliente o servicio no encontrado");
        }
        Cita cita = DTOMapper.toEntity(dto, cliente, servicio);
        return ResponseEntity.ok(service.agendar(cita));
    }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable("id") Long id, @RequestBody CitaDTO dto){
        // simple: map and set id (validaciones similares que en agendar)
        Cliente cliente = clienteRepo.findById(dto.getClienteId()).orElse(null);
        Servicio servicio = servicioRepo.findById(dto.getServicioId()).orElse(null);
        Cita c = DTOMapper.toEntity(dto, cliente, servicio);
        c.setId(id);
        return service.actualizar(c);
    }

    @DeleteMapping("/{id}")
    public void cancelar(@PathVariable("id") Long id){ service.cancelar(id); }
}