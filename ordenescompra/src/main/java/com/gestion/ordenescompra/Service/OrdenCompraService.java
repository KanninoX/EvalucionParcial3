package com.gestion.ordenescompra.Service;

import com.gestion.ordenescompra.Client.ClienteClient;
import com.gestion.ordenescompra.DTO.OrdenCompraDTO;
import com.gestion.ordenescompra.Model.OrdenCompra;
import com.gestion.ordenescompra.Repository.OrdenCompraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrdenCompraService {

    private static final Logger log = LoggerFactory.getLogger(OrdenCompraService.class);

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private ClienteClient clienteClient;

    public List<OrdenCompra> obtenerTodos() {
        log.info("[OrdenCompraService] obtenerTodos - Consultando todas las órdenes de compra");
        List<OrdenCompra> lista = ordenCompraRepository.findAll();
        log.info("[OrdenCompraService] obtenerTodos - Encontradas {} órdenes", lista.size());
        return lista;
    }

    public Optional<OrdenCompra> obtenerPorId(Long id) {
        log.info("[OrdenCompraService] obtenerPorId - Buscando orden de compra con id={}", id);
        Optional<OrdenCompra> resultado = ordenCompraRepository.findById(id);
        resultado.ifPresentOrElse(
                o -> log.info("[OrdenCompraService] obtenerPorId - Encontrada: numero='{}', estado='{}', clienteId={}",
                        o.getNumero(), o.getEstado(), o.getClienteId()),
                () -> log.warn("[OrdenCompraService] obtenerPorId - Orden con id={} no existe", id)
        );
        return resultado;
    }

    public OrdenCompra crear(OrdenCompraDTO dto) {
        log.info("[OrdenCompraService] crear - Creando orden numero='{}', clienteId={}", dto.getNumero(), dto.getClienteId());

        if (dto.getClienteId() != null) {
            Map<String, Object> cliente = clienteClient.obtenerCliente(dto.getClienteId());
            if (cliente != null) {
                log.info("[OrdenCompraService] crear - Cliente id={} verificado: nombre='{}'",
                        dto.getClienteId(), cliente.get("nombre"));
            } else {
                log.warn("[OrdenCompraService] crear - Cliente id={} no encontrado en servicio Clientes, la orden se registra igualmente", dto.getClienteId());
            }
        }

        OrdenCompra orden = new OrdenCompra();
        orden.setNumero(dto.getNumero());
        orden.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        orden.setClienteId(dto.getClienteId());
        OrdenCompra guardada = ordenCompraRepository.save(orden);
        log.info("[OrdenCompraService] crear - Orden guardada con id={}, estado='{}', clienteId={}",
                guardada.getId(), guardada.getEstado(), guardada.getClienteId());
        return guardada;
    }

    public Optional<OrdenCompra> actualizar(Long id, OrdenCompraDTO dto) {
        log.info("[OrdenCompraService] actualizar - Actualizando orden id={}", id);
        return ordenCompraRepository.findById(id).map(orden -> {
            orden.setNumero(dto.getNumero());
            if (dto.getEstado() != null) {
                orden.setEstado(dto.getEstado());
            }
            if (dto.getClienteId() != null) {
                orden.setClienteId(dto.getClienteId());
            }
            OrdenCompra guardada = ordenCompraRepository.save(orden);
            log.info("[OrdenCompraService] actualizar - Orden id={} actualizada, estado='{}'", id, guardada.getEstado());
            return guardada;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[OrdenCompraService] eliminar - Eliminando orden id={}", id);
        if (ordenCompraRepository.existsById(id)) {
            ordenCompraRepository.deleteById(id);
            log.info("[OrdenCompraService] eliminar - Orden id={} eliminada de la BD", id);
            return true;
        }
        log.warn("[OrdenCompraService] eliminar - Orden id={} no encontrada", id);
        return false;
    }
}
