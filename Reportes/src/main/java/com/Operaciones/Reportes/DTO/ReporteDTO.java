package com.Operaciones.Reportes.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReporteDTO {

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    private String descripcion;
}
