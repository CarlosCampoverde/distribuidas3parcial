package com.entregaexpress.logiflow.gateway.config;

import com.entregaexpress.logiflow.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas del API Gateway
 * Define enrutamiento a microservicios con filtros de autenticación y rate limiting
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    
    private final JwtAuthenticationFilter jwtFilter;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .filter(jwtFilter))
                        .uri("http://localhost:8081"))
                
                // Pedido Service
                .route("pedido-service", r -> r
                        .path("/api/pedidos/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .filter(jwtFilter))
                        .uri("http://localhost:8082"))
                
                // Fleet Service
                .route("fleet-service", r -> r
                        .path("/api/repartidores/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .filter(jwtFilter))
                        .uri("http://localhost:8083"))
                
                // Billing Service
                .route("billing-service", r -> r
                        .path("/api/facturas/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .filter(jwtFilter))
                        .uri("http://localhost:8084"))
                
                // GraphQL Service
                .route("graphql-service", r -> r
                        .path("/graphql/**", "/graphiql/**")
                        .filters(f -> f
                                .stripPrefix(0))
                        .uri("http://localhost:8085"))
                
                // Swagger UI de cada servicio
                .route("auth-docs", r -> r
                        .path("/auth/swagger-ui/**", "/auth/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                
                .route("pedido-docs", r -> r
                        .path("/pedido/swagger-ui/**", "/pedido/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))
                
                .route("fleet-docs", r -> r
                        .path("/fleet/swagger-ui/**", "/fleet/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))
                
                .route("billing-docs", r -> r
                        .path("/billing/swagger-ui/**", "/billing/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8084"))
                
                .route("graphql-docs", r -> r
                        .path("/graphql-service/swagger-ui/**", "/graphql-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8085"))
                
                .build();
    }
}
