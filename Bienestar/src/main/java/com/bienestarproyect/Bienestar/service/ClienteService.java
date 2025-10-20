package com.bienestarproyect.Bienestar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bienestarproyect.Bienestar.entity.Cliente;
import com.bienestarproyect.Bienestar.repository.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository repo;
    public ClienteService(ClienteRepository repo){ this.repo = repo; }
    public List<Cliente> listar(){ return repo.findAll(); }
    public Cliente guardar(Cliente c){ return repo.save(c); }
    public Cliente buscar(Long id){ return repo.findById(id).orElse(null); }
    public void eliminar(Long id){ repo.deleteById(id); }
}