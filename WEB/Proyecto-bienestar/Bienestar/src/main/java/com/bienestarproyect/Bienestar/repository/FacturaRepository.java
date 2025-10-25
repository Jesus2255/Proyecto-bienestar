package com.bienestarproyect.Bienestar.repository;

import com.bienestarproyect.Bienestar.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // usar la propiedad cliente.id explícitamente
    List<Factura> findByCliente_IdOrderByFechaDesc(Long clienteId);
}