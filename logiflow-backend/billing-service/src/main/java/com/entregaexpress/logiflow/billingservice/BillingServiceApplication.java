package com.entregaexpress.logiflow.billingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.entregaexpress.logiflow.billingservice", "com.entregaexpress.logiflow.common"})
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

}
