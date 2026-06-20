package com.gestion.autenticacion.Controller;

import com.gestion.autenticacion.Assemblers.UsuarioModelAssembler;
import com.gestion.autenticacion.DTO.UsuarioDTO;
import com.gestion.autenticacion.Model.Usuario;
import com.gestion.autenticacion.Service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/autenticacion")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioModelAssembler assembler;

    @GetMapping("/usuarios")
    public CollectionModel<EntityModel<Usuario>> listar() {
        log.info("[UsuarioController] GET /usuarios - Iniciando listado de usuarios");
        List<EntityModel<Usuario>> usuarios = service.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[UsuarioController] GET /usuarios - Retornando {} usuarios", usuarios.size());
        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listar()).withSelfRel());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtener(@PathVariable Integer id) {
        log.info("[UsuarioController] GET /usuarios/{} - Buscando usuario", id);
        return service.obtenerPorId(id)
                .map(u -> {
                    log.info("[UsuarioController] GET /usuarios/{} - Usuario encontrado: '{}'", id, u.getUsername());
                    return ResponseEntity.ok(assembler.toModel(u));
                })
                .orElseGet(() -> {
                    log.warn("[UsuarioController] GET /usuarios/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody UsuarioDTO dto) {
        log.info("[UsuarioController] POST /registrar - Registrando usuario '{}'", dto.getUsername());
        Boolean save = service.registrar(dto);
        if (!save) {
            log.warn("[UsuarioController] POST /registrar - Falló registro de '{}'", dto.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Empleado no existe en su microservicio.");
        }
        log.info("[UsuarioController] POST /registrar - Usuario '{}' registrado exitosamente", dto.getUsername());
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UsuarioDTO dto) {
        log.info("[UsuarioController] POST /login - Intento de login para username='{}'", dto.getUsername());
        return service.login(dto.getUsername(), dto.getPassword())
                .map(token -> {
                    log.info("[UsuarioController] POST /login - Login exitoso para '{}'", dto.getUsername());
                    return ResponseEntity.ok(Map.of("token", token, "tipo", "Bearer"));
                })
                .orElseGet(() -> {
                    log.warn("[UsuarioController] POST /login - Credenciales incorrectas para '{}'", dto.getUsername());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Credenciales incorrectas"));
                });
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("[UsuarioController] DELETE /usuarios/{} - Eliminando usuario", id);
        if (service.eliminar(id)) {
            log.info("[UsuarioController] DELETE /usuarios/{} - Usuario desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[UsuarioController] DELETE /usuarios/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
