package com.gestion.Clientes.Service;

import com.gestion.Clientes.DTO.ClienteDTO;
import com.gestion.Clientes.Model.Cliente;
import com.gestion.Clientes.Repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> obtenerTodos() {
        log.info("[ClienteService] obtenerTodos - Consultando todos los clientes");
        List<Cliente> lista = clienteRepository.findAll();
        log.info("[ClienteService] obtenerTodos - Encontrados {} clientes", lista.size());
        return lista;
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        log.info("[ClienteService] obtenerPorId - Buscando cliente con id={}", id);
        Optional<Cliente> resultado = clienteRepository.findById(id);
        resultado.ifPresentOrElse(
                c -> log.info("[ClienteService] obtenerPorId - Encontrado: nombre='{}'", c.getNombre()),
                () -> log.warn("[ClienteService] obtenerPorId - Cliente con id={} no existe", id)
        );
        return resultado;
    }

    public Cliente crear(ClienteDTO dto) {
        log.info("[ClienteService] crear - Creando cliente nombre='{}'", dto.getNombre());
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setEstado("ACTIVO");
        Cliente guardado = clienteRepository.save(cliente);
        log.info("[ClienteService] crear - Cliente guardado con id={}", guardado.getId());
        return guardado;
    }

    public Optional<Cliente> actualizar(Long id, ClienteDTO dto) {
        log.info("[ClienteService] actualizar - Actualizando cliente id={}", id);
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(dto.getNombre());
            cliente.setApellido(dto.getApellido());
            cliente.setEmail(dto.getEmail());
            cliente.setTelefono(dto.getTelefono());
            cliente.setDireccion(dto.getDireccion());
            Cliente guardado = clienteRepository.save(cliente);
            log.info("[ClienteService] actualizar - Cliente id={} actualizado", id);
            return guardado;
        });
    }

    public boolean eliminar(Long id) {
        log.info("[ClienteService] eliminar - Soft-delete cliente id={}", id);
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setEstado("INACTIVO");
            clienteRepository.save(cliente);
            log.info("[ClienteService] eliminar - Cliente id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[ClienteService] eliminar - Cliente id={} no encontrado", id);
            return false;
        });
    }
}
