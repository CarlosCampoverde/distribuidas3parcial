package com.entregaexpress.logiflow.billingservice.dto;

import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerarFacturaRequest {
    
    @NotNull
    private Long pedidoId;
    
    @NotNull
    private Long clienteId;
    
    @NotNull
    private TipoEntrega tipoEntrega;
    
    @NotNull
    @Positive
    private Double distanciaKm;
    
    @NotNull
    @Positive
    private Double pesoKg;
}
