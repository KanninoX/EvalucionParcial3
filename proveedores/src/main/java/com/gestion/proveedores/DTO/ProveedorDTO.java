package com.gestion.proveedores.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProveedorDTO {

    @NotNull
    private String nombre;

    private String nif;

    private String estado;
}
