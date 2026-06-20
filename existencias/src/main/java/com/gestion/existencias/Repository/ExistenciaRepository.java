package com.gestion.existencias.Repository;

import com.gestion.existencias.Model.Existencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExistenciaRepository extends JpaRepository<Existencia, Long> {
}
