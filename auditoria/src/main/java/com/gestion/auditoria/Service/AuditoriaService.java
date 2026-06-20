package com.gestion.auditoria.Service;

import com.gestion.auditoria.DTO.AuditoriaDTO;
import com.gestion.auditoria.Model.Auditoria;
import com.gestion.auditoria.Repository.AuditoriaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    @Autowired
    private AuditoriaRepository repo;

    public List<Auditoria> obtenerTodas() {
        log.info("[AuditoriaService] Obteniendo todos los registros de auditoría");
        List<Auditoria> lista = repo.findAll();
        log.info("[AuditoriaService] Se encontraron {} registros", lista.size());
        return lista;
    }

    public Optional<Auditoria> obtenerPorId(Integer id) {
        log.info("[AuditoriaService] Buscando registro de auditoría id={}", id);
        Optional<Auditoria> registro = repo.findById(id);
        if (registro.isPresent()) {
            log.info("[AuditoriaService] Registro encontrado: accion='{}', tabla='{}'",
                    registro.get().getAccion(), registro.get().getTabla());
        } else {
            log.warn("[AuditoriaService] No se encontró registro id={}", id);
        }
        return registro;
    }

    public List<Auditoria> obtenerPorTabla(String tabla) {
        log.info("[AuditoriaService] Filtrando registros por tabla='{}'", tabla);
        List<Auditoria> lista = repo.findByTabla(tabla);
        log.info("[AuditoriaService] {} registros encontrados para tabla='{}'", lista.size(), tabla);
        return lista;
    }

    public List<Auditoria> obtenerPorUsuario(String usuario) {
        log.info("[AuditoriaService] Filtrando registros por usuario='{}'", usuario);
        List<Auditoria> lista = repo.findByUsuario(usuario);
        log.info("[AuditoriaService] {} registros encontrados para usuario='{}'", lista.size(), usuario);
        return lista;
    }

    public Auditoria registrar(AuditoriaDTO dto) {
        log.info("[AuditoriaService] Registrando auditoría: accion='{}', tabla='{}', registroId={}, usuario='{}'",
                dto.getAccion(), dto.getTabla(), dto.getRegistroId(), dto.getUsuario());
        Auditoria a = new Auditoria();
        a.setAccion(dto.getAccion());
        a.setTabla(dto.getTabla());
        a.setRegistroId(dto.getRegistroId());
        a.setUsuario(dto.getUsuario());
        a.setFechaHora(LocalDateTime.now());
        a.setDetalles(dto.getDetalles());
        Auditoria guardada = repo.save(a);
        log.info("[AuditoriaService] Registro de auditoría creado con id={}", guardada.getId());
        return guardada;
    }
}
