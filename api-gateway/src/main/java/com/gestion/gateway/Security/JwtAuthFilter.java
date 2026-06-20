package com.gestion.gateway.Security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    // Rutas públicas que no requieren JWT
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/autenticacion/login",
            "/api/v1/autenticacion/registrar"
    );

    // GETs públicos: servicios de referencia accedidos inter-servicio sin token
    private static final List<String> PUBLIC_GET_PREFIXES = List.of(
            "/api/v1/departamentos",   // Int1: consultado por empleados
            "/api/v1/cargos",          // Int1: consultado por empleados
            "/api/v1/empleados",       // Int1: consultado por ventas (Int3)
            "/api/v1/clientes"         // Int3: consultado por ordenescompra (Int2)
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "";

        log.info("[API-GATEWAY] --> {} {}", method, path);

        // Rutas completamente públicas
        if (isPublicPath(path)) {
            log.info("[API-GATEWAY] Ruta pública, sin validación JWT: {}", path);
            return chain.filter(exchange);
        }

        // GETs públicos en servicios de referencia
        if (HttpMethod.GET.matches(method) && isPublicGetPrefix(path)) {
            log.info("[API-GATEWAY] GET público en ruta de referencia: {}", path);
            return chain.filter(exchange);
        }

        // Validar JWT
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[API-GATEWAY] Token JWT ausente en: {} {}", method, path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            log.warn("[API-GATEWAY] Token JWT inválido para: {} {}", method, path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = jwtUtil.extractUsername(token);
        log.info("[API-GATEWAY] Token válido. Usuario='{}' accede a {} {}", username, method, path);

        // Propagar el usuario en header para que los microservicios lo reciban
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Auth-User", username)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() ->
                        log.info("[API-GATEWAY] <-- {} {} completado (status={})",
                                method, path, exchange.getResponse().getStatusCode())
                ));
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isPublicGetPrefix(String path) {
        return PUBLIC_GET_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
