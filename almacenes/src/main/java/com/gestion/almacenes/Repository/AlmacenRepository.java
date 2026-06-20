package com.gestion.almacenes.Repository;

import com.gestion.almacenes.Model.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
}
