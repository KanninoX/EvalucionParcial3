package com.Operaciones.Reportes.Repository;

import com.Operaciones.Reportes.Model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
}