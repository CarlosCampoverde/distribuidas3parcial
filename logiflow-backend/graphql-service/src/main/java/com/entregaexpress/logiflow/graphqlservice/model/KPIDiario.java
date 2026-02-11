package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIDiario {
    private String fecha;
    private Integer totalPedidos;
    private Integer pedidosEntregados;
    private Integer pedidosCancelados;
    private Double facturacionTotal;
    private Double tiempoPromedioEntrega;
}
