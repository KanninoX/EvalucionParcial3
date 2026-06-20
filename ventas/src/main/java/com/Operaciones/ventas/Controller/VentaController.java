package com.Operaciones.ventas.Controller;

import com.Operaciones.ventas.Assemblers.VentaModelAssembler;
import com.Operaciones.ventas.DTO.VentaDTO;
import com.Operaciones.ventas.Model.Venta;
import com.Operaciones.ventas.Service.VentaService;
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
@RequestMapping("/api/v1/ventas")
public class VentaController {

    private static final Logger log = LoggerFactory.getLogger(VentaController.class);

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Venta>> obtenerTodos() {
        log.info("[VentaController] GET /api/v1/ventas - Listando ventas");
        List<EntityModel<Venta>> ventas = ventaService.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[VentaController] GET /api/v1/ventas - Retornando {} ventas", ventas.size());
        return CollectionModel.of(ventas,
                linkTo(methodOn(VentaController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerPorId(@PathVariable Long id) {
        log.info("[VentaController] GET /api/v1/ventas/{} - Buscando venta", id);
        return ventaService.obtenerPorId(id)
                .map(venta -> {
                    log.info("[VentaController] GET /api/v1/ventas/{} - Venta encontrada", id);
                    return ResponseEntity.ok(assembler.toModel(venta));
                })
                .orElseGet(() -> {
                    log.warn("[VentaController] GET /api/v1/ventas/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Venta>> crear(@Valid @RequestBody VentaDTO dto) {
        log.info("[VentaController] POST /api/v1/ventas - Creando venta");
        Venta creada = ventaService.crear(dto);
        log.info("[VentaController] POST /api/v1/ventas - Venta creada con id={}", creada.getId());
        return ResponseEntity
                .created(URI.create("/api/v1/ventas/" + creada.getId()))
                .body(assembler.toModel(creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> actualizar(@PathVariable Long id, @Valid @RequestBody VentaDTO dto) {
        log.info("[VentaController] PUT /api/v1/ventas/{} - Actualizando venta", id);
        return ventaService.actualizar(id, dto)
                .map(venta -> {
                    log.info("[VentaController] PUT /api/v1/ventas/{} - Venta actualizada", id);
                    return ResponseEntity.ok(assembler.toModel(venta));
                })
                .orElseGet(() -> {
                    log.warn("[VentaController] PUT /api/v1/ventas/{} - No encontrada", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[VentaController] DELETE /api/v1/ventas/{} - Eliminando venta", id);
        if (ventaService.eliminar(id)) {
            log.info("[VentaController] DELETE /api/v1/ventas/{} - Venta eliminada", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[VentaController] DELETE /api/v1/ventas/{} - No encontrada", id);
        return ResponseEntity.notFound().build();
    }
}
