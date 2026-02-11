package com.entregaexpress.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Notification Service - RabbitMQ Consumer and WebSocket Server
 * Listens to events from Pedido and Fleet services and broadcasts via WebSocket
 */
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
