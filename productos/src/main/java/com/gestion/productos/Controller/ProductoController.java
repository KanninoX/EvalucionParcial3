package com.gestion.productos.Controller;

import com.gestion.productos.Assemblers.ProductoModelAssembler;
import com.gestion.productos.DTO.ProductoDTO;
import com.gestion.productos.Model.Producto;
import com.gestion.productos.Service.ProductoService;
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
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Producto>> obtenerTodos() {
        log.info("[ProductoController] GET /productos - Iniciando listado de productos");
        List<EntityModel<Producto>> productos = productoService.obtenerTodos().stream()
                .map(assembler::toModel).toList();
        log.info("[ProductoController] GET /productos - Retornando {} productos", productos.size());
        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerPorId(@PathVariable Long id) {
        log.info("[ProductoController] GET /productos/{} - Buscando producto", id);
        return productoService.obtenerPorId(id)
                .map(p -> {
                    log.info("[ProductoController] GET /productos/{} - Encontrado: '{}'", id, p.getNombre());
                    return ResponseEntity.ok(assembler.toModel(p));
                })
                .orElseGet(() -> {
                    log.warn("[ProductoController] GET /productos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<EntityModel<Producto>> crear(@Valid @RequestBody ProductoDTO dto) {
        log.info("[ProductoController] POST /productos - Creando producto '{}'", dto.getNombre());
        Producto creado = productoService.crear(dto);
        log.info("[ProductoController] POST /productos - Producto '{}' creado con id={}", creado.getNombre(), creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        log.info("[ProductoController] PUT /productos/{} - Actualizando producto", id);
        return productoService.actualizar(id, dto)
                .map(p -> {
                    log.info("[ProductoController] PUT /productos/{} - Actualizado: '{}'", id, p.getNombre());
                    return ResponseEntity.ok(assembler.toModel(p));
                })
                .orElseGet(() -> {
                    log.warn("[ProductoController] PUT /productos/{} - No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("[ProductoController] DELETE /productos/{} - Eliminando producto", id);
        if (productoService.eliminar(id)) {
            log.info("[ProductoController] DELETE /productos/{} - Producto desactivado correctamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("[ProductoController] DELETE /productos/{} - No encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
