package com.bienestarproyect.Bienestar.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bienestarproyect.Bienestar.dto.ClienteDTO;
import com.bienestarproyect.Bienestar.entity.Cliente;
import com.bienestarproyect.Bienestar.mapper.DTOMapper;
import com.bienestarproyect.Bienestar.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service){ this.service = service; }

    @GetMapping
    public List<Cliente> listar(){ return service.listar(); }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id){ return service.buscar(id); }

    @PostMapping
    public Cliente crear(@Valid @RequestBody ClienteDTO dto){
        Cliente c = DTOMapper.toEntity(dto);
        return service.guardar(c);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto){
        Cliente c = DTOMapper.toEntity(dto);
        c.setId(id);
        return service.guardar(c);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id){ service.eliminar(id); }
}