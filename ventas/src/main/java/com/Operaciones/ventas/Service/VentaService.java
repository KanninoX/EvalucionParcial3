package com.Operaciones.ventas.Service;

import com.Operaciones.ventas.Client.EmpleadoClient;
import com.Operaciones.ventas.DTO.VentaDTO;
import com.Operaciones.ventas.Model.Venta;
import com.Operaciones.ventas.Repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private EmpleadoClient empleadoClient;

    public List<Venta> obtenerTodos() {
        log.info("[VentaService] obtenerTodos - Consultando todas las ventas");
        List<Venta> lista = ventaRepository.findAll();
        log.info("[VentaService] obtenerTodos - Encontradas {} ventas", lista.size());
        return lista;
    }

    public Optional<Venta> obtenerPorId(Long id) {
        log.info("[VentaService] obtenerPorId - Buscando venta con id={}", id);
        Optional<Venta> resultado = ventaRepository.findById(id);
        resultado.ifPresentOrElse(
                v -> {
                    log.info("[VentaService] obtenerPorId - Encontrada: producto='{}'", v.getProducto());
                    if (v.getEmpleadoId() != null) {
                        log.info("[VentaService] obtenerPorId - Consultando empleado id={}", v.getEmpleadoId());
                        empleadoClient.obtenerEmpleado(v.getEmpleadoId());
                    }
                },
                () -> log.warn("[VentaService] obtenerPorId - Venta con id={} no existe", id)
        );
        return resultado;
    }

    public Venta crear(VentaDTO dto) {
        log.info("[VentaService] crear - Creando venta para clienteId={}, producto='{}'", dto.getClienteId(), dto.getProducto());
        if (dto.getEmpleadoId() != null) {
            log.info("[VentaService] crear - Consultando empleado id={}", dto.getEmpleadoId());
            empleadoClient.obtenerEmpleado(dto.getEmpleadoId());
        }
        Venta venta = new Venta();
        venta.setClienteId(dto.getClienteId());
        venta.setEmpleadoId(dto.getEmpleadoId());
        venta.setProducto(dto.getProducto());
        venta.setCantidad(dto.getCantidad());
        venta.setPrecioUnitario(dto.getPrecioUnitario());
        venta.setTotal(dto.getCantidad() * dto.getPrecioUnitario());
        venta.setEstado("ACTIVO");
        venta.setFecha(LocalDateTime.now());
        Venta guardada = ventaRepository.save(venta);
        log.info("[VentaService] crear - Venta guardada con id={}", guardada.getId());
        return guardada;
    }

    public Optional<Venta> actualizar(Long id, VentaDTO dto) {
        log.info("[VentaService] actualizar - Actualizando venta id={}", id);
        return ventaRepository.findById(id).map(venta -> {
            venta.setClienteId(dto.getClienteId());
            venta.setEmpleadoId(dto.getEmpleadoId());
            venta.setProducto(dto.getProducto());
            venta.setCantidad(dto.getCantidad());
            venta.setPrecioUnitario(dto.getPrecioUnitario());
            venta.setTotal(dto.getCantidad() * dto.getPrecioUnitario());
            Venta guardada = ventaRepository.save(venta);
            log.info("[VentaService] actualizar - Venta id={} actualizada", id);
            return guardada;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[VentaService] eliminar - Soft-delete venta id={}", id);
        return ventaRepository.findById(id).map(venta -> {
            venta.setEstado("INACTIVO");
            ventaRepository.save(venta);
            log.info("[VentaService] eliminar - Venta id={} marcada como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[VentaService] eliminar - Venta id={} no encontrada", id);
            return false;
        });
    }
}
