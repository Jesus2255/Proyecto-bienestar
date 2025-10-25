package com.bienestarproyect.Bienestar.service;

import com.bienestarproyect.Bienestar.entity.Cita;
import com.bienestarproyect.Bienestar.repository.CitaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CitaService {
    private final CitaRepository repo;
    
    public CitaService(CitaRepository repo){ 
        this.repo = repo; 
    }

    @Transactional(readOnly = true)
    public List<Cita> listarTodas() {
        try {
            return repo.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al listar citas: " + e.getMessage());
        }
    }

    @Transactional
    public Cita agendar(Cita c){ 
        try {
            c.setEstado("AGENDADA"); 
            return repo.save(c);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al agendar cita: " + e.getMessage());
        }
    }

    @Transactional
    public Cita actualizar(Cita c){ 
        try {
            return repo.save(c);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar cita: " + e.getMessage());
        }
    }

    @Transactional
    public void cancelar(Long id){
        try {
            repo.findById(id).ifPresent(c -> { 
                c.setEstado("CANCELADA"); 
                repo.save(c); 
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cancelar cita: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Cita> historialPorCliente(Long clienteId){
        try {
            return repo.findByClienteId(clienteId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener historial: " + e.getMessage());
        }
    }
}