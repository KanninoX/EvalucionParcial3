package com.Operaciones.Pagos.Service;

import com.Operaciones.Pagos.DTO.PagoDTO;
import com.Operaciones.Pagos.Model.Pago;
import com.Operaciones.Pagos.Repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    @Autowired
    private PagoRepository pagoRepository;

    public List<Pago> obtenerTodos() {
        log.info("[PagoService] obtenerTodos - Consultando todos los pagos");
        List<Pago> lista = pagoRepository.findAll();
        log.info("[PagoService] obtenerTodos - Encontrados {} pagos", lista.size());
        return lista;
    }

    public Optional<Pago> obtenerPorId(Long id) {
        log.info("[PagoService] obtenerPorId - Buscando pago con id={}", id);
        Optional<Pago> resultado = pagoRepository.findById(id);
        resultado.ifPresentOrElse(
                p -> log.info("[PagoService] obtenerPorId - Encontrado: monto={}, estado='{}'", p.getMonto(), p.getEstado()),
                () -> log.warn("[PagoService] obtenerPorId - Pago con id={} no existe", id)
        );
        return resultado;
    }

    public Pago crear(PagoDTO dto) {
        log.info("[PagoService] crear - Creando pago ventaId={}, monto={}", dto.getVentaId(), dto.getMonto());
        Pago pago = new Pago();
        pago.setVentaId(dto.getVentaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        pago.setFecha(LocalDateTime.now());
        Pago guardado = pagoRepository.save(pago);
        log.info("[PagoService] crear - Pago guardado con id={}", guardado.getId());
        return guardado;
    }

    public Optional<Pago> actualizar(Long id, PagoDTO dto) {
        log.info("[PagoService] actualizar - Actualizando pago id={}", id);
        return pagoRepository.findById(id).map(pago -> {
            pago.setVentaId(dto.getVentaId());
            pago.setMonto(dto.getMonto());
            pago.setMetodoPago(dto.getMetodoPago());
            if (dto.getEstado() != null) {
                pago.setEstado(dto.getEstado());
            }
            Pago guardado = pagoRepository.save(pago);
            log.info("[PagoService] actualizar - Pago id={} actualizado, estado='{}'", id, guardado.getEstado());
            return guardado;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[PagoService] eliminar - Eliminando pago id={}", id);
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
            log.info("[PagoService] eliminar - Pago id={} eliminado", id);
            return true;
        }
        log.warn("[PagoService] eliminar - Pago id={} no encontrado", id);
        return false;
    }
}
