package com.entregaexpress.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Notification Service - Broadcasts events to WebSocket clients
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast pedido status change to all WebSocket clients subscribed to /topic/pedidos
     */
    public void broadcastPedidoEstado(Object event) {
        log.info("📡 Broadcasting pedido estado event to /topic/pedidos");
        messagingTemplate.convertAndSend("/topic/pedidos", event);
    }

    /**
     * Broadcast repartidor location update to all WebSocket clients subscribed to /topic/ubicaciones
     */
    public void broadcastUbicacionRepartidor(Object event) {
        log.info("📍 Broadcasting ubicacion event to /topic/ubicaciones");
        messagingTemplate.convertAndSend("/topic/ubicaciones", event);
    }

    /**
     * Broadcast to a specific client (if needed for future features)
     */
    public void sendToUser(String username, String destination, Object payload) {
        log.info("📤 Sending message to user: {} at {}", username, destination);
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }
}
