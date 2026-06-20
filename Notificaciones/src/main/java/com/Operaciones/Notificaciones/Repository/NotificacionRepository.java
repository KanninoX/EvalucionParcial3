package com.Operaciones.Notificaciones.Repository;

import com.Operaciones.Notificaciones.Model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}