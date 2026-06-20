package com.gestion.ordenescompra.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class ClienteClient {

    private static final Logger log = LoggerFactory.getLogger(ClienteClient.class);

    private final WebClient webClient;

    public ClienteClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public Map<String, Object> obtenerCliente(Long clienteId) {
        try {
            log.info("[ClienteClient] Consultando cliente id={} en servicio Clientes (Int3)", clienteId);
            @SuppressWarnings("unchecked")
            Map<String, Object> cliente = webClient.get()
                    .uri("/api/v1/clientes/{id}", clienteId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            log.info("[ClienteClient] Cliente id={} obtenido correctamente", clienteId);
            return cliente;
        } catch (Exception e) {
            log.warn("[ClienteClient] No se pudo obtener cliente id={}: {}", clienteId, e.getMessage());
            return null;
        }
    }
}
