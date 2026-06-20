package com.gestion.empleados.Assemblers;

import com.gestion.empleados.Controller.EmpleadoController;
import com.gestion.empleados.Model.Empleado;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EmpleadoModelAssembler implements RepresentationModelAssembler<Empleado, EntityModel<Empleado>> {

    @Override
    public EntityModel<Empleado> toModel(Empleado empleado) {
        return EntityModel.of(empleado,
                linkTo(methodOn(EmpleadoController.class).obtener(empleado.getId())).withSelfRel(),
                linkTo(methodOn(EmpleadoController.class).listar()).withRel("empleados"),
                linkTo(methodOn(EmpleadoController.class).eliminar(empleado.getId())).withRel("eliminar")
        );
    }
}
