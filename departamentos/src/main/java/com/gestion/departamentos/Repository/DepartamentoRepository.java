package com.gestion.departamentos.Repository;

import com.gestion.departamentos.Model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
}
