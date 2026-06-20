package com.gestion.almacenes.Assemblers;

import com.gestion.almacenes.Controller.AlmacenController;
import com.gestion.almacenes.Model.Almacen;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AlmacenModelAssembler implements RepresentationModelAssembler<Almacen, EntityModel<Almacen>> {

    @Override
    public EntityModel<Almacen> toModel(Almacen almacen) {
        return EntityModel.of(almacen,
                linkTo(methodOn(AlmacenController.class).obtenerPorId(almacen.getId())).withSelfRel(),
                linkTo(methodOn(AlmacenController.class).obtenerTodos()).withRel("almacenes"),
                linkTo(methodOn(AlmacenController.class).eliminar(almacen.getId())).withRel("eliminar")
        );
    }
}
