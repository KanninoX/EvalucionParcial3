package com.gestion.empleados.Service;

import com.gestion.empleados.DTO.CargoDTO;
import com.gestion.empleados.DTO.DepartamentoDTO;
import com.gestion.empleados.DTO.EmpleadoDTO;
import com.gestion.empleados.Model.Empleado;
import com.gestion.empleados.Repository.EmpleadoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);

    @Autowired
    private EmpleadoRepository repo;

    @Autowired
    @Qualifier("webClientDepartamentos")
    private WebClient webClientDepartamentos;

    @Autowired
    @Qualifier("webClientCargos")
    private WebClient webClientCargos;

    public List<Empleado> obtenerTodos() {
        log.info("[EmpleadoService] Obteniendo lista completa de empleados");
        List<Empleado> lista = repo.findAll();
        log.info("[EmpleadoService] Se encontraron {} empleados", lista.size());
        return lista;
    }

    public Optional<Empleado> obtenerPorId(Integer id) {
        log.info("[EmpleadoService] Buscando empleado con id={}", id);
        Optional<Empleado> emp = repo.findById(id);
        if (emp.isPresent()) {
            log.info("[EmpleadoService] Empleado encontrado: '{} {}'", emp.get().getNombre(), emp.get().getApellido());
        } else {
            log.warn("[EmpleadoService] No se encontró empleado con id={}", id);
        }
        return emp;
    }

    public Boolean crearEmpleado(EmpleadoDTO dto) {
        log.info("[EmpleadoService] Creando empleado '{} {}', dpto={}, cargo={}",
                dto.getNombre(), dto.getApellido(), dto.getDepartamentoId(), dto.getCargoId());

        DepartamentoDTO depto;
        try {
            depto = webClientDepartamentos.get()
                    .uri("/departamentos/{id}", dto.getDepartamentoId())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new RuntimeException("Departamento no encontrado")))
                    .bodyToMono(DepartamentoDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("[EmpleadoService] Error al consultar departamento id={}: {}", dto.getDepartamentoId(), e.getMessage());
            return false;
        }

        if (depto == null) {
            log.warn("[EmpleadoService] Departamento id={} no existe", dto.getDepartamentoId());
            return false;
        }

        CargoDTO cargo;
        try {
            cargo = webClientCargos.get()
                    .uri("/cargos/{id}", dto.getCargoId())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            Mono.error(new RuntimeException("Cargo no encontrado")))
                    .bodyToMono(CargoDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("[EmpleadoService] Error al consultar cargo id={}: {}", dto.getCargoId(), e.getMessage());
            return false;
        }

        if (cargo == null) {
            log.warn("[EmpleadoService] Cargo id={} no existe", dto.getCargoId());
            return false;
        }

        Empleado emp = new Empleado();
        emp.setNombre(dto.getNombre());
        emp.setApellido(dto.getApellido());
        emp.setEmail(dto.getEmail());
        emp.setTelefono(dto.getTelefono());
        emp.setDepartamentoId(dto.getDepartamentoId());
        emp.setCargoId(dto.getCargoId());
        emp.setEstado("ACTIVO");
        repo.save(emp);

        log.info("[EmpleadoService] Empleado '{} {}' creado con éxito", emp.getNombre(), emp.getApellido());
        return true;
    }

    public Optional<Empleado> actualizar(Integer id, EmpleadoDTO dto) {
        log.info("[EmpleadoService] Actualizando empleado id={}", id);
        return repo.findById(id).map(e -> {
            e.setNombre(dto.getNombre());
            e.setApellido(dto.getApellido());
            e.setEmail(dto.getEmail());
            e.setTelefono(dto.getTelefono());
            e.setDepartamentoId(dto.getDepartamentoId());
            e.setCargoId(dto.getCargoId());
            Empleado actualizado = repo.save(e);
            log.info("[EmpleadoService] Empleado id={} actualizado correctamente", id);
            return actualizado;
        });
    }

    public boolean eliminar(Integer id) {
        log.info("[EmpleadoService] Desactivando empleado id={}", id);
        return repo.findById(id).map(e -> {
            e.setEstado("INACTIVO");
            repo.save(e);
            log.info("[EmpleadoService] Empleado id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[EmpleadoService] No se encontró empleado id={} para eliminar", id);
            return false;
        });
    }
}
