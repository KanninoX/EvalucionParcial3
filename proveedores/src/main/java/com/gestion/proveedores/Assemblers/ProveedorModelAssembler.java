package com.gestion.proveedores.Assemblers;

import com.gestion.proveedores.Controller.ProveedorController;
import com.gestion.proveedores.Model.Proveedor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProveedorModelAssembler implements RepresentationModelAssembler<Proveedor, EntityModel<Proveedor>> {

    @Override
    public EntityModel<Proveedor> toModel(Proveedor proveedor) {
        return EntityModel.of(proveedor,
                linkTo(methodOn(ProveedorController.class).obtenerPorId(proveedor.getId())).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).obtenerTodos()).withRel("proveedores"),
                linkTo(methodOn(ProveedorController.class).eliminar(proveedor.getId())).withRel("eliminar")
        );
    }
}
