package com.entregaexpress.logiflow.graphqlservice.client;

import com.entregaexpress.logiflow.graphqlservice.dto.ApiResponse;
import com.entregaexpress.logiflow.graphqlservice.model.Pedido;
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
public class PedidoClient {
    
    private final WebClient webClient;
    
    public PedidoClient(WebClient.Builder webClientBuilder,
                        @Value("${services.pedido.url}") String pedidoServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(pedidoServiceUrl).build();
    }
    
    public Mono<Pedido> getPedidoById(Long id) {
        return webClient.get()
                .uri("/api/pedidos/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Pedido>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching pedido {}: {}", id, error.getMessage()));
    }
    
    public Mono<List<Pedido>> getAllPedidos() {
        return webClient.get()
                .uri("/api/pedidos")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Pedido>>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching pedidos: {}", error.getMessage()));
    }
    
    public Mono<List<Pedido>> getPedidosByCliente(Long clienteId) {
        return webClient.get()
                .uri("/api/pedidos/cliente/{clienteId}", clienteId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Pedido>>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching pedidos for cliente {}: {}", clienteId, error.getMessage()));
    }
    
    public Mono<Pedido> createPedido(Map<String, Object> input) {
        return webClient.post()
                .uri("/api/pedidos")
                .bodyValue(input)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Pedido>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error creating pedido: {}", error.getMessage()));
    }
    
    public Mono<Pedido> updateEstadoPedido(Long id, String estado, Long repartidorId) {
        Map<String, Object> request = Map.of(
            "estado", estado,
            "repartidorId", repartidorId != null ? repartidorId : 0
        );
        
        return webClient.patch()
                .uri("/api/pedidos/{id}/estado", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Pedido>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error updating pedido {} estado: {}", id, error.getMessage()));
    }
}
