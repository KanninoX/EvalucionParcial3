package com.gestion.existencias.Controller;

import com.gestion.existencias.Assemblers.ExistenciaModelAssembler;
import com.gestion.existencias.DTO.ExistenciaDTO;
import com.gestion.existencias.Model.Existencia;
import com.gestion.existencias.Service.ExistenciaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/existencias")
public class ExistenciaController {

    private static final Logger log = LoggerFactory.getLogger(ExistenciaController.class);

    @Autowired
    private ExistenciaService existenciaService;

    @Autowired
    private ExistenciaModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Existencia>> obtenerTodos() {
        log.info("[ExistenciaController] GET /existencias - Iniciando listado de existencias");
        List<EntityModel<Existencia>> existencias = existenciaService.obtenerTodos().stream()
                .map(assembler::toModel).toList();
        log.info("[ExistenciaController] GET /existencias - Retornando {} registros de existencia", existencias.size());
        return CollectionModel.of(existencias,
                linkTo(methodOn(ExistenciaController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Existencia>> obtenerPorId(@PathVariable Long id) {
        log.info("[ExistenciaController] GET /existencias/{} - Buscando existencia", id);
        return existenciaService.obtenerPorId(id)
                .map(e -> {
                    log.info("[ExistenciaController] GET /existencias/{} - Encontrada: referencia='{}', cantidad={}", id, e.getReferencia(), e.getCantidad());
                    return ResponseEntity.ok(assembler.toModel(e));
                })
                .orElseGet(() -> {
                    log.warn("[ExistenciaController] GET /existencias/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Existencia>> crear(@Valid @RequestBody ExistenciaDTO dto) {
        log.info("[ExistenciaController] POST /existencias - Creando existencia referencia='{}', cantidad={}", dto.getReferencia(), dto.getCantidad());
        Existencia creada = existenciaService.crear(dto);
        log.info("[ExistenciaController] POST /existencias - Existencia creada con id={}", creada.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Existencia>> actualizar(@PathVariable Long id, @Valid @RequestBody ExistenciaDTO dto) {
        log.info("[ExistenciaController] PUT /existencias/{} - Actualizando existencia, nueva cantidad={}", id, dto.getCantidad());
        return existenciaService.actualizar(id, dto)
                .map(e -> {
                    log.info("[ExistenciaController] PUT /existencias/{} - Actualizada correctamente", id);
                    return ResponseEntity.ok(assembler.toModel(e));
                })
                .orElseGet(() -> {
                    log.warn("[ExistenciaController] PUT /existencias/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[ExistenciaController] DELETE /existencias/{} - Eliminando existencia", id);
        if (existenciaService.eliminar(id)) {
            log.info("[ExistenciaController] DELETE /existencias/{} - Eliminada correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[ExistenciaController] DELETE /existencias/{} - No encontrada", id);
        return ResponseEntity.notFound().build();
    }
}
