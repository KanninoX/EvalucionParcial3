package com.Operaciones.ventas.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaDTO {

    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;

    private Long empleadoId;

    @NotBlank(message = "El producto es obligatorio")
    private String producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    private Double precioUnitario;
}
