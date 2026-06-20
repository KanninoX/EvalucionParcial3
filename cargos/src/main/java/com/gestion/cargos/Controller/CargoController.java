package com.gestion.cargos.Controller;

import com.gestion.cargos.Assemblers.CargoModelAssembler;
import com.gestion.cargos.DTO.CargoDTO;
import com.gestion.cargos.Model.Cargo;
import com.gestion.cargos.Service.CargoService;
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
@RequestMapping("/api/v1/cargos")
public class CargoController {

    private static final Logger log = LoggerFactory.getLogger(CargoController.class);

    @Autowired
    private CargoService service;

    @Autowired
    private CargoModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Cargo>> listar() {
        log.info("[CargoController] GET /cargos - Iniciando listado de cargos");
        List<EntityModel<Cargo>> cargos = service.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[CargoController] GET /cargos - Retornando {} cargos", cargos.size());
        return CollectionModel.of(cargos,
                linkTo(methodOn(CargoController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cargo>> obtener(@PathVariable Integer id) {
        log.info("[CargoController] GET /cargos/{} - Buscando cargo", id);
        return service.obtenerPorId(id)
                .map(c -> {
                    log.info("[CargoController] GET /cargos/{} - Encontrado: '{}'", id, c.getNombre());
                    return ResponseEntity.ok(assembler.toModel(c));
                })
                .orElseGet(() -> {
                    log.warn("[CargoController] GET /cargos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody CargoDTO dto) {
        log.info("[CargoController] POST /cargos/crear - Creando cargo '{}', dpto={}", dto.getNombre(), dto.getDepartamentoId());
        Boolean save = service.crearCargo(dto);
        if (!save) {
            log.warn("[CargoController] POST /cargos/crear - Fallido: departamento id={} no existe", dto.getDepartamentoId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Departamento no existe en su microservicio.");
        }
        log.info("[CargoController] POST /cargos/crear - Cargo '{}' creado exitosamente", dto.getNombre());
        return ResponseEntity.ok("Cargo creado correctamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cargo>> actualizar(@PathVariable Integer id, @RequestBody CargoDTO dto) {
        log.info("[CargoController] PUT /cargos/{} - Actualizando cargo", id);
        return service.actualizar(id, dto)
                .map(c -> {
                    log.info("[CargoController] PUT /cargos/{} - Actualizado: '{}'", id, c.getNombre());
                    return ResponseEntity.ok(assembler.toModel(c));
                })
                .orElseGet(() -> {
                    log.warn("[CargoController] PUT /cargos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("[CargoController] DELETE /cargos/{} - Desactivando cargo", id);
        if (service.eliminar(id)) {
            log.info("[CargoController] DELETE /cargos/{} - Desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[CargoController] DELETE /cargos/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
