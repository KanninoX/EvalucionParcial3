package com.gestion.cargos.DTO;

import lombok.Data;

@Data
public class CargoDTO {
    private String nombre;
    private String descripcion;
    private Integer departamentoId;
}
