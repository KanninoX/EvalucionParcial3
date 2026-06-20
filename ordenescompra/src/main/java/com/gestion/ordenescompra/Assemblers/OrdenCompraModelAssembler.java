package com.gestion.ordenescompra.Assemblers;

import com.gestion.ordenescompra.Controller.OrdenCompraController;
import com.gestion.ordenescompra.Model.OrdenCompra;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class OrdenCompraModelAssembler implements RepresentationModelAssembler<OrdenCompra, EntityModel<OrdenCompra>> {

    @Override
    public EntityModel<OrdenCompra> toModel(OrdenCompra orden) {
        return EntityModel.of(orden,
                linkTo(methodOn(OrdenCompraController.class).obtenerPorId(orden.getId())).withSelfRel(),
                linkTo(methodOn(OrdenCompraController.class).obtenerTodos()).withRel("ordenescompra"),
                linkTo(methodOn(OrdenCompraController.class).eliminar(orden.getId())).withRel("eliminar")
        );
    }
}
