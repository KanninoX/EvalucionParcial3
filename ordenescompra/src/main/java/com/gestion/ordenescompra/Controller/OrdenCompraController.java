package com.gestion.ordenescompra.Controller;

import com.gestion.ordenescompra.Assemblers.OrdenCompraModelAssembler;
import com.gestion.ordenescompra.DTO.OrdenCompraDTO;
import com.gestion.ordenescompra.Model.OrdenCompra;
import com.gestion.ordenescompra.Service.OrdenCompraService;
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
@RequestMapping("/api/v1/ordenescompra")
public class OrdenCompraController {

    private static final Logger log = LoggerFactory.getLogger(OrdenCompraController.class);

    @Autowired
    private OrdenCompraService ordenCompraService;

    @Autowired
    private OrdenCompraModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<OrdenCompra>> obtenerTodos() {
        log.info("[OrdenCompraController] GET /ordenescompra - Iniciando listado de órdenes de compra");
        List<EntityModel<OrdenCompra>> ordenes = ordenCompraService.obtenerTodos().stream()
                .map(assembler::toModel).toList();
        log.info("[OrdenCompraController] GET /ordenescompra - Retornando {} órdenes", ordenes.size());
        return CollectionModel.of(ordenes,
                linkTo(methodOn(OrdenCompraController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrdenCompra>> obtenerPorId(@PathVariable Long id) {
        log.info("[OrdenCompraController] GET /ordenescompra/{} - Buscando orden de compra", id);
        return ordenCompraService.obtenerPorId(id)
                .map(o -> {
                    log.info("[OrdenCompraController] GET /ordenescompra/{} - Encontrada: numero='{}', estado='{}'", id, o.getNumero(), o.getEstado());
                    return ResponseEntity.ok(assembler.toModel(o));
                })
                .orElseGet(() -> {
                    log.warn("[OrdenCompraController] GET /ordenescompra/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<OrdenCompra>> crear(@Valid @RequestBody OrdenCompraDTO dto) {
        log.info("[OrdenCompraController] POST /ordenescompra - Creando orden numero='{}'", dto.getNumero());
        OrdenCompra creada = ordenCompraService.crear(dto);
        log.info("[OrdenCompraController] POST /ordenescompra - Orden '{}' creada con id={}, estado='{}'", creada.getNumero(), creada.getId(), creada.getEstado());
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<OrdenCompra>> actualizar(@PathVariable Long id, @Valid @RequestBody OrdenCompraDTO dto) {
        log.info("[OrdenCompraController] PUT /ordenescompra/{} - Actualizando orden, nuevo estado='{}'", id, dto.getEstado());
        return ordenCompraService.actualizar(id, dto)
                .map(o -> {
                    log.info("[OrdenCompraController] PUT /ordenescompra/{} - Actualizada: estado='{}'", id, o.getEstado());
                    return ResponseEntity.ok(assembler.toModel(o));
                })
                .orElseGet(() -> {
                    log.warn("[OrdenCompraController] PUT /ordenescompra/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[OrdenCompraController] DELETE /ordenescompra/{} - Eliminando orden de compra", id);
        if (ordenCompraService.eliminar(id)) {
            log.info("[OrdenCompraController] DELETE /ordenescompra/{} - Eliminada correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[OrdenCompraController] DELETE /ordenescompra/{} - No encontrada", id);
        return ResponseEntity.notFound().build();
    }
}
