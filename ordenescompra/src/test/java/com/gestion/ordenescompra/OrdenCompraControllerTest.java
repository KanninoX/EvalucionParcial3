package com.gestion.ordenescompra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.ordenescompra.Assemblers.OrdenCompraModelAssembler;
import com.gestion.ordenescompra.Controller.OrdenCompraController;
import com.gestion.ordenescompra.DTO.OrdenCompraDTO;
import com.gestion.ordenescompra.Model.OrdenCompra;
import com.gestion.ordenescompra.Security.JwtFilter;
import com.gestion.ordenescompra.Security.JwtUtil;
import com.gestion.ordenescompra.Service.OrdenCompraService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenCompraController.class)
@Import({OrdenCompraModelAssembler.class, JwtFilter.class})
class OrdenCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrdenCompraService ordenCompraService;

    @MockBean
    private JwtUtil jwtUtil;

    private OrdenCompra ordenEjemplo() {
        return new OrdenCompra(1L, "OC-2024-001", "PENDIENTE", 10L);
    }

    private OrdenCompraDTO dtoEjemplo() {
        OrdenCompraDTO dto = new OrdenCompraDTO();
        dto.setNumero("OC-2024-001");
        dto.setEstado("PENDIENTE");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(ordenCompraService.obtenerTodos()).thenReturn(List.of(ordenEjemplo()));

        mockMvc.perform(get("/api/v1/ordenescompra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ordenCompraList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(ordenCompraService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ordenescompra"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(ordenCompraService.obtenerPorId(1L)).thenReturn(Optional.of(ordenEjemplo()));

        mockMvc.perform(get("/api/v1/ordenescompra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("OC-2024-001"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(ordenCompraService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/ordenescompra/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(ordenCompraService.crear(any())).thenReturn(ordenEjemplo());

        mockMvc.perform(post("/api/v1/ordenescompra")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("OC-2024-001"));
    }

    @Test
    @WithMockUser
    void crear_sinNumero_retorna400() throws Exception {
        OrdenCompraDTO dto = new OrdenCompraDTO();

        mockMvc.perform(post("/api/v1/ordenescompra")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        OrdenCompra actualizada = new OrdenCompra(1L, "OC-2024-001", "APROBADA", 10L);
        when(ordenCompraService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizada));

        mockMvc.perform(put("/api/v1/ordenescompra/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADA"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(ordenCompraService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/ordenescompra/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(ordenCompraService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/ordenescompra/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(ordenCompraService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/ordenescompra/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
