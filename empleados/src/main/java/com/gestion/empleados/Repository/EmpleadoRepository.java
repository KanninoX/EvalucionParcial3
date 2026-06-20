package com.gestion.empleados.Repository;

import com.gestion.empleados.Model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByEmail(String email);
    List<Empleado> findByEstado(String estado);
}
