package com.entregaexpress.logiflow.graphqlservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCliente {
    private Long clienteId;
    private Integer totalPedidos;
    private Double gastoTotal;
    private List<Pedido> pedidosActivos;
    private List<Factura> ultimasFacturas;
}
