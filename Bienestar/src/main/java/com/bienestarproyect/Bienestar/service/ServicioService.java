package com.bienestarproyect.Bienestar.service;

import com.bienestarproyect.Bienestar.entity.Servicio;
import com.bienestarproyect.Bienestar.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {
    private final ServicioRepository repo;
    public ServicioService(ServicioRepository repo){ this.repo = repo; }
    public List<Servicio> listar(){ return repo.findAll(); }
    public Servicio guardar(Servicio s){ return repo.save(s); }
    public Servicio buscar(Long id){ return repo.findById(id).orElse(null); }
    public void eliminar(Long id){ repo.deleteById(id); }
}