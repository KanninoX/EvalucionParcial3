package com.gestion.Clientes.Controller;

import com.gestion.Clientes.Assemblers.ClienteModelAssembler;
import com.gestion.Clientes.DTO.ClienteDTO;
import com.gestion.Clientes.Model.Cliente;
import com.gestion.Clientes.Service.ClienteService;
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
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Cliente>> obtenerTodos() {
        log.info("[ClienteController] GET /api/v1/clientes - Listando clientes");
        List<EntityModel<Cliente>> clientes = clienteService.obtenerTodos().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[ClienteController] GET /api/v1/clientes - Retornando {} clientes", clientes.size());
        return CollectionModel.of(clientes,
                linkTo(methodOn(ClienteController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> obtenerPorId(@PathVariable Long id) {
        log.info("[ClienteController] GET /api/v1/clientes/{} - Buscando cliente", id);
        return clienteService.obtenerPorId(id)
                .map(cliente -> {
                    log.info("[ClienteController] GET /api/v1/clientes/{} - Cliente encontrado", id);
                    return ResponseEntity.ok(assembler.toModel(cliente));
                })
                .orElseGet(() -> {
                    log.warn("[ClienteController] GET /api/v1/clientes/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> crear(@Valid @RequestBody ClienteDTO dto) {
        log.info("[ClienteController] POST /api/v1/clientes - Creando cliente");
        Cliente creado = clienteService.crear(dto);
        log.info("[ClienteController] POST /api/v1/clientes - Cliente creado con id={}", creado.getId());
        return ResponseEntity
                .created(URI.create("/api/v1/clientes/" + creado.getId()))
                .body(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        log.info("[ClienteController] PUT /api/v1/clientes/{} - Actualizando cliente", id);
        return clienteService.actualizar(id, dto)
                .map(cliente -> {
                    log.info("[ClienteController] PUT /api/v1/clientes/{} - Cliente actualizado", id);
                    return ResponseEntity.ok(assembler.toModel(cliente));
                })
                .orElseGet(() -> {
                    log.warn("[ClienteController] PUT /api/v1/clientes/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[ClienteController] DELETE /api/v1/clientes/{} - Eliminando cliente", id);
        if (clienteService.eliminar(id)) {
            log.info("[ClienteController] DELETE /api/v1/clientes/{} - Cliente eliminado", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[ClienteController] DELETE /api/v1/clientes/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
