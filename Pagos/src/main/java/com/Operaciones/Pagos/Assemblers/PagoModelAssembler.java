package com.Operaciones.Pagos.Assemblers;

import com.Operaciones.Pagos.Controller.PagoController;
import com.Operaciones.Pagos.Model.Pago;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<Pago, EntityModel<Pago>> {

    @Override
    public EntityModel<Pago> toModel(Pago pago) {
        return EntityModel.of(pago,
                linkTo(methodOn(PagoController.class).obtenerPorId(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagoController.class).obtenerTodos()).withRel("pagos"),
                linkTo(methodOn(PagoController.class).eliminar(pago.getId())).withRel("eliminar")
        );
    }
}
