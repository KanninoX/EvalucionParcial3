package com.gestion.auditoria.Assemblers;

import com.gestion.auditoria.Controller.AuditoriaController;
import com.gestion.auditoria.Model.Auditoria;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AuditoriaModelAssembler implements RepresentationModelAssembler<Auditoria, EntityModel<Auditoria>> {

    @Override
    public EntityModel<Auditoria> toModel(Auditoria auditoria) {
        return EntityModel.of(auditoria,
                linkTo(methodOn(AuditoriaController.class).obtener(auditoria.getId())).withSelfRel(),
                linkTo(methodOn(AuditoriaController.class).listar()).withRel("auditorias"),
                linkTo(methodOn(AuditoriaController.class).porTabla(auditoria.getTabla())).withRel("por-tabla"),
                linkTo(methodOn(AuditoriaController.class).porUsuario(auditoria.getUsuario())).withRel("por-usuario")
        );
    }
}
