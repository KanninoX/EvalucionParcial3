package com.gestion.existencias.Assemblers;

import com.gestion.existencias.Controller.ExistenciaController;
import com.gestion.existencias.Model.Existencia;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ExistenciaModelAssembler implements RepresentationModelAssembler<Existencia, EntityModel<Existencia>> {

    @Override
    public EntityModel<Existencia> toModel(Existencia existencia) {
        return EntityModel.of(existencia,
                linkTo(methodOn(ExistenciaController.class).obtenerPorId(existencia.getId())).withSelfRel(),
                linkTo(methodOn(ExistenciaController.class).obtenerTodos()).withRel("existencias"),
                linkTo(methodOn(ExistenciaController.class).eliminar(existencia.getId())).withRel("eliminar")
        );
    }
}
