package com.entregaexpress.logiflow.fleetservice.event;

import com.entregaexpress.logiflow.common.event.UbicacionRepartidorEvent;
import com.entregaexpress.logiflow.fleetservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FleetEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishUbicacionActualizada(UbicacionRepartidorEvent event) {
        try {
            log.info("Publishing ubicacion event for repartidor: {}", event.getRepartidorId());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FLEET_EXCHANGE,
                    RabbitMQConfig.UBICACION_ROUTING_KEY,
                    event
            );
            
            log.info("Ubicacion event published successfully for repartidor: {}", 
                    event.getNombreCompleto());
        } catch (Exception e) {
            log.error("Error publishing ubicacion event", e);
        }
    }
}
