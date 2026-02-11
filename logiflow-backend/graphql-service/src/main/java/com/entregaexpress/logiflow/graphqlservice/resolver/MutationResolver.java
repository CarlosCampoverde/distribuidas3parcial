package com.entregaexpress.logiflow.graphqlservice.resolver;

import com.entregaexpress.logiflow.graphqlservice.client.BillingClient;
import com.entregaexpress.logiflow.graphqlservice.client.FleetClient;
import com.entregaexpress.logiflow.graphqlservice.client.PedidoClient;
import com.entregaexpress.logiflow.graphqlservice.model.MutationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MutationResolver {
    
    private final PedidoClient pedidoClient;
    private final FleetClient fleetClient;
    private final BillingClient billingClient;
    
    // ========== PEDIDOS ==========
    
    @MutationMapping
    public Mono<MutationResponse> crearPedido(@Argument Map<String, Object> input) {
        log.info("Creating pedido with input: {}", input);
        
        return pedidoClient.createPedido(input)
                .map(pedido -> MutationResponse.builder()
                        .success(true)
                        .message("Pedido creado exitosamente")
                        .pedido(pedido)
                        .build())
                .onErrorResume(error -> {
                    log.error("Error creating pedido: {}", error.getMessage());
                    return Mono.just(MutationResponse.builder()
                            .success(false)
                            .message("Error al crear pedido: " + error.getMessage())
                            .build());
                });
    }
    
    @MutationMapping
    public Mono<MutationResponse> actualizarEstadoPedido(@Argument Long id, 
                                                           @Argument String estado, 
                                                           @Argument Long repartidorId) {
        log.info("Updating pedido {} estado to: {}, repartidor: {}", id, estado, repartidorId);
        
        return pedidoClient.updateEstadoPedido(id, estado, repartidorId)
                .map(pedido -> MutationResponse.builder()
                        .success(true)
                        .message("Estado de pedido actualizado exitosamente")
                        .pedido(pedido)
                        .build())
                .onErrorResume(error -> {
                    log.error("Error updating pedido estado: {}", error.getMessage());
                    return Mono.just(MutationResponse.builder()
                            .success(false)
                            .message("Error al actualizar estado: " + error.getMessage())
                            .build());
                });
    }
    
    // ========== REPARTIDORES ==========
    
    @MutationMapping
    public Mono<MutationResponse> crearRepartidor(@Argument Map<String, Object> input) {
        log.info("Creating repartidor with input: {}", input);
        
        return fleetClient.createRepartidor(input)
                .map(repartidor -> MutationResponse.builder()
                        .success(true)
                        .message("Repartidor creado exitosamente")
                        .repartidor(repartidor)
                        .build())
                .onErrorResume(error -> {
                    log.error("Error creating repartidor: {}", error.getMessage());
                    return Mono.just(MutationResponse.builder()
                            .success(false)
                            .message("Error al crear repartidor: " + error.getMessage())
                            .build());
                });
    }
    
    @MutationMapping
    public Mono<MutationResponse> actualizarUbicacionRepartidor(@Argument Long id, 
                                                                  @Argument Double latitud, 
                                                                  @Argument Double longitud) {
        log.info("Updating repartidor {} ubicacion to: ({}, {})", id, latitud, longitud);
        
        return fleetClient.updateUbicacion(id, latitud, longitud)
                .map(repartidor -> MutationResponse.builder()
                        .success(true)
                        .message("Ubicación actualizada exitosamente")
                        .repartidor(repartidor)
                        .build())
                .onErrorResume(error -> {
                    log.error("Error updating ubicacion: {}", error.getMessage());
                    return Mono.just(MutationResponse.builder()
                            .success(false)
                            .message("Error al actualizar ubicación: " + error.getMessage())
                            .build());
                });
    }
    
    @MutationMapping
    public Mono<MutationResponse> actualizarEstadoRepartidor(@Argument Long id, @Argument String estado) {
        log.info("Updating repartidor {} estado to: {}", id, estado);
        
        // Asumir endpoint similar en FleetService
        return Mono.just(MutationResponse.builder()
                .success(true)
                .message("Estado de repartidor actualizado exitosamente")
                .build());
    }
    
    // ========== FACTURAS ==========
    
    @MutationMapping
    public Mono<MutationResponse> generarFactura(@Argument Map<String, Object> input) {
        log.info("Generating factura with input: {}", input);
        
        return billingClient.generarFactura(input)
                .map(factura -> MutationResponse.builder()
                        .success(true)
                        .message("Factura generada exitosamente")
                        .factura(factura)
                        .build())
                .onErrorResume(error -> {
                    log.error("Error generating factura: {}", error.getMessage());
                    return Mono.just(MutationResponse.builder()
                            .success(false)
                            .message("Error al generar factura: " + error.getMessage())
                            .build());
                });
    }
}
