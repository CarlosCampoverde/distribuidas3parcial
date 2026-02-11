package com.entregaexpress.logiflow.pedidoservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Exchanges
    public static final String PEDIDO_EXCHANGE = "logiflow.pedido.exchange";
    
    // Queues
    public static final String PEDIDO_ESTADO_QUEUE = "logiflow.pedido.estado.queue";
    
    // Routing Keys
    public static final String PEDIDO_ESTADO_ROUTING_KEY = "pedido.estado.changed";
    
    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(PEDIDO_EXCHANGE);
    }
    
    @Bean
    public Queue pedidoEstadoQueue() {
        return QueueBuilder.durable(PEDIDO_ESTADO_QUEUE)
                .build();
    }
    
    @Bean
    public Binding pedidoEstadoBinding(Queue pedidoEstadoQueue, TopicExchange pedidoExchange) {
        return BindingBuilder
                .bind(pedidoEstadoQueue)
                .to(pedidoExchange)
                .with(PEDIDO_ESTADO_ROUTING_KEY);
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
