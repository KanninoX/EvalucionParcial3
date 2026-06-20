package com.gestion.autenticacion.DTO;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String username;
    private String password;
    private String rol;
    private Integer empleadoId;
}
