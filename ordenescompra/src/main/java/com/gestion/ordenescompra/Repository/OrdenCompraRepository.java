package com.gestion.ordenescompra.Repository;

import com.gestion.ordenescompra.Model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
}
