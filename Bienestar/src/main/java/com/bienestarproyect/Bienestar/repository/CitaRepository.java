package com.bienestarproyect.Bienestar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bienestarproyect.Bienestar.entity.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByCliente_IdOrderByFechaHoraDesc(Long clienteId);
}