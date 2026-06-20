package com.gestion.proveedores.Service;

import com.gestion.proveedores.DTO.ProveedorDTO;
import com.gestion.proveedores.Model.Proveedor;
import com.gestion.proveedores.Repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private static final Logger log = LoggerFactory.getLogger(ProveedorService.class);

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> obtenerTodos() {
        log.info("[ProveedorService] obtenerTodos - Consultando todos los proveedores");
        List<Proveedor> lista = proveedorRepository.findAll();
        log.info("[ProveedorService] obtenerTodos - Encontrados {} proveedores", lista.size());
        return lista;
    }

    public Optional<Proveedor> obtenerPorId(Long id) {
        log.info("[ProveedorService] obtenerPorId - Buscando proveedor con id={}", id);
        Optional<Proveedor> resultado = proveedorRepository.findById(id);
        resultado.ifPresentOrElse(
                p -> log.info("[ProveedorService] obtenerPorId - Proveedor encontrado: '{}'", p.getNombre()),
                () -> log.warn("[ProveedorService] obtenerPorId - Proveedor con id={} no existe", id)
        );
        return resultado;
    }

    public Proveedor crear(ProveedorDTO dto) {
        log.info("[ProveedorService] crear - Creando proveedor nombre='{}', nif='{}'", dto.getNombre(), dto.getNif());
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setNif(dto.getNif());
        proveedor.setEstado("ACTIVO");
        Proveedor guardado = proveedorRepository.save(proveedor);
        log.info("[ProveedorService] crear - Proveedor guardado con id={}", guardado.getId());
        return guardado;
    }

    public Optional<Proveedor> actualizar(Long id, ProveedorDTO dto) {
        log.info("[ProveedorService] actualizar - Actualizando proveedor id={}", id);
        return proveedorRepository.findById(id).map(proveedor -> {
            proveedor.setNombre(dto.getNombre());
            proveedor.setNif(dto.getNif());
            if (dto.getEstado() != null) {
                proveedor.setEstado(dto.getEstado());
            }
            Proveedor guardado = proveedorRepository.save(proveedor);
            log.info("[ProveedorService] actualizar - Proveedor id={} actualizado correctamente", id);
            return guardado;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[ProveedorService] eliminar - Desactivando proveedor id={}", id);
        return proveedorRepository.findById(id).map(proveedor -> {
            proveedor.setEstado("INACTIVO");
            proveedorRepository.save(proveedor);
            log.info("[ProveedorService] eliminar - Proveedor id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[ProveedorService] eliminar - Proveedor id={} no encontrado", id);
            return false;
        });
    }
}
