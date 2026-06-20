package com.gestion.proveedores.Controller;

import com.gestion.proveedores.Assemblers.ProveedorModelAssembler;
import com.gestion.proveedores.DTO.ProveedorDTO;
import com.gestion.proveedores.Model.Proveedor;
import com.gestion.proveedores.Service.ProveedorService;
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
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private static final Logger log = LoggerFactory.getLogger(ProveedorController.class);

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProveedorModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Proveedor>> obtenerTodos() {
        log.info("[ProveedorController] GET /proveedores - Iniciando listado de proveedores");
        List<EntityModel<Proveedor>> proveedores = proveedorService.obtenerTodos().stream()
                .map(assembler::toModel).toList();
        log.info("[ProveedorController] GET /proveedores - Retornando {} proveedores", proveedores.size());
        return CollectionModel.of(proveedores,
                linkTo(methodOn(ProveedorController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Proveedor>> obtenerPorId(@PathVariable Long id) {
        log.info("[ProveedorController] GET /proveedores/{} - Buscando proveedor", id);
        return proveedorService.obtenerPorId(id)
                .map(p -> {
                    log.info("[ProveedorController] GET /proveedores/{} - Encontrado: '{}'", id, p.getNombre());
                    return ResponseEntity.ok(assembler.toModel(p));
                })
                .orElseGet(() -> {
                    log.warn("[ProveedorController] GET /proveedores/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Proveedor>> crear(@Valid @RequestBody ProveedorDTO dto) {
        log.info("[ProveedorController] POST /proveedores - Creando proveedor '{}'", dto.getNombre());
        Proveedor creado = proveedorService.crear(dto);
        log.info("[ProveedorController] POST /proveedores - Proveedor '{}' creado con id={}", creado.getNombre(), creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Proveedor>> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorDTO dto) {
        log.info("[ProveedorController] PUT /proveedores/{} - Actualizando proveedor", id);
        return proveedorService.actualizar(id, dto)
                .map(p -> {
                    log.info("[ProveedorController] PUT /proveedores/{} - Actualizado: '{}'", id, p.getNombre());
                    return ResponseEntity.ok(assembler.toModel(p));
                })
                .orElseGet(() -> {
                    log.warn("[ProveedorController] PUT /proveedores/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[ProveedorController] DELETE /proveedores/{} - Eliminando proveedor", id);
        if (proveedorService.eliminar(id)) {
            log.info("[ProveedorController] DELETE /proveedores/{} - Desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[ProveedorController] DELETE /proveedores/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
