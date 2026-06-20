package com.gestion.existencias.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExistenciaDTO {

    private String referencia;

    private BigDecimal cantidad;
}
