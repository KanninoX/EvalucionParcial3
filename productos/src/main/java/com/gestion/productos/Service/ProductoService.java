package com.gestion.productos.Service;

import com.gestion.productos.DTO.ProductoDTO;
import com.gestion.productos.Model.Producto;
import com.gestion.productos.Repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        log.info("[ProductoService] obtenerTodos - Consultando todos los productos");
        List<Producto> lista = productoRepository.findAll();
        log.info("[ProductoService] obtenerTodos - Encontrados {} productos", lista.size());
        return lista;
    }

    public Optional<Producto> obtenerPorId(Long id) {
        log.info("[ProductoService] obtenerPorId - Buscando producto con id={}", id);
        Optional<Producto> resultado = productoRepository.findById(id);
        resultado.ifPresentOrElse(
                p -> log.info("[ProductoService] obtenerPorId - Producto encontrado: '{}'", p.getNombre()),
                () -> log.warn("[ProductoService] obtenerPorId - Producto con id={} no existe", id)
        );
        return resultado;
    }

    public Producto crear(ProductoDTO dto) {
        log.info("[ProductoService] crear - Creando producto nombre='{}', codigo='{}'", dto.getNombre(), dto.getCodigo());
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setCodigo(dto.getCodigo());
        producto.setEstado("ACTIVO");
        Producto guardado = productoRepository.save(producto);
        log.info("[ProductoService] crear - Producto guardado con id={}", guardado.getId());
        return guardado;
    }

    public Optional<Producto> actualizar(Long id, ProductoDTO dto) {
        log.info("[ProductoService] actualizar - Actualizando producto id={}", id);
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(dto.getNombre());
            producto.setCodigo(dto.getCodigo());
            if (dto.getEstado() != null) {
                producto.setEstado(dto.getEstado());
            }
            Producto guardado = productoRepository.save(producto);
            log.info("[ProductoService] actualizar - Producto id={} actualizado correctamente", id);
            return guardado;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[ProductoService] eliminar - Desactivando producto id={}", id);
        return productoRepository.findById(id).map(producto -> {
            producto.setEstado("INACTIVO");
            productoRepository.save(producto);
            log.info("[ProductoService] eliminar - Producto id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[ProductoService] eliminar - Producto id={} no encontrado", id);
            return false;
        });
    }
}
