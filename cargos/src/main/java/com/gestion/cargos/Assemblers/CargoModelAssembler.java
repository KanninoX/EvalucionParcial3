package com.gestion.cargos.Assemblers;

import com.gestion.cargos.Controller.CargoController;
import com.gestion.cargos.Model.Cargo;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CargoModelAssembler implements RepresentationModelAssembler<Cargo, EntityModel<Cargo>> {

    @Override
    public EntityModel<Cargo> toModel(Cargo cargo) {
        return EntityModel.of(cargo,
                linkTo(methodOn(CargoController.class).obtener(cargo.getId())).withSelfRel(),
                linkTo(methodOn(CargoController.class).listar()).withRel("cargos"),
                linkTo(methodOn(CargoController.class).eliminar(cargo.getId())).withRel("eliminar")
        );
    }
}
