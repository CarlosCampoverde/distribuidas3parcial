package com.entregaexpress.logiflow.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global para logging centralizado
 * Registra método, URI, código de respuesta y userId si está disponible
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        long startTime = System.currentTimeMillis();
        String method = request.getMethod().toString();
        String uri = request.getURI().toString();
        String userId = request.getHeaders().getFirst("X-User-Id");
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("Request: {} {} | Status: {} | Duration: {}ms | User: {}",
                    method, uri, response.getStatusCode(), duration, 
                    userId != null ? userId : "anonymous");
        }));
    }
    
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
