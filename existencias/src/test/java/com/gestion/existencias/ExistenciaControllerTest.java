package com.gestion.existencias;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.existencias.Assemblers.ExistenciaModelAssembler;
import com.gestion.existencias.Controller.ExistenciaController;
import com.gestion.existencias.DTO.ExistenciaDTO;
import com.gestion.existencias.Model.Existencia;
import com.gestion.existencias.Security.JwtFilter;
import com.gestion.existencias.Security.JwtUtil;
import com.gestion.existencias.Service.ExistenciaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExistenciaController.class)
@Import({ExistenciaModelAssembler.class, JwtFilter.class})
class ExistenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExistenciaService existenciaService;

    @MockBean
    private JwtUtil jwtUtil;

    private Existencia existenciaEjemplo() {
        return new Existencia(1L, "REF-001", new BigDecimal("100.00"));
    }

    private ExistenciaDTO dtoEjemplo() {
        ExistenciaDTO dto = new ExistenciaDTO();
        dto.setReferencia("REF-001");
        dto.setCantidad(new BigDecimal("100.00"));
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(existenciaService.obtenerTodos()).thenReturn(List.of(existenciaEjemplo()));

        mockMvc.perform(get("/api/v1/existencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.existenciaList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(existenciaService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/existencias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(existenciaService.obtenerPorId(1L)).thenReturn(Optional.of(existenciaEjemplo()));

        mockMvc.perform(get("/api/v1/existencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referencia").value("REF-001"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(existenciaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/existencias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(existenciaService.crear(any())).thenReturn(existenciaEjemplo());

        mockMvc.perform(post("/api/v1/existencias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referencia").value("REF-001"));
    }

    @Test
    @WithMockUser
    void crear_datosValidos_retornaHateoas() throws Exception {
        when(existenciaService.crear(any())).thenReturn(existenciaEjemplo());

        mockMvc.perform(post("/api/v1/existencias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Existencia actualizada = new Existencia(1L, "REF-001", new BigDecimal("200.00"));
        when(existenciaService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizada));

        mockMvc.perform(put("/api/v1/existencias/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(200.00));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(existenciaService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/existencias/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(existenciaService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/existencias/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(existenciaService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/existencias/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
