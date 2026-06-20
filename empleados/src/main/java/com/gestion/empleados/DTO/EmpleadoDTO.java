package com.gestion.empleados.DTO;

import lombok.Data;

@Data
public class EmpleadoDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Integer departamentoId;
    private Integer cargoId;
}
