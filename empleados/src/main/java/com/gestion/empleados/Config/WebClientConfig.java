package com.gestion.empleados.Config;

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

    // Puerto del microservicio de Departamentos
    @Bean
    public WebClient webClientDepartamentos(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8081/api/v1").build();
    }

    // Puerto del microservicio de Cargos
    @Bean
    public WebClient webClientCargos(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:8082/api/v1").build();
    }
}
