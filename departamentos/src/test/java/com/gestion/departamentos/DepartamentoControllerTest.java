package com.gestion.departamentos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.departamentos.Assemblers.DepartamentoModelAssembler;
import com.gestion.departamentos.Controller.DepartamentoController;
import com.gestion.departamentos.DTO.DepartamentoDTO;
import com.gestion.departamentos.Model.Departamento;
import com.gestion.departamentos.Security.JwtFilter;
import com.gestion.departamentos.Security.JwtUtil;
import com.gestion.departamentos.Service.DepartamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartamentoController.class)
@Import({DepartamentoModelAssembler.class, JwtFilter.class})
class DepartamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartamentoService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Departamento departamento;

    @BeforeEach
    void setUp() {
        departamento = new Departamento(1, "Recursos Humanos", "Gestión del personal", "ACTIVO");
    }

    // ===== GET /departamentos (público) =====

    @Test
    void listar_sinAutenticacion_retornaOk() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(departamento));

        mockMvc.perform(get("/api/v1/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departamentoList[0].nombre").value("Recursos Humanos"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void listar_variousDepartamentos_retornaColeccion() throws Exception {
        Departamento d2 = new Departamento(2, "TI", "Tecnología", "ACTIVO");
        when(service.obtenerTodos()).thenReturn(List.of(departamento, d2));

        mockMvc.perform(get("/api/v1/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.departamentoList.length()").value(2));
    }

    // ===== GET /departamentos/{id} (público) =====

    @Test
    void obtener_departamentoExistente_retorna200ConHateoas() throws Exception {
        when(service.obtenerPorId(1)).thenReturn(Optional.of(departamento));

        mockMvc.perform(get("/api/v1/departamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Recursos Humanos"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.departamentos").exists());
    }

    @Test
    void obtener_departamentoInexistente_retorna404() throws Exception {
        when(service.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/departamentos/99"))
                .andExpect(status().isNotFound());
    }

    // ===== POST /departamentos =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_departamentoValido_retorna200() throws Exception {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setNombre("Contabilidad");
        dto.setDescripcion("Área contable");

        when(service.crear(any(DepartamentoDTO.class))).thenReturn(
                new Departamento(3, "Contabilidad", "Área contable", "ACTIVO"));

        mockMvc.perform(post("/api/v1/departamentos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Contabilidad"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_sinAutenticacion_retorna401() throws Exception {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setNombre("Marketing");

        mockMvc.perform(post("/api/v1/departamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ===== PUT /departamentos/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_departamentoExistente_retorna200() throws Exception {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setNombre("RRHH Actualizado");
        dto.setDescripcion("Nueva descripción");

        when(service.actualizar(eq(1), any(DepartamentoDTO.class)))
                .thenReturn(Optional.of(new Departamento(1, "RRHH Actualizado", "Nueva descripción", "ACTIVO")));

        mockMvc.perform(put("/api/v1/departamentos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("RRHH Actualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_departamentoInexistente_retorna404() throws Exception {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setNombre("Ghost");

        when(service.actualizar(eq(99), any(DepartamentoDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/departamentos/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== DELETE /departamentos/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_departamentoExistente_retorna204() throws Exception {
        when(service.eliminar(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/departamentos/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_departamentoInexistente_retorna404() throws Exception {
        when(service.eliminar(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/departamentos/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
