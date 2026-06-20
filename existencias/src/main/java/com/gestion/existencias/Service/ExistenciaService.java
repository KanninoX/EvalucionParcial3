package com.gestion.existencias.Service;

import com.gestion.existencias.DTO.ExistenciaDTO;
import com.gestion.existencias.Model.Existencia;
import com.gestion.existencias.Repository.ExistenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExistenciaService {

    private static final Logger log = LoggerFactory.getLogger(ExistenciaService.class);

    @Autowired
    private ExistenciaRepository existenciaRepository;

    public List<Existencia> obtenerTodos() {
        log.info("[ExistenciaService] obtenerTodos - Consultando todas las existencias");
        List<Existencia> lista = existenciaRepository.findAll();
        log.info("[ExistenciaService] obtenerTodos - Encontrados {} registros", lista.size());
        return lista;
    }

    public Optional<Existencia> obtenerPorId(Long id) {
        log.info("[ExistenciaService] obtenerPorId - Buscando existencia con id={}", id);
        Optional<Existencia> resultado = existenciaRepository.findById(id);
        resultado.ifPresentOrElse(
                e -> log.info("[ExistenciaService] obtenerPorId - Encontrada: referencia='{}', cantidad={}", e.getReferencia(), e.getCantidad()),
                () -> log.warn("[ExistenciaService] obtenerPorId - Existencia con id={} no existe", id)
        );
        return resultado;
    }

    public Existencia crear(ExistenciaDTO dto) {
        log.info("[ExistenciaService] crear - Creando existencia referencia='{}', cantidad={}", dto.getReferencia(), dto.getCantidad());
        Existencia existencia = new Existencia();
        existencia.setReferencia(dto.getReferencia());
        existencia.setCantidad(dto.getCantidad());
        Existencia guardada = existenciaRepository.save(existencia);
        log.info("[ExistenciaService] crear - Existencia guardada con id={}", guardada.getId());
        return guardada;
    }

    public Optional<Existencia> actualizar(Long id, ExistenciaDTO dto) {
        log.info("[ExistenciaService] actualizar - Actualizando existencia id={}", id);
        return existenciaRepository.findById(id).map(existencia -> {
            existencia.setReferencia(dto.getReferencia());
            existencia.setCantidad(dto.getCantidad());
            Existencia guardada = existenciaRepository.save(existencia);
            log.info("[ExistenciaService] actualizar - Existencia id={} actualizada correctamente", id);
            return guardada;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[ExistenciaService] eliminar - Eliminando existencia id={}", id);
        if (existenciaRepository.existsById(id)) {
            existenciaRepository.deleteById(id);
            log.info("[ExistenciaService] eliminar - Existencia id={} eliminada de la BD", id);
            return true;
        }
        log.warn("[ExistenciaService] eliminar - Existencia id={} no encontrada", id);
        return false;
    }
}
