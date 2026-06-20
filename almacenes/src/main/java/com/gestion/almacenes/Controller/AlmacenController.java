package com.gestion.almacenes.Controller;

import com.gestion.almacenes.Assemblers.AlmacenModelAssembler;
import com.gestion.almacenes.DTO.AlmacenDTO;
import com.gestion.almacenes.Model.Almacen;
import com.gestion.almacenes.Service.AlmacenService;
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
@RequestMapping("/api/v1/almacenes")
public class AlmacenController {

    private static final Logger log = LoggerFactory.getLogger(AlmacenController.class);

    @Autowired
    private AlmacenService almacenService;

    @Autowired
    private AlmacenModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Almacen>> obtenerTodos() {
        log.info("[AlmacenController] GET /almacenes - Iniciando listado de almacenes");
        List<EntityModel<Almacen>> almacenes = almacenService.obtenerTodos().stream()
                .map(assembler::toModel).toList();
        log.info("[AlmacenController] GET /almacenes - Retornando {} almacenes", almacenes.size());
        return CollectionModel.of(almacenes,
                linkTo(methodOn(AlmacenController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Almacen>> obtenerPorId(@PathVariable Long id) {
        log.info("[AlmacenController] GET /almacenes/{} - Buscando almacen", id);
        return almacenService.obtenerPorId(id)
                .map(a -> {
                    log.info("[AlmacenController] GET /almacenes/{} - Encontrado: '{}'", id, a.getNombre());
                    return ResponseEntity.ok(assembler.toModel(a));
                })
                .orElseGet(() -> {
                    log.warn("[AlmacenController] GET /almacenes/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Almacen>> crear(@Valid @RequestBody AlmacenDTO dto) {
        log.info("[AlmacenController] POST /almacenes - Creando almacen '{}', ubicacion='{}'", dto.getNombre(), dto.getUbicacion());
        Almacen creado = almacenService.crear(dto);
        log.info("[AlmacenController] POST /almacenes - Almacen '{}' creado con id={}", creado.getNombre(), creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Almacen>> actualizar(@PathVariable Long id, @Valid @RequestBody AlmacenDTO dto) {
        log.info("[AlmacenController] PUT /almacenes/{} - Actualizando almacen", id);
        return almacenService.actualizar(id, dto)
                .map(a -> {
                    log.info("[AlmacenController] PUT /almacenes/{} - Actualizado: '{}'", id, a.getNombre());
                    return ResponseEntity.ok(assembler.toModel(a));
                })
                .orElseGet(() -> {
                    log.warn("[AlmacenController] PUT /almacenes/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[AlmacenController] DELETE /almacenes/{} - Eliminando almacen", id);
        if (almacenService.eliminar(id)) {
            log.info("[AlmacenController] DELETE /almacenes/{} - Desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[AlmacenController] DELETE /almacenes/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
