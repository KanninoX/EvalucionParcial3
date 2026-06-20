package com.Operaciones.ventas.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private Long empleadoId;
    private String producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double total;
    private String estado;
    private LocalDateTime fecha;
}
