package com.gestion.auditoria.Controller;

import com.gestion.auditoria.Assemblers.AuditoriaModelAssembler;
import com.gestion.auditoria.DTO.AuditoriaDTO;
import com.gestion.auditoria.Model.Auditoria;
import com.gestion.auditoria.Service.AuditoriaService;
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
@RequestMapping("/api/v1/auditoria")
public class AuditoriaController {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaController.class);

    @Autowired
    private AuditoriaService service;

    @Autowired
    private AuditoriaModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Auditoria>> listar() {
        log.info("[AuditoriaController] GET /auditoria - Listando todos los registros de auditoría");
        List<EntityModel<Auditoria>> registros = service.obtenerTodas().stream()
                .map(assembler::toModel)
                .toList();
        log.info("[AuditoriaController] GET /auditoria - Retornando {} registros", registros.size());
        return CollectionModel.of(registros,
                linkTo(methodOn(AuditoriaController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Auditoria>> obtener(@PathVariable Integer id) {
        log.info("[AuditoriaController] GET /auditoria/{} - Buscando registro", id);
        return service.obtenerPorId(id)
                .map(a -> {
                    log.info("[AuditoriaController] GET /auditoria/{} - Encontrado: accion='{}', tabla='{}'",
                            id, a.getAccion(), a.getTabla());
                    return ResponseEntity.ok(assembler.toModel(a));
                })
                .orElseGet(() -> {
                    log.warn("[AuditoriaController] GET /auditoria/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/tabla/{tabla}")
    public CollectionModel<EntityModel<Auditoria>> porTabla(@PathVariable String tabla) {
        log.info("[AuditoriaController] GET /auditoria/tabla/{} - Filtrando por tabla", tabla);
        List<EntityModel<Auditoria>> registros = service.obtenerPorTabla(tabla).stream()
                .map(assembler::toModel)
                .toList();
        log.info("[AuditoriaController] GET /auditoria/tabla/{} - {} registros encontrados", tabla, registros.size());
        return CollectionModel.of(registros,
                linkTo(methodOn(AuditoriaController.class).porTabla(tabla)).withSelfRel(),
                linkTo(methodOn(AuditoriaController.class).listar()).withRel("auditorias"));
    }

    @GetMapping("/usuario/{usuario}")
    public CollectionModel<EntityModel<Auditoria>> porUsuario(@PathVariable String usuario) {
        log.info("[AuditoriaController] GET /auditoria/usuario/{} - Filtrando por usuario", usuario);
        List<EntityModel<Auditoria>> registros = service.obtenerPorUsuario(usuario).stream()
                .map(assembler::toModel)
                .toList();
        log.info("[AuditoriaController] GET /auditoria/usuario/{} - {} registros encontrados", usuario, registros.size());
        return CollectionModel.of(registros,
                linkTo(methodOn(AuditoriaController.class).porUsuario(usuario)).withSelfRel(),
                linkTo(methodOn(AuditoriaController.class).listar()).withRel("auditorias"));
    }

    @PostMapping("/registrar")
    public ResponseEntity<EntityModel<Auditoria>> registrar(@RequestBody AuditoriaDTO dto) {
        log.info("[AuditoriaController] POST /auditoria/registrar - Registrando accion='{}' en tabla='{}' por usuario='{}'",
                dto.getAccion(), dto.getTabla(), dto.getUsuario());
        Auditoria guardada = service.registrar(dto);
        log.info("[AuditoriaController] POST /auditoria/registrar - Registro creado con id={}", guardada.getId());
        return ResponseEntity.ok(assembler.toModel(guardada));
    }
}
