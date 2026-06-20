package com.gestion.cargos.Repository;

import com.gestion.cargos.Model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargoRepository extends JpaRepository<Cargo, Integer> {
    List<Cargo> findByDepartamentoId(Integer departamentoId);
}
