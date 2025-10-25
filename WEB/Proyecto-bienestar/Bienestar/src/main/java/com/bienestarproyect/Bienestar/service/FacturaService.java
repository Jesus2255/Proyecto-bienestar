package com.bienestarproyect.Bienestar.service;

import com.bienestarproyect.Bienestar.entity.Factura;
import com.bienestarproyect.Bienestar.repository.FacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FacturaService {
    private final FacturaRepository repo;
    public List<Factura> listarTodas() {
        return repo.findAll();
    }
    public FacturaService(FacturaRepository repo){ this.repo = repo; }
    public Factura crear(Factura f){ f.setFecha(LocalDateTime.now()); return repo.save(f); }
    public List<Factura> porCliente(Long clienteId){ return repo.findByCliente_IdOrderByFechaDesc(clienteId); }
}