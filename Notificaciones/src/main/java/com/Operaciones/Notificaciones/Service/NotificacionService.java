package com.Operaciones.Notificaciones.Service;

import com.Operaciones.Notificaciones.DTO.NotificacionDTO;
import com.Operaciones.Notificaciones.Model.Notificacion;
import com.Operaciones.Notificaciones.Repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<Notificacion> obtenerTodos() {
        log.info("[NotificacionService] obtenerTodos - Consultando todas las notificaciones");
        List<Notificacion> lista = notificacionRepository.findAll();
        log.info("[NotificacionService] obtenerTodos - Encontradas {} notificaciones", lista.size());
        return lista;
    }

    public Optional<Notificacion> obtenerPorId(Long id) {
        log.info("[NotificacionService] obtenerPorId - Buscando notificacion con id={}", id);
        Optional<Notificacion> resultado = notificacionRepository.findById(id);
        resultado.ifPresentOrElse(
                n -> log.info("[NotificacionService] obtenerPorId - Encontrada: titulo='{}'", n.getTitulo()),
                () -> log.warn("[NotificacionService] obtenerPorId - Notificacion con id={} no existe", id)
        );
        return resultado;
    }

    public Notificacion crear(NotificacionDTO dto) {
        log.info("[NotificacionService] crear - Creando notificacion titulo='{}'", dto.getTitulo());
        Notificacion notificacion = new Notificacion();
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setDestinatario(dto.getDestinatario());
        notificacion.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        notificacion.setFecha(LocalDateTime.now());
        Notificacion guardada = notificacionRepository.save(notificacion);
        log.info("[NotificacionService] crear - Notificacion guardada con id={}", guardada.getId());
        return guardada;
    }

    public Optional<Notificacion> actualizar(Long id, NotificacionDTO dto) {
        log.info("[NotificacionService] actualizar - Actualizando notificacion id={}", id);
        return notificacionRepository.findById(id).map(notificacion -> {
            notificacion.setTitulo(dto.getTitulo());
            notificacion.setMensaje(dto.getMensaje());
            notificacion.setDestinatario(dto.getDestinatario());
            if (dto.getEstado() != null) {
                notificacion.setEstado(dto.getEstado());
            }
            Notificacion guardada = notificacionRepository.save(notificacion);
            log.info("[NotificacionService] actualizar - Notificacion id={} actualizada", id);
            return guardada;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[NotificacionService] eliminar - Eliminando notificacion id={}", id);
        if (notificacionRepository.existsById(id)) {
            notificacionRepository.deleteById(id);
            log.info("[NotificacionService] eliminar - Notificacion id={} eliminada", id);
            return true;
        }
        log.warn("[NotificacionService] eliminar - Notificacion id={} no encontrada", id);
        return false;
    }
}
