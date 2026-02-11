package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutationResponse {
    private Boolean success;
    private String message;
    private Pedido pedido;
    private Repartidor repartidor;
    private Factura factura;
}
