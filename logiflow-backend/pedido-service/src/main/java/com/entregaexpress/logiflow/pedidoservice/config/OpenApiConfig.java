package com.entregaexpress.logiflow.pedidoservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiFlow Pedido Service API")
                        .version("1.0.0")
                        .description("Order Management Service with delivery types hierarchy")
                        .contact(new Contact()
                                .name("EntregaExpress S.A.")
                                .email("dev@entregaexpress.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
