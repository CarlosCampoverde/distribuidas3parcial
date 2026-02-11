package com.entregaexpress.logiflow.billingservice.dto;

import com.entregaexpress.logiflow.common.enums.EstadoFactura;
import com.entregaexpress.logiflow.common.enums.TipoEntrega;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {
    private Long id;
    private String numeroFactura;
    private Long pedidoId;
    private Long clienteId;
    private TipoEntrega tipoEntrega;
    private Double distanciaKm;
    private Double pesoKg;
    private Double subtotal;
    private Double impuestos;
    private Double total;
    private EstadoFactura estado;
    private LocalDateTime fechaCreacion;
}
