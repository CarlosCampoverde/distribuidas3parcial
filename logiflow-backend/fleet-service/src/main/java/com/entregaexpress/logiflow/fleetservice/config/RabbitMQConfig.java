package com.entregaexpress.logiflow.fleetservice.config;

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
    public static final String FLEET_EXCHANGE = "logiflow.fleet.exchange";
    
    // Queues
    public static final String UBICACION_QUEUE = "logiflow.ubicacion.queue";
    
    // Routing Keys
    public static final String UBICACION_ROUTING_KEY = "repartidor.ubicacion.updated";
    
    @Bean
    public TopicExchange fleetExchange() {
        return new TopicExchange(FLEET_EXCHANGE);
    }
    
    @Bean
    public Queue ubicacionQueue() {
        return QueueBuilder.durable(UBICACION_QUEUE)
                .build();
    }
    
    @Bean
    public Binding ubicacionBinding(Queue ubicacionQueue, TopicExchange fleetExchange) {
        return BindingBuilder
                .bind(ubicacionQueue)
                .to(fleetExchange)
                .with(UBICACION_ROUTING_KEY);
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
