package com.gestion.productos.Assemblers;

import com.gestion.productos.Controller.ProductoController;
import com.gestion.productos.Model.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).obtenerTodos()).withRel("productos"),
                linkTo(methodOn(ProductoController.class).eliminar(producto.getId())).withRel("eliminar")
        );
    }
}
