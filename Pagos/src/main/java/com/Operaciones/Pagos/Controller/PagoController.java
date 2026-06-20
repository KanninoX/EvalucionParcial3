package com.Operaciones.Pagos.Controller;

import com.Operaciones.Pagos.Assemblers.PagoModelAssembler;
import com.Operaciones.Pagos.DTO.PagoDTO;
import com.Operaciones.Pagos.Model.Pago;
import com.Operaciones.Pagos.Service.PagoService;
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
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Pago>> obtenerTodos() {
        log.info("[PagoController] GET /api/v1/pagos - Listando pagos");
        List<EntityModel<Pago>> pagos = pagoService.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[PagoController] GET /api/v1/pagos - Retornando {} pagos", pagos.size());
        return CollectionModel.of(pagos,
                linkTo(methodOn(PagoController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pago>> obtenerPorId(@PathVariable Long id) {
        log.info("[PagoController] GET /api/v1/pagos/{} - Buscando pago", id);
        return pagoService.obtenerPorId(id)
                .map(pago -> {
                    log.info("[PagoController] GET /api/v1/pagos/{} - Pago encontrado", id);
                    return ResponseEntity.ok(assembler.toModel(pago));
                })
                .orElseGet(() -> {
                    log.warn("[PagoController] GET /api/v1/pagos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Pago>> crear(@Valid @RequestBody PagoDTO dto) {
        log.info("[PagoController] POST /api/v1/pagos - Creando pago");
        Pago creado = pagoService.crear(dto);
        log.info("[PagoController] POST /api/v1/pagos - Pago creado con id={}", creado.getId());
        return ResponseEntity
                .created(URI.create("/api/v1/pagos/" + creado.getId()))
                .body(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Pago>> actualizar(@PathVariable Long id, @Valid @RequestBody PagoDTO dto) {
        log.info("[PagoController] PUT /api/v1/pagos/{} - Actualizando pago", id);
        return pagoService.actualizar(id, dto)
                .map(pago -> {
                    log.info("[PagoController] PUT /api/v1/pagos/{} - Pago actualizado", id);
                    return ResponseEntity.ok(assembler.toModel(pago));
                })
                .orElseGet(() -> {
                    log.warn("[PagoController] PUT /api/v1/pagos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[PagoController] DELETE /api/v1/pagos/{} - Eliminando pago", id);
        if (pagoService.eliminar(id)) {
            log.info("[PagoController] DELETE /api/v1/pagos/{} - Pago eliminado", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[PagoController] DELETE /api/v1/pagos/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
