package com.Operaciones.Reportes.Controller;

import com.Operaciones.Reportes.Assemblers.ReporteModelAssembler;
import com.Operaciones.Reportes.DTO.ReporteDTO;
import com.Operaciones.Reportes.Model.Reporte;
import com.Operaciones.Reportes.Service.ReporteService;
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
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private ReporteModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Reporte>> obtenerTodos() {
        log.info("[ReporteController] GET /api/v1/reportes - Listando reportes");
        List<EntityModel<Reporte>> reportes = reporteService.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[ReporteController] GET /api/v1/reportes - Retornando {} reportes", reportes.size());
        return CollectionModel.of(reportes,
                linkTo(methodOn(ReporteController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Reporte>> obtenerPorId(@PathVariable Long id) {
        log.info("[ReporteController] GET /api/v1/reportes/{} - Buscando reporte", id);
        return reporteService.obtenerPorId(id)
                .map(reporte -> {
                    log.info("[ReporteController] GET /api/v1/reportes/{} - Reporte encontrado", id);
                    return ResponseEntity.ok(assembler.toModel(reporte));
                })
                .orElseGet(() -> {
                    log.warn("[ReporteController] GET /api/v1/reportes/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Reporte>> crear(@Valid @RequestBody ReporteDTO dto) {
        log.info("[ReporteController] POST /api/v1/reportes - Creando reporte");
        Reporte creado = reporteService.crear(dto);
        log.info("[ReporteController] POST /api/v1/reportes - Reporte creado con id={}", creado.getId());
        return ResponseEntity
                .created(URI.create("/api/v1/reportes/" + creado.getId()))
                .body(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Reporte>> actualizar(@PathVariable Long id, @Valid @RequestBody ReporteDTO dto) {
        log.info("[ReporteController] PUT /api/v1/reportes/{} - Actualizando reporte", id);
        return reporteService.actualizar(id, dto)
                .map(reporte -> {
                    log.info("[ReporteController] PUT /api/v1/reportes/{} - Reporte actualizado", id);
                    return ResponseEntity.ok(assembler.toModel(reporte));
                })
                .orElseGet(() -> {
                    log.warn("[ReporteController] PUT /api/v1/reportes/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[ReporteController] DELETE /api/v1/reportes/{} - Eliminando reporte", id);
        if (reporteService.eliminar(id)) {
            log.info("[ReporteController] DELETE /api/v1/reportes/{} - Reporte eliminado", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[ReporteController] DELETE /api/v1/reportes/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
