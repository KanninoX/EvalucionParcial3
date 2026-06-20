package com.Operaciones.Pagos.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoDTO {

    @NotNull(message = "El ventaId es obligatorio")
    private Long ventaId;

    @NotNull(message = "El monto es obligatorio")
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    private String estado;
}
