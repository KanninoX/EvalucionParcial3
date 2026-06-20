package com.Operaciones.Reportes.Assemblers;

import com.Operaciones.Reportes.Controller.ReporteController;
import com.Operaciones.Reportes.Model.Reporte;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ReporteModelAssembler implements RepresentationModelAssembler<Reporte, EntityModel<Reporte>> {

    @Override
    public EntityModel<Reporte> toModel(Reporte reporte) {
        return EntityModel.of(reporte,
                linkTo(methodOn(ReporteController.class).obtenerPorId(reporte.getId())).withSelfRel(),
                linkTo(methodOn(ReporteController.class).obtenerTodos()).withRel("reportes"),
                linkTo(methodOn(ReporteController.class).eliminar(reporte.getId())).withRel("eliminar")
        );
    }
}
