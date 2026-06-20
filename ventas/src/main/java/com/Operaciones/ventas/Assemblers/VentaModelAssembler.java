package com.Operaciones.ventas.Assemblers;

import com.Operaciones.ventas.Controller.VentaController;
import com.Operaciones.ventas.Model.Venta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class VentaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerPorId(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).obtenerTodos()).withRel("ventas"),
                linkTo(methodOn(VentaController.class).eliminar(venta.getId())).withRel("eliminar")
        );
    }
}
