package com.bienestarproyect.Bienestar.repository;

import com.bienestarproyect.Bienestar.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}