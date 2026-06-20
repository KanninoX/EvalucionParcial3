package com.gestion.auditoria.Repository;

import com.gestion.auditoria.Model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findByTabla(String tabla);
    List<Auditoria> findByUsuario(String usuario);
}
