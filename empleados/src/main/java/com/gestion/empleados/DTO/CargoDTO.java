package com.gestion.empleados.DTO;

import lombok.Data;

@Data
public class CargoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String estado;
    private Integer departamentoId;
}
