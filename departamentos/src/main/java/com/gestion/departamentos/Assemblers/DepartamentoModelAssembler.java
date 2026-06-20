package com.gestion.departamentos.Assemblers;

import com.gestion.departamentos.Controller.DepartamentoController;
import com.gestion.departamentos.Model.Departamento;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class DepartamentoModelAssembler implements RepresentationModelAssembler<Departamento, EntityModel<Departamento>> {

    @Override
    public EntityModel<Departamento> toModel(Departamento departamento) {
        return EntityModel.of(departamento,
                linkTo(methodOn(DepartamentoController.class).obtener(departamento.getId())).withSelfRel(),
                linkTo(methodOn(DepartamentoController.class).listar()).withRel("departamentos"),
                linkTo(methodOn(DepartamentoController.class).eliminar(departamento.getId())).withRel("eliminar")
        );
    }
}
