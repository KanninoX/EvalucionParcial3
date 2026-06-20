package com.Operaciones.Reportes.Service;

import com.Operaciones.Reportes.DTO.ReporteDTO;
import com.Operaciones.Reportes.Model.Reporte;
import com.Operaciones.Reportes.Repository.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    @Autowired
    private ReporteRepository reporteRepository;

    public List<Reporte> obtenerTodos() {
        log.info("[ReporteService] obtenerTodos - Consultando todos los reportes");
        List<Reporte> lista = reporteRepository.findAll();
        log.info("[ReporteService] obtenerTodos - Encontrados {} reportes", lista.size());
        return lista;
    }

    public Optional<Reporte> obtenerPorId(Long id) {
        log.info("[ReporteService] obtenerPorId - Buscando reporte con id={}", id);
        Optional<Reporte> resultado = reporteRepository.findById(id);
        resultado.ifPresentOrElse(
                r -> log.info("[ReporteService] obtenerPorId - Encontrado: tipo='{}'", r.getTipo()),
                () -> log.warn("[ReporteService] obtenerPorId - Reporte con id={} no existe", id)
        );
        return resultado;
    }

    public Reporte crear(ReporteDTO dto) {
        log.info("[ReporteService] crear - Creando reporte tipo='{}'", dto.getTipo());
        Reporte reporte = new Reporte();
        reporte.setTipo(dto.getTipo());
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setEstado("ACTIVO");
        reporte.setFecha(LocalDateTime.now());
        Reporte guardado = reporteRepository.save(reporte);
        log.info("[ReporteService] crear - Reporte guardado con id={}", guardado.getId());
        return guardado;
    }

    public Optional<Reporte> actualizar(Long id, ReporteDTO dto) {
        log.info("[ReporteService] actualizar - Actualizando reporte id={}", id);
        return reporteRepository.findById(id).map(reporte -> {
            reporte.setTipo(dto.getTipo());
            reporte.setDescripcion(dto.getDescripcion());
            Reporte guardado = reporteRepository.save(reporte);
            log.info("[ReporteService] actualizar - Reporte id={} actualizado", id);
            return guardado;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[ReporteService] eliminar - Soft-delete reporte id={}", id);
        return reporteRepository.findById(id).map(reporte -> {
            reporte.setEstado("INACTIVO");
            reporteRepository.save(reporte);
            log.info("[ReporteService] eliminar - Reporte id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[ReporteService] eliminar - Reporte id={} no encontrado", id);
            return false;
        });
    }
}
