package com.gestion.autenticacion.Service;

import com.gestion.autenticacion.DTO.EmpleadoDTO;
import com.gestion.autenticacion.DTO.UsuarioDTO;
import com.gestion.autenticacion.Model.Usuario;
import com.gestion.autenticacion.Repository.UsuarioRepository;
import com.gestion.autenticacion.Security.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Qualifier("webClientEmpleados")
    private WebClient webClientEmpleados;

    public List<Usuario> obtenerTodos() {
        log.info("[UsuarioService] Obteniendo lista completa de usuarios");
        List<Usuario> lista = repo.findAll();
        log.info("[UsuarioService] Se encontraron {} usuarios", lista.size());
        return lista;
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        log.info("[UsuarioService] Buscando usuario con id={}", id);
        Optional<Usuario> usuario = repo.findById(id);
        if (usuario.isPresent()) {
            log.info("[UsuarioService] Usuario encontrado: username='{}'", usuario.get().getUsername());
        } else {
            log.warn("[UsuarioService] No se encontró usuario con id={}", id);
        }
        return usuario;
    }

    public Boolean registrar(UsuarioDTO dto) {
        log.info("[UsuarioService] Registrando nuevo usuario: username='{}', empleadoId={}",
                dto.getUsername(), dto.getEmpleadoId());

        EmpleadoDTO emp;
        try {
            emp = webClientEmpleados.get()
                    .uri("/empleados/{id}", dto.getEmpleadoId())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new RuntimeException("Empleado no encontrado")))
                    .bodyToMono(EmpleadoDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("[UsuarioService] Error al consultar empleado id={}: {}", dto.getEmpleadoId(), e.getMessage());
            return false;
        }

        if (emp == null) {
            log.warn("[UsuarioService] Empleado id={} no existe, registro cancelado", dto.getEmpleadoId());
            return false;
        }

        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setRol(dto.getRol() != null ? dto.getRol() : "USER");
        u.setEmpleadoId(dto.getEmpleadoId());
        u.setEstado("ACTIVO");
        repo.save(u);

        log.info("[UsuarioService] Usuario '{}' registrado exitosamente con rol='{}'", u.getUsername(), u.getRol());
        return true;
    }

    public Optional<String> login(String username, String password) {
        log.info("[UsuarioService] Intento de login para username='{}'", username);
        Optional<Usuario> usuarioOpt = repo.findByUsernameAndPassword(username, password);

        if (usuarioOpt.isEmpty()) {
            log.warn("[UsuarioService] Login fallido para username='{}'", username);
            return Optional.empty();
        }

        Usuario u = usuarioOpt.get();
        String token = jwtUtil.generateToken(u.getUsername(), u.getRol());
        log.info("[UsuarioService] Login exitoso para username='{}', rol='{}'. JWT generado.", u.getUsername(), u.getRol());
        return Optional.of(token);
    }

    public boolean eliminar(Integer id) {
        log.info("[UsuarioService] Desactivando usuario id={}", id);
        return repo.findById(id).map(u -> {
            u.setEstado("INACTIVO");
            repo.save(u);
            log.info("[UsuarioService] Usuario id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[UsuarioService] No se encontró usuario id={} para eliminar", id);
            return false;
        });
    }
}
