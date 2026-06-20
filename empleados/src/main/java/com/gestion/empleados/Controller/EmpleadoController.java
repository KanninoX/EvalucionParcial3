package com.gestion.empleados.Controller;

import com.gestion.empleados.Assemblers.EmpleadoModelAssembler;
import com.gestion.empleados.DTO.EmpleadoDTO;
import com.gestion.empleados.Model.Empleado;
import com.gestion.empleados.Service.EmpleadoService;
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
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoController.class);

    @Autowired
    private EmpleadoService service;

    @Autowired
    private EmpleadoModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Empleado>> listar() {
        log.info("[EmpleadoController] GET /empleados - Iniciando listado de empleados");
        List<EntityModel<Empleado>> empleados = service.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[EmpleadoController] GET /empleados - Retornando {} empleados", empleados.size());
        return CollectionModel.of(empleados,
                linkTo(methodOn(EmpleadoController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Empleado>> obtener(@PathVariable Integer id) {
        log.info("[EmpleadoController] GET /empleados/{} - Buscando empleado", id);
        return service.obtenerPorId(id)
                .map(e -> {
                    log.info("[EmpleadoController] GET /empleados/{} - Encontrado: '{} {}'", id, e.getNombre(), e.getApellido());
                    return ResponseEntity.ok(assembler.toModel(e));
                })
                .orElseGet(() -> {
                    log.warn("[EmpleadoController] GET /empleados/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crear(@RequestBody EmpleadoDTO dto) {
        log.info("[EmpleadoController] POST /empleados/crear - Creando empleado '{} {}', dpto={}, cargo={}",
                dto.getNombre(), dto.getApellido(), dto.getDepartamentoId(), dto.getCargoId());
        Boolean save = service.crearEmpleado(dto);
        if (!save) {
            log.warn("[EmpleadoController] POST /empleados/crear - Fallido: departamento o cargo no existen");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Departamento o cargo no existen en sus respectivos microservicios.");
        }
        log.info("[EmpleadoController] POST /empleados/crear - Empleado '{}' creado exitosamente", dto.getNombre());
        return ResponseEntity.ok("Empleado creado correctamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Empleado>> actualizar(@PathVariable Integer id, @RequestBody EmpleadoDTO dto) {
        log.info("[EmpleadoController] PUT /empleados/{} - Actualizando empleado", id);
        return service.actualizar(id, dto)
                .map(e -> {
                    log.info("[EmpleadoController] PUT /empleados/{} - Actualizado correctamente", id);
                    return ResponseEntity.ok(assembler.toModel(e));
                })
                .orElseGet(() -> {
                    log.warn("[EmpleadoController] PUT /empleados/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("[EmpleadoController] DELETE /empleados/{} - Desactivando empleado", id);
        if (service.eliminar(id)) {
            log.info("[EmpleadoController] DELETE /empleados/{} - Empleado desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[EmpleadoController] DELETE /empleados/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
