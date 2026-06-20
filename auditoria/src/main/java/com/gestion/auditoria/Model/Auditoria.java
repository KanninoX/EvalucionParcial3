package com.gestion.auditoria.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String accion;

    @Column(nullable = false)
    private String tabla;

    private Integer registroId;

    private String usuario;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(length = 500)
    private String detalles;
}
