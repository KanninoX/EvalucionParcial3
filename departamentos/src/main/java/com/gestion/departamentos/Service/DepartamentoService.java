package com.gestion.departamentos.Service;

import com.gestion.departamentos.DTO.DepartamentoDTO;
import com.gestion.departamentos.Model.Departamento;
import com.gestion.departamentos.Repository.DepartamentoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartamentoService {

    private static final Logger log = LoggerFactory.getLogger(DepartamentoService.class);

    @Autowired
    private DepartamentoRepository repo;

    public List<Departamento> obtenerTodos() {
        log.info("[DepartamentoService] Obteniendo todos los departamentos");
        List<Departamento> lista = repo.findAll();
        log.info("[DepartamentoService] Se encontraron {} departamentos", lista.size());
        return lista;
    }

    public Optional<Departamento> obtenerPorId(Integer id) {
        log.info("[DepartamentoService] Buscando departamento id={}", id);
        Optional<Departamento> dep = repo.findById(id);
        if (dep.isPresent()) {
            log.info("[DepartamentoService] Departamento encontrado: '{}'", dep.get().getNombre());
        } else {
            log.warn("[DepartamentoService] No se encontró departamento id={}", id);
        }
        return dep;
    }

    public Departamento crear(DepartamentoDTO dto) {
        log.info("[DepartamentoService] Creando departamento '{}'", dto.getNombre());
        Departamento d = new Departamento();
        d.setNombre(dto.getNombre());
        d.setDescripcion(dto.getDescripcion());
        d.setEstado("ACTIVO");
        Departamento guardado = repo.save(d);
        log.info("[DepartamentoService] Departamento '{}' creado con id={}", guardado.getNombre(), guardado.getId());
        return guardado;
    }

    public Optional<Departamento> actualizar(Integer id, DepartamentoDTO dto) {
        log.info("[DepartamentoService] Actualizando departamento id={}", id);
        return repo.findById(id).map(d -> {
            d.setNombre(dto.getNombre());
            d.setDescripcion(dto.getDescripcion());
            Departamento actualizado = repo.save(d);
            log.info("[DepartamentoService] Departamento id={} actualizado a '{}'", id, actualizado.getNombre());
            return actualizado;
        });
    }

    public boolean eliminar(Integer id) {
        log.info("[DepartamentoService] Desactivando departamento id={}", id);
        return repo.findById(id).map(d -> {
            d.setEstado("INACTIVO");
            repo.save(d);
            log.info("[DepartamentoService] Departamento id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[DepartamentoService] No se encontró departamento id={} para eliminar", id);
            return false;
        });
    }
}
