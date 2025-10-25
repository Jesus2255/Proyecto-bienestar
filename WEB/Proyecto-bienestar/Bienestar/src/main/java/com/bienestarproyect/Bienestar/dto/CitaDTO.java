package com.bienestarproyect.Bienestar.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CitaDTO {
    @NotNull(message = "clienteId obligatorio")
    private Long clienteId;

    @NotNull(message = "servicioId obligatorio")
    private Long servicioId;

    @NotNull(message = "fechaHora obligatoria")
    @Future(message = "fechaHora debe ser futura")
    private LocalDateTime fechaHora;

    public CitaDTO() {}
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId = servicioId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}