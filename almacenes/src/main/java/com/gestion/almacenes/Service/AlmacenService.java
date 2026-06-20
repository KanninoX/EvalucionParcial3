package com.gestion.almacenes.Service;

import com.gestion.almacenes.DTO.AlmacenDTO;
import com.gestion.almacenes.Model.Almacen;
import com.gestion.almacenes.Repository.AlmacenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlmacenService {

    private static final Logger log = LoggerFactory.getLogger(AlmacenService.class);

    @Autowired
    private AlmacenRepository almacenRepository;

    public List<Almacen> obtenerTodos() {
        log.info("[AlmacenService] obtenerTodos - Consultando todos los almacenes");
        List<Almacen> lista = almacenRepository.findAll();
        log.info("[AlmacenService] obtenerTodos - Encontrados {} almacenes", lista.size());
        return lista;
    }

    public Optional<Almacen> obtenerPorId(Long id) {
        log.info("[AlmacenService] obtenerPorId - Buscando almacen con id={}", id);
        Optional<Almacen> resultado = almacenRepository.findById(id);
        resultado.ifPresentOrElse(
                a -> log.info("[AlmacenService] obtenerPorId - Almacen encontrado: '{}'", a.getNombre()),
                () -> log.warn("[AlmacenService] obtenerPorId - Almacen con id={} no existe", id)
        );
        return resultado;
    }

    public Almacen crear(AlmacenDTO dto) {
        log.info("[AlmacenService] crear - Creando almacen nombre='{}', ubicacion='{}'", dto.getNombre(), dto.getUbicacion());
        Almacen almacen = new Almacen();
        almacen.setNombre(dto.getNombre());
        almacen.setUbicacion(dto.getUbicacion());
        almacen.setEstado("ACTIVO");
        Almacen guardado = almacenRepository.save(almacen);
        log.info("[AlmacenService] crear - Almacen guardado con id={}", guardado.getId());
        return guardado;
    }

    public Optional<Almacen> actualizar(Long id, AlmacenDTO dto) {
        log.info("[AlmacenService] actualizar - Actualizando almacen id={}", id);
        return almacenRepository.findById(id).map(almacen -> {
            almacen.setNombre(dto.getNombre());
            almacen.setUbicacion(dto.getUbicacion());
            if (dto.getEstado() != null) {
                almacen.setEstado(dto.getEstado());
            }
            Almacen guardado = almacenRepository.save(almacen);
            log.info("[AlmacenService] actualizar - Almacen id={} actualizado correctamente", id);
            return guardado;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[AlmacenService] eliminar - Desactivando almacen id={}", id);
        return almacenRepository.findById(id).map(almacen -> {
            almacen.setEstado("INACTIVO");
            almacenRepository.save(almacen);
            log.info("[AlmacenService] eliminar - Almacen id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[AlmacenService] eliminar - Almacen id={} no encontrado", id);
            return false;
        });
    }
}
