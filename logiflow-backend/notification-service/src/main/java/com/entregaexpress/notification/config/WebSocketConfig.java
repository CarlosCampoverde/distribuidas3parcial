package com.entregaexpress.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration with STOMP
 * Configures endpoints for real-time bidirectional communication
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker
        // Messages sent to /topic will be broadcast to all subscribers
        config.enableSimpleBroker("/topic");
        
        // Messages prefixed with /app will be routed to @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the /ws endpoint for WebSocket connections
        // withSockJS() enables SockJS fallback options
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
