package com.gestion.cargos.Service;

import com.gestion.cargos.DTO.CargoDTO;
import com.gestion.cargos.DTO.DepartamentoDTO;
import com.gestion.cargos.Model.Cargo;
import com.gestion.cargos.Repository.CargoRepository;
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
public class CargoService {

    private static final Logger log = LoggerFactory.getLogger(CargoService.class);

    @Autowired
    private CargoRepository repo;

    @Autowired
    @Qualifier("webClientDepartamentos")
    private WebClient webClientDepartamentos;

    public List<Cargo> obtenerTodos() {
        log.info("[CargoService] Obteniendo todos los cargos");
        List<Cargo> lista = repo.findAll();
        log.info("[CargoService] Se encontraron {} cargos", lista.size());
        return lista;
    }

    public Optional<Cargo> obtenerPorId(Integer id) {
        log.info("[CargoService] Buscando cargo id={}", id);
        Optional<Cargo> cargo = repo.findById(id);
        if (cargo.isPresent()) {
            log.info("[CargoService] Cargo encontrado: '{}'", cargo.get().getNombre());
        } else {
            log.warn("[CargoService] No se encontró cargo id={}", id);
        }
        return cargo;
    }

    public Boolean crearCargo(CargoDTO dto) {
        log.info("[CargoService] Creando cargo '{}', dpto={}", dto.getNombre(), dto.getDepartamentoId());

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
            log.error("[CargoService] Error al consultar departamento id={}: {}", dto.getDepartamentoId(), e.getMessage());
            return false;
        }

        if (depto == null) {
            log.warn("[CargoService] Departamento id={} no existe, cargo no creado", dto.getDepartamentoId());
            return false;
        }

        Cargo c = new Cargo();
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        c.setDepartamentoId(dto.getDepartamentoId());
        c.setEstado("ACTIVO");
        repo.save(c);

        log.info("[CargoService] Cargo '{}' creado exitosamente en departamento id={}", c.getNombre(), c.getDepartamentoId());
        return true;
    }

    public Optional<Cargo> actualizar(Integer id, CargoDTO dto) {
        log.info("[CargoService] Actualizando cargo id={}", id);
        return repo.findById(id).map(c -> {
            c.setNombre(dto.getNombre());
            c.setDescripcion(dto.getDescripcion());
            c.setDepartamentoId(dto.getDepartamentoId());
            Cargo actualizado = repo.save(c);
            log.info("[CargoService] Cargo id={} actualizado a '{}'", id, actualizado.getNombre());
            return actualizado;
        });
    }

    public boolean eliminar(Integer id) {
        log.info("[CargoService] Desactivando cargo id={}", id);
        return repo.findById(id).map(c -> {
            c.setEstado("INACTIVO");
            repo.save(c);
            log.info("[CargoService] Cargo id={} marcado como INACTIVO", id);
            return true;
        }).orElseGet(() -> {
            log.warn("[CargoService] No se encontró cargo id={} para eliminar", id);
            return false;
        });
    }
}
