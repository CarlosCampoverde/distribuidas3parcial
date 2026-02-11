package com.entregaexpress.notification.consumer;

import com.entregaexpress.logiflow.common.event.PedidoEstadoEvent;
import com.entregaexpress.logiflow.common.event.UbicacionRepartidorEvent;
import com.entregaexpress.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ Event Consumer
 * Listens to events from Pedido and Fleet services and broadcasts them via WebSocket
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final NotificationService notificationService;

    /**
     * Consume PedidoEstadoEvent from logiflow.pedido.estado.queue
     * Triggered when a pedido status changes (PENDIENTE -> ASIGNADO -> EN_CAMINO, etc.)
     */
    @RabbitListener(queues = "logiflow.pedido.estado.queue")
    public void consumePedidoEstadoEvent(PedidoEstadoEvent event) {
        log.info("🔔 Received PedidoEstadoEvent: Pedido {} changed from {} to {}", 
                 event.getCodigoPedido(), 
                 event.getEstadoAnterior(), 
                 event.getEstadoNuevo());
        
        // Broadcast to all WebSocket clients
        notificationService.broadcastPedidoEstado(event);
    }

    /**
     * Consume UbicacionRepartidorEvent from logiflow.ubicacion.queue
     * Triggered when a repartidor's GPS location is updated
     */
    @RabbitListener(queues = "logiflow.ubicacion.queue")
    public void consumeUbicacionRepartidorEvent(UbicacionRepartidorEvent event) {
        log.info("📍 Received UbicacionRepartidorEvent: {} at ({}, {})", 
                 event.getNombreCompleto(),
                 event.getLatitud(), 
                 event.getLongitud());
        
        // Broadcast to all WebSocket clients
        notificationService.broadcastUbicacionRepartidor(event);
    }
}
