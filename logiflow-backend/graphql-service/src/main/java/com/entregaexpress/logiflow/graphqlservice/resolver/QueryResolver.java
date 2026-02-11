package com.entregaexpress.logiflow.graphqlservice.resolver;

import com.entregaexpress.logiflow.graphqlservice.client.BillingClient;
import com.entregaexpress.logiflow.graphqlservice.client.FleetClient;
import com.entregaexpress.logiflow.graphqlservice.client.PedidoClient;
import com.entregaexpress.logiflow.graphqlservice.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QueryResolver {
    
    private final PedidoClient pedidoClient;
    private final FleetClient fleetClient;
    private final BillingClient billingClient;
    
    // ========== PEDIDOS ==========
    
   @QueryMapping
    public Mono<Pedido> pedido(@Argument Long id) {
        log.info("Fetching pedido with id: {}", id);
        return pedidoClient.getPedidoById(id);
    }
    
    @QueryMapping
    public Mono<List<Pedido>> pedidos(@Argument Map<String, Object> filtro, 
                                       @Argument Integer limit, 
                                       @Argument Integer offset) {
        log.info("Fetching pedidos with filter: {}, limit: {}, offset: {}", filtro, limit, offset);
        return pedidoClient.getAllPedidos()
                .map(pedidos -> {
                    // Aplicar filtros si es necesario
                    if (filtro != null && filtro.containsKey("estado")) {
                        String estado = (String) filtro.get("estado");
                        pedidos = pedidos.stream()
                                .filter(p -> p.getEstado().equals(estado))
                                .collect(Collectors.toList());
                    }
                    
                    // Aplicar paginación
                    int start = offset != null ? offset : 0;
                    int end = limit != null ? Math.min(start + limit, pedidos.size()) : pedidos.size();
                    
                    return pedidos.subList(start, Math.min(end, pedidos.size()));
                });
    }
    
    @QueryMapping
    public Mono<PedidoCompleto> pedidoCompleto(@Argument Long id) {
        log.info("Fetching complete pedido with id: {}", id);
        
        return pedidoClient.getPedidoById(id)
                .flatMap(pedido -> {
                    // Fetch repartidor if assigned
                    Mono<Repartidor> repartidorMono = pedido.getRepartidorId() != null ?
                            fleetClient.getRepartidorById(pedido.getRepartidorId())
                                    .onErrorReturn(new Repartidor()) :
                            Mono.just(new Repartidor());
                    
                    // Fetch factura
                    Mono<Factura> facturaMono = billingClient.getFacturaByPedido(pedido.getId())
                            .onErrorReturn(new Factura());
                    
                    // Combine all data
                    return Mono.zip(Mono.just(pedido), repartidorMono, facturaMono)
                            .map(tuple -> PedidoCompleto.builder()
                                    .pedido(tuple.getT1())
                                    .repartidor(tuple.getT2().getId() != null ? tuple.getT2() : null)
                                    .factura(tuple.getT3().getId() != null ? tuple.getT3() : null)
                                    .build());
                });
    }
    
    @QueryMapping
    public Mono<DashboardCliente> dashboardCliente(@Argument Long clienteId) {
        log.info("Fetching dashboard for cliente: {}", clienteId);
        
        Mono<List<Pedido>> pedidosMono = pedidoClient.getPedidosByCliente(clienteId);
        Mono<List<Factura>> facturasMono = billingClient.getFacturasByCliente(clienteId);
        
        return Mono.zip(pedidosMono, facturasMono)
                .map(tuple -> {
                    List<Pedido> pedidos = tuple.getT1();
                    List<Factura> facturas = tuple.getT2();
                    
                    List<Pedido> pedidosActivos = pedidos.stream()
                            .filter(p -> !p.getEstado().equals("ENTREGADO") && !p.getEstado().equals("CANCELADO"))
                            .collect(Collectors.toList());
                    
                    Double gastoTotal = facturas.stream()
                            .filter(f -> f.getEstado().equals("PAGADA"))
                            .mapToDouble(Factura::getTotal)
                            .sum();
                    
                    List<Factura> ultimasFacturas = facturas.stream()
                            .limit(5)
                            .collect(Collectors.toList());
                    
                    return DashboardCliente.builder()
                            .clienteId(clienteId)
                            .totalPedidos(pedidos.size())
                            .gastoTotal(gastoTotal)
                            .pedidosActivos(pedidosActivos)
                            .ultimasFacturas(ultimasFacturas)
                            .build();
                });
    }
    
    // ========== REPARTIDORES ==========
    
    @QueryMapping
    public Mono<Repartidor> repartidor(@Argument Long id) {
        log.info("Fetching repartidor with id: {}", id);
        return fleetClient.getRepartidorById(id);
    }
    
    @QueryMapping
    public Mono<List<Repartidor>> repartidores(@Argument String estado) {
        log.info("Fetching repartidores with estado: {}", estado);
        return fleetClient.getAllRepartidores()
                .map(repartidores -> {
                    if (estado != null) {
                        return repartidores.stream()
                                .filter(r -> r.getEstado().equals(estado))
                                .collect(Collectors.toList());
                    }
                    return repartidores;
                });
    }
    
    @QueryMapping
    public Mono<List<Repartidor>> repartidoresDisponibles() {
        log.info("Fetching available repartidores");
        return fleetClient.getRepartidoresDisponibles();
    }
    
    // ========== FLOTA Y KPI ==========
    
    @QueryMapping
    public Mono<FlotaResumen> flotaActiva(@Argument Long zonaId) {
        log.info("Fetching flota resumen for zona: {}", zonaId);
        
        return fleetClient.getAllRepartidores()
                .map(repartidores -> {
                    int total = repartidores.size();
                    long disponibles = repartidores.stream()
                            .filter(r -> r.getEstado().equals("DISPONIBLE"))
                            .count();
                    long enRuta = repartidores.stream()
                            .filter(r -> r.getEstado().equals("EN_RUTA"))
                            .count();
                    long mantenimiento = repartidores.stream()
                            .filter(r -> r.getEstado().equals("MANTENIMIENTO"))
                            .count();
                    
                    return FlotaResumen.builder()
                            .total(total)
                            .disponibles((int) disponibles)
                            .enRuta((int) enRuta)
                            .mantenimiento((int) mantenimiento)
                            .build();
                });
    }
    
    @QueryMapping
    public Mono<KPIDiario> kpiDiario(@Argument String fecha, @Argument Long zonaId) {
        log.info("Fetching KPI for fecha: {}, zona: {}", fecha, zonaId);
        
        return pedidoClient.getAllPedidos()
                .flatMap(pedidos -> {
                    // Filter by date logic here
                    long totalPedidos = pedidos.size();
                    long entregados = pedidos.stream()
                            .filter(p -> p.getEstado().equals("ENTREGADO"))
                            .count();
                    long cancelados = pedidos.stream()
                            .filter(p -> p.getEstado().equals("CANCELADO"))
                            .count();
                    
                    // Calcular facturación total
                    return billingClient.getFacturaById(1L) // Placeholder
                            .map(factura -> KPIDiario.builder()
                                    .fecha(fecha)
                                    .totalPedidos((int) totalPedidos)
                                    .pedidosEntregados((int) entregados)
                                    .pedidosCancelados((int) cancelados)
                                    .facturacionTotal(0.0) // Calcular real
                                    .tiempoPromedioEntrega(30.5) // Calcular real
                                    .build())
                            .onErrorReturn(KPIDiario.builder()
                                    .fecha(fecha)
                                    .totalPedidos((int) totalPedidos)
                                    .pedidosEntregados((int) entregados)
                                    .pedidosCancelados((int) cancelados)
                                    .facturacionTotal(0.0)
                                    .tiempoPromedioEntrega(0.0)
                                    .build());
                });
    }
    
    // ========== FACTURAS ==========
    
    @QueryMapping
    public Mono<Factura> factura(@Argument Long id) {
        log.info("Fetching factura with id: {}", id);
        return billingClient.getFacturaById(id);
    }
    
    @QueryMapping
    public Mono<List<Factura>> facturasPorCliente(@Argument Long clienteId) {
        log.info("Fetching facturas for cliente: {}", clienteId);
        return billingClient.getFacturasByCliente(clienteId);
    }
    
    @QueryMapping
    public Mono<Factura> facturasPorPedido(@Argument Long pedidoId) {
        log.info("Fetching factura for pedido: {}", pedidoId);
        return billingClient.getFacturaByPedido(pedidoId);
    }
}
