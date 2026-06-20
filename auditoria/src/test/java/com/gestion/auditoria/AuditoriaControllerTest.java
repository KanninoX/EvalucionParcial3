package com.gestion.auditoria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.auditoria.Assemblers.AuditoriaModelAssembler;
import com.gestion.auditoria.Controller.AuditoriaController;
import com.gestion.auditoria.DTO.AuditoriaDTO;
import com.gestion.auditoria.Model.Auditoria;
import com.gestion.auditoria.Security.JwtFilter;
import com.gestion.auditoria.Security.JwtUtil;
import com.gestion.auditoria.Service.AuditoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditoriaController.class)
@Import({AuditoriaModelAssembler.class, JwtFilter.class})
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditoriaService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Auditoria auditoria;

    @BeforeEach
    void setUp() {
        auditoria = new Auditoria(1, "CREATE", "empleados", 5, "jdoe",
                LocalDateTime.now(), "Empleado creado desde API");
    }

    // ===== GET /auditoria =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_retornaColeccionConHateoas() throws Exception {
        when(service.obtenerTodas()).thenReturn(List.of(auditoria));

        mockMvc.perform(get("/api/v1/auditoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.auditoriaList[0].accion").value("CREATE"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_listaVacia_retornaColeccionVacia() throws Exception {
        when(service.obtenerTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/auditoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    // ===== GET /auditoria/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtener_registroExistente_retorna200ConHateoas() throws Exception {
        when(service.obtenerPorId(1)).thenReturn(Optional.of(auditoria));

        mockMvc.perform(get("/api/v1/auditoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accion").value("CREATE"))
                .andExpect(jsonPath("$.tabla").value("empleados"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.auditorias").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtener_registroInexistente_retorna404() throws Exception {
        when(service.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/auditoria/99"))
                .andExpect(status().isNotFound());
    }

    // ===== GET /auditoria/tabla/{tabla} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void porTabla_conRegistros_retornaColeccion() throws Exception {
        when(service.obtenerPorTabla("empleados")).thenReturn(List.of(auditoria));

        mockMvc.perform(get("/api/v1/auditoria/tabla/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.auditoriaList[0].tabla").value("empleados"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void porTabla_sinRegistros_retornaColeccionVacia() throws Exception {
        when(service.obtenerPorTabla("noExiste")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/auditoria/tabla/noExiste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    // ===== GET /auditoria/usuario/{usuario} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void porUsuario_conRegistros_retornaColeccion() throws Exception {
        when(service.obtenerPorUsuario("jdoe")).thenReturn(List.of(auditoria));

        mockMvc.perform(get("/api/v1/auditoria/usuario/jdoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.auditoriaList[0].usuario").value("jdoe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void porUsuario_sinRegistros_retornaColeccionVacia() throws Exception {
        when(service.obtenerPorUsuario("ghost")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/auditoria/usuario/ghost"))
                .andExpect(status().isOk());
    }

    // ===== POST /auditoria/registrar =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void registrar_datosValidos_retorna200ConHateoas() throws Exception {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setAccion("DELETE");
        dto.setTabla("empleados");
        dto.setRegistroId(3);
        dto.setUsuario("jdoe");
        dto.setDetalles("Empleado eliminado");

        when(service.registrar(any(AuditoriaDTO.class))).thenReturn(
                new Auditoria(2, "DELETE", "empleados", 3, "jdoe",
                        LocalDateTime.now(), "Empleado eliminado"));

        mockMvc.perform(post("/api/v1/auditoria/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accion").value("DELETE"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registrar_sinAutenticacion_retorna401() throws Exception {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setAccion("CREATE");
        dto.setTabla("test");

        mockMvc.perform(post("/api/v1/auditoria/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
