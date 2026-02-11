package com.entregaexpress.logiflow.fleetservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.entregaexpress.logiflow.fleetservice", "com.entregaexpress.logiflow.common"})
public class FleetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetServiceApplication.class, args);
    }

}
