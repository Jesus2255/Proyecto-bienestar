package com.bienestarproyect.Bienestar.service;

import com.bienestarproyect.Bienestar.entity.Cita;
import com.bienestarproyect.Bienestar.repository.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CitaService {
    private final CitaRepository repo;
    public CitaService(CitaRepository repo){ this.repo = repo; }

    @Transactional
    public Cita agendar(Cita c){ c.setEstado("AGENDADA"); return repo.save(c); }

    @Transactional
    public Cita actualizar(Cita c){ return repo.save(c); }

    @Transactional
    public void cancelar(Long id){
        repo.findById(id).ifPresent(c -> { c.setEstado("CANCELADA"); repo.save(c); });
    }

    public List<Cita> historialPorCliente(Long clienteId){
        return repo.findByCliente_IdOrderByFechaHoraDesc(clienteId);
    }
}