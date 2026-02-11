package com.entregaexpress.logiflow.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

/**
 * Filtro para validación de JWT
 * Valida tokens en todas las rutas protegidas
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter {
    
    @Value("${jwt.secret:logiflow-secret-key-super-secure-change-in-production-2024}")
    private String secret;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Rutas públicas que no requieren autenticación
        if (isPublicPath(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        // Extraer token del header Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Token no proporcionado");
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Validar y extraer claims
            Claims claims = validateToken(token);
            
            // Agregar información del usuario al request
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .build();
            
            log.info("Request autenticado - Usuario: {}, Role: {}, Path: {}", 
                    claims.getSubject(), claims.get("role"), request.getPath());
            
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
            
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }
    }
    
    private boolean isPublicPath(String path) {
        return path.contains("/api/auth/") 
                || path.contains("/swagger-ui") 
                || path.contains("/v3/api-docs")
                || path.contains("/actuator");
    }
    
    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("Error de autenticación: {}", message);
        return response.setComplete();
    }
}
