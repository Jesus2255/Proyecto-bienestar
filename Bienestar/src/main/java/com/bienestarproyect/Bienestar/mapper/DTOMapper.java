package com.bienestarproyect.Bienestar.mapper;

import java.math.BigDecimal;

import com.bienestarproyect.Bienestar.entity.Cita;
import com.bienestarproyect.Bienestar.entity.Cliente;
import com.bienestarproyect.Bienestar.entity.Factura;
import com.bienestarproyect.Bienestar.entity.Servicio;

public final class DTOMapper {
    private DTOMapper(){}

    public static Cliente toEntity(com.bienestarproyect.Bienestar.dto.ClienteDTO d){
        Cliente c = new Cliente();
        c.setNombre(d.getNombre());
        c.setEmail(d.getEmail());
        c.setTelefono(d.getTelefono());
        return c;
    }

    public static Factura toEntity(com.bienestarproyect.Bienestar.dto.FacturaDTO d, Cliente cliente){
        Factura f = new Factura();
        f.setCliente(cliente);
        f.setTotal(d.getTotal() != null ? d.getTotal() : BigDecimal.ZERO);
        f.setDescripcion(d.getDescripcion());
        return f;
    }

    public static Cita toEntity(com.bienestarproyect.Bienestar.dto.CitaDTO d, Cliente cliente, Servicio servicio){
        Cita c = new Cita();
        c.setCliente(cliente);
        c.setServicio(servicio);
        c.setFechaHora(d.getFechaHora());
        c.setEstado("AGENDADA");
        return c;
    }
}