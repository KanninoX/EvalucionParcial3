package com.gestion.ordenescompra.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdenCompraDTO {

    @NotNull
    private String numero;

    private String estado;

    private Long clienteId;
}
