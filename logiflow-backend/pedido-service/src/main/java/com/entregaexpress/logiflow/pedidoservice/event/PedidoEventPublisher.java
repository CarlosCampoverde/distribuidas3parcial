package com.entregaexpress.logiflow.pedidoservice.event;

import com.entregaexpress.logiflow.common.event.PedidoEstadoEvent;
import com.entregaexpress.logiflow.pedidoservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishPedidoEstadoCambiado(PedidoEstadoEvent event) {
        try {
            log.info("Publishing pedido estado event: {} -> {}", 
                    event.getEstadoAnterior(), event.getEstadoNuevo());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PEDIDO_EXCHANGE,
                    RabbitMQConfig.PEDIDO_ESTADO_ROUTING_KEY,
                    event
            );
            
            log.info("Pedido estado event published successfully for pedido: {}", 
                    event.getCodigoPedido());
        } catch (Exception e) {
            log.error("Error publishing pedido estado event", e);
        }
    }
}
