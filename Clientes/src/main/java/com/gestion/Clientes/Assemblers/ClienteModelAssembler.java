package com.gestion.Clientes.Assemblers;

import com.gestion.Clientes.Controller.ClienteController;
import com.gestion.Clientes.Model.Cliente;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<Cliente, EntityModel<Cliente>> {

    @Override
    public EntityModel<Cliente> toModel(Cliente cliente) {
        return EntityModel.of(cliente,
                linkTo(methodOn(ClienteController.class).obtenerPorId(cliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).obtenerTodos()).withRel("clientes"),
                linkTo(methodOn(ClienteController.class).eliminar(cliente.getId())).withRel("eliminar")
        );
    }
}
