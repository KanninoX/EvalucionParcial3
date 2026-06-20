package com.Operaciones.ventas.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class EmpleadoClient {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoClient.class);

    private final WebClient webClient;

    public EmpleadoClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    public Map<String, Object> obtenerEmpleado(Long empleadoId) {
        try {
            log.info("[EmpleadoClient] Consultando empleado id={}", empleadoId);
            @SuppressWarnings("unchecked")
            Map<String, Object> empleado = webClient.get()
                    .uri("/api/v1/empleados/{id}", empleadoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            log.info("[EmpleadoClient] Empleado id={} obtenido correctamente", empleadoId);
            return empleado;
        } catch (Exception e) {
            log.warn("[EmpleadoClient] No se pudo obtener empleado id={}: {}", empleadoId, e.getMessage());
            return null;
        }
    }
}
