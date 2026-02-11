package com.entregaexpress.logiflow.graphqlservice.client;

import com.entregaexpress.logiflow.graphqlservice.dto.ApiResponse;
import com.entregaexpress.logiflow.graphqlservice.model.Repartidor;
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
public class FleetClient {
    
    private final WebClient webClient;
    
    public FleetClient(WebClient.Builder webClientBuilder,
                       @Value("${services.fleet.url}") String fleetServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(fleetServiceUrl).build();
    }
    
    public Mono<Repartidor> getRepartidorById(Long id) {
        return webClient.get()
                .uri("/api/repartidores/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Repartidor>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching repartidor {}: {}", id, error.getMessage()));
    }
    
    public Mono<List<Repartidor>> getAllRepartidores() {
        return webClient.get()
                .uri("/api/repartidores")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Repartidor>>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching repartidores: {}", error.getMessage()));
    }
    
    public Mono<List<Repartidor>> getRepartidoresDisponibles() {
        return webClient.get()
                .uri("/api/repartidores/disponibles")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Repartidor>>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error fetching repartidores disponibles: {}", error.getMessage()));
    }
    
    public Mono<Repartidor> createRepartidor(Map<String, Object> input) {
        return webClient.post()
                .uri("/api/repartidores")
                .bodyValue(input)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Repartidor>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error creating repartidor: {}", error.getMessage()));
    }
    
    public Mono<Repartidor> updateUbicacion(Long id, Double latitud, Double longitud) {
        Map<String, Object> ubicacion = Map.of(
            "latitud", latitud,
            "longitud", longitud
        );
        
        return webClient.patch()
                .uri("/api/repartidores/{id}/ubicacion", id)
                .bodyValue(ubicacion)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Repartidor>>() {})
                .map(ApiResponse::getData)
                .doOnError(error -> log.error("Error updating repartidor {} ubicacion: {}", id, error.getMessage()));
    }
}
