package com.entregaexpress.logiflow.graphqlservice.client;

import com.entregaexpress.logiflow.graphqlservice.dto.ApiResponse;
import com.entregaexpress.logiflow.graphqlservice.model.Factura;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BillingClient {
    
    private final WebClient webClient;
    
    public BillingClient(WebClient.Builder webClientBuilder,
                         @Value("${services.billing.url}") String billingServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(billingServiceUrl).build();
    }
    
    public Mono<Factura> getFacturaById(Long id) {
        return webClient.get()
                .uri("/api/facturas/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Factura>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching factura {}: {}", id, error.getMessage()));
    }
    
    public Mono<Factura> getFacturaByPedido(Long pedidoId) {
        return webClient.get()
                .uri("/api/facturas/pedido/{pedidoId}", pedidoId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Factura>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching factura for pedido {}: {}", pedidoId, error.getMessage()));
    }
    
    public Mono<List<Factura>> getFacturasByCliente(Long clienteId) {
        return webClient.get()
                .uri("/api/facturas/cliente/{clienteId}", clienteId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Factura>>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching facturas for cliente {}: {}", clienteId, error.getMessage()));
    }
    
    public Mono<Factura> generarFactura(Map<String, Object> input) {
        return webClient.post()
                .uri("/api/facturas")
                .bodyValue(input)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Factura>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error generating factura: {}", error.getMessage()));
    }
}
