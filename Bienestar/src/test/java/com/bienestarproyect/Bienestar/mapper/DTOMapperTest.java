package com.bienestarproyect.Bienestar.mapper;

import com.bienestarproyect.Bienestar.dto.*;
import com.bienestarproyect.Bienestar.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DTOMapperTest {

    @Test
    public void clienteDtoToEntity() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Juan");
        dto.setEmail("juan@example.com");
        dto.setTelefono("12345678");

        Cliente c = DTOMapper.toEntity(dto);
        assertEquals("Juan", c.getNombre());
        assertEquals("juan@example.com", c.getEmail());
        assertEquals("12345678", c.getTelefono());
    }

    @Test
    public void citaDtoToEntity() {
        CitaDTO dto = new CitaDTO();
        dto.setClienteId(1L);
        dto.setServicioId(2L);
        dto.setFechaHora(LocalDateTime.now().plusDays(1));

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Servicio servicio = new Servicio();
        servicio.setId(2L);

        Cita cita = DTOMapper.toEntity(dto, cliente, servicio);
        assertEquals(cliente, cita.getCliente());
        assertEquals(servicio, cita.getServicio());
        assertEquals("AGENDADA", cita.getEstado());
        assertNotNull(cita.getFechaHora());
    }

    @Test
    public void facturaDtoToEntity() {
        FacturaDTO dto = new FacturaDTO();
        dto.setClienteId(1L);
        dto.setTotal(new BigDecimal("50.00"));
        dto.setDescripcion("Masaje completo");

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Factura f = DTOMapper.toEntity(dto, cliente);
        assertEquals(cliente, f.getCliente());
        assertEquals(new BigDecimal("50.00"), f.getTotal());
        assertEquals("Masaje completo", f.getDescripcion());
    }
}