package com.Operaciones.Notificaciones.Controller;

import com.Operaciones.Notificaciones.Assemblers.NotificacionModelAssembler;
import com.Operaciones.Notificaciones.DTO.NotificacionDTO;
import com.Operaciones.Notificaciones.Model.Notificacion;
import com.Operaciones.Notificaciones.Service.NotificacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private NotificacionModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Notificacion>> obtenerTodos() {
        log.info("[NotificacionController] GET /api/v1/notificaciones - Listando notificaciones");
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[NotificacionController] GET /api/v1/notificaciones - Retornando {} notificaciones", notificaciones.size());
        return CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionController.class).obtenerTodos()).withRel("notificaciones"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> obtenerPorId(@PathVariable Long id) {
        log.info("[NotificacionController] GET /api/v1/notificaciones/{} - Buscando notificacion", id);
        return notificacionService.obtenerPorId(id)
                .map(notificacion -> {
                    log.info("[NotificacionController] GET /api/v1/notificaciones/{} - Notificacion encontrada", id);
                    return ResponseEntity.ok(assembler.toModel(notificacion));
                })
                .orElseGet(() -> {
                    log.warn("[NotificacionController] GET /api/v1/notificaciones/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Notificacion>> crear(@Valid @RequestBody NotificacionDTO dto) {
        log.info("[NotificacionController] POST /api/v1/notificaciones - Creando notificacion");
        Notificacion creada = notificacionService.crear(dto);
        log.info("[NotificacionController] POST /api/v1/notificaciones - Notificacion creada con id={}", creada.getId());
        return ResponseEntity
                .created(URI.create("/api/v1/notificaciones/" + creada.getId()))
                .body(assembler.toModel(creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> actualizar(@PathVariable Long id, @Valid @RequestBody NotificacionDTO dto) {
        log.info("[NotificacionController] PUT /api/v1/notificaciones/{} - Actualizando notificacion", id);
        return notificacionService.actualizar(id, dto)
                .map(notificacion -> {
                    log.info("[NotificacionController] PUT /api/v1/notificaciones/{} - Notificacion actualizada", id);
                    return ResponseEntity.ok(assembler.toModel(notificacion));
                })
                .orElseGet(() -> {
                    log.warn("[NotificacionController] PUT /api/v1/notificaciones/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[NotificacionController] DELETE /api/v1/notificaciones/{} - Eliminando notificacion", id);
        if (notificacionService.eliminar(id)) {
            log.info("[NotificacionController] DELETE /api/v1/notificaciones/{} - Notificacion eliminada", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[NotificacionController] DELETE /api/v1/notificaciones/{} - No encontrada", id);
        return ResponseEntity.notFound().build();
    }
}
