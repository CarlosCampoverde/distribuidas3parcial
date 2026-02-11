package com.entregaexpress.logiflow.pedidoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.entregaexpress.logiflow.pedidoservice", "com.entregaexpress.logiflow.common"})
public class PedidoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidoServiceApplication.class, args);
    }

}
