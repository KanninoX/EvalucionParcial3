package com.gestion.autenticacion.DTO;

import lombok.Data;

@Data
public class EmpleadoDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String estado;
}
