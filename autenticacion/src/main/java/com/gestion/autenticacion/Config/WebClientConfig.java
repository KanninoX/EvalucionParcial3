package com.gestion.autenticacion.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 1. Definimos el Builder primero
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Puerto del microservicio de Empleados
    @Bean
    public WebClient webClientEmpleados(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080/api/v1").build();
    }
}
