package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    private Long id;
    private String numeroFactura;
    private Long pedidoId;
    private Long clienteId;
    private Double subtotal;
    private Double impuestos;
    private Double total;
    private String estado;
    private String fechaCreacion;
    private String fechaPago;
}
