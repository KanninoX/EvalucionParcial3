package com.gestion.almacenes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.almacenes.Assemblers.AlmacenModelAssembler;
import com.gestion.almacenes.Controller.AlmacenController;
import com.gestion.almacenes.DTO.AlmacenDTO;
import com.gestion.almacenes.Model.Almacen;
import com.gestion.almacenes.Security.JwtFilter;
import com.gestion.almacenes.Security.JwtUtil;
import com.gestion.almacenes.Service.AlmacenService;
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

@WebMvcTest(AlmacenController.class)
@Import({AlmacenModelAssembler.class, JwtFilter.class})
class AlmacenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlmacenService almacenService;

    @MockBean
    private JwtUtil jwtUtil;

    private Almacen almacenEjemplo() {
        return new Almacen(1L, "Almacen Central", "Calle Mayor 10", "ACTIVO");
    }

    private AlmacenDTO dtoEjemplo() {
        AlmacenDTO dto = new AlmacenDTO();
        dto.setNombre("Almacen Central");
        dto.setUbicacion("Calle Mayor 10");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(almacenService.obtenerTodos()).thenReturn(List.of(almacenEjemplo()));

        mockMvc.perform(get("/api/v1/almacenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.almacenList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(almacenService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/almacenes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(almacenService.obtenerPorId(1L)).thenReturn(Optional.of(almacenEjemplo()));

        mockMvc.perform(get("/api/v1/almacenes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Almacen Central"))
                .andExpect(jsonPath("$.ubicacion").value("Calle Mayor 10"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(almacenService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/almacenes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(almacenService.crear(any())).thenReturn(almacenEjemplo());

        mockMvc.perform(post("/api/v1/almacenes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Almacen Central"));
    }

    @Test
    @WithMockUser
    void crear_sinNombre_retorna400() throws Exception {
        AlmacenDTO dto = new AlmacenDTO();

        mockMvc.perform(post("/api/v1/almacenes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Almacen actualizado = new Almacen(1L, "Almacen Norte", "Av. Norte 5", "ACTIVO");
        when(almacenService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/almacenes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Almacen Norte"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(almacenService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/almacenes/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(almacenService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/almacenes/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(almacenService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/almacenes/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
