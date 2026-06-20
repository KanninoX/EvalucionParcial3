package com.gestion.autenticacion.Assemblers;

import com.gestion.autenticacion.Controller.UsuarioController;
import com.gestion.autenticacion.Model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtener(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listar()).withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class).eliminar(usuario.getId())).withRel("eliminar")
        );
    }
}
