package com.bienestarproyect.Bienestar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FacturaDTO {
    @NotNull(message = "clienteId obligatorio")
    private Long clienteId;

    @NotNull(message = "total obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "total debe ser mayor a 0")
    private BigDecimal total;

    private String descripcion;

    public FacturaDTO() {}
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}