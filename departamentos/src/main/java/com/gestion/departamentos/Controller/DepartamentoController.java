package com.gestion.departamentos.Controller;

import com.gestion.departamentos.Assemblers.DepartamentoModelAssembler;
import com.gestion.departamentos.DTO.DepartamentoDTO;
import com.gestion.departamentos.Model.Departamento;
import com.gestion.departamentos.Service.DepartamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/departamentos")
public class DepartamentoController {

    private static final Logger log = LoggerFactory.getLogger(DepartamentoController.class);

    @Autowired
    private DepartamentoService service;

    @Autowired
    private DepartamentoModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Departamento>> listar() {
        log.info("[DepartamentoController] GET /departamentos - Iniciando listado");
        List<EntityModel<Departamento>> departamentos = service.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[DepartamentoController] GET /departamentos - Retornando {} departamentos", departamentos.size());
        return CollectionModel.of(departamentos,
                linkTo(methodOn(DepartamentoController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Departamento>> obtener(@PathVariable Integer id) {
        log.info("[DepartamentoController] GET /departamentos/{} - Buscando departamento", id);
        return service.obtenerPorId(id)
                .map(d -> {
                    log.info("[DepartamentoController] GET /departamentos/{} - Encontrado: '{}'", id, d.getNombre());
                    return ResponseEntity.ok(assembler.toModel(d));
                })
                .orElseGet(() -> {
                    log.warn("[DepartamentoController] GET /departamentos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Departamento>> crear(@RequestBody DepartamentoDTO dto) {
        log.info("[DepartamentoController] POST /departamentos - Creando departamento '{}'", dto.getNombre());
        Departamento creado = service.crear(dto);
        log.info("[DepartamentoController] POST /departamentos - Departamento '{}' creado con id={}", creado.getNombre(), creado.getId());
        return ResponseEntity.ok(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Departamento>> actualizar(@PathVariable Integer id, @RequestBody DepartamentoDTO dto) {
        log.info("[DepartamentoController] PUT /departamentos/{} - Actualizando departamento", id);
        return service.actualizar(id, dto)
                .map(d -> {
                    log.info("[DepartamentoController] PUT /departamentos/{} - Actualizado: '{}'", id, d.getNombre());
                    return ResponseEntity.ok(assembler.toModel(d));
                })
                .orElseGet(() -> {
                    log.warn("[DepartamentoController] PUT /departamentos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("[DepartamentoController] DELETE /departamentos/{} - Desactivando departamento", id);
        if (service.eliminar(id)) {
            log.info("[DepartamentoController] DELETE /departamentos/{} - Desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[DepartamentoController] DELETE /departamentos/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
