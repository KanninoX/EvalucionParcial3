package com.gestion.auditoria.DTO;

import lombok.Data;

@Data
public class AuditoriaDTO {
    private String accion;
    private String tabla;
    private Integer registroId;
    private String usuario;
    private String detalles;
}
