package com.gestion.productos.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoDTO {

    @NotNull
    private String nombre;

    private String codigo;

    private String estado;
}
