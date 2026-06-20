package com.Operaciones.Notificaciones.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificacionDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatario;

    private String estado;
}
