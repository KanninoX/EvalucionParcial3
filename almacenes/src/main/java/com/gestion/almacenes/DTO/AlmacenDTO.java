package com.gestion.almacenes.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlmacenDTO {

    @NotNull
    private String nombre;

    private String ubicacion;

    private String estado;
}
