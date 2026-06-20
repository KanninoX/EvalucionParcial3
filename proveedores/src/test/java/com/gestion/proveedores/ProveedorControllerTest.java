package com.gestion.proveedores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.proveedores.Assemblers.ProveedorModelAssembler;
import com.gestion.proveedores.Controller.ProveedorController;
import com.gestion.proveedores.DTO.ProveedorDTO;
import com.gestion.proveedores.Model.Proveedor;
import com.gestion.proveedores.Security.JwtFilter;
import com.gestion.proveedores.Security.JwtUtil;
import com.gestion.proveedores.Service.ProveedorService;
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

@WebMvcTest(ProveedorController.class)
@Import({ProveedorModelAssembler.class, JwtFilter.class})
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProveedorService proveedorService;

    @MockBean
    private JwtUtil jwtUtil;

    private Proveedor proveedorEjemplo() {
        return new Proveedor(1L, "Proveedor ABC", "B12345678", "ACTIVO");
    }

    private ProveedorDTO dtoEjemplo() {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setNombre("Proveedor ABC");
        dto.setNif("B12345678");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(proveedorService.obtenerTodos()).thenReturn(List.of(proveedorEjemplo()));

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.proveedorList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(proveedorService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(proveedorService.obtenerPorId(1L)).thenReturn(Optional.of(proveedorEjemplo()));

        mockMvc.perform(get("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor ABC"))
                .andExpect(jsonPath("$.nif").value("B12345678"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(proveedorService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/proveedores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(proveedorService.crear(any())).thenReturn(proveedorEjemplo());

        mockMvc.perform(post("/api/v1/proveedores")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Proveedor ABC"));
    }

    @Test
    @WithMockUser
    void crear_sinNombre_retorna400() throws Exception {
        ProveedorDTO dto = new ProveedorDTO();

        mockMvc.perform(post("/api/v1/proveedores")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Proveedor actualizado = new Proveedor(1L, "Proveedor XYZ", "C99999999", "ACTIVO");
        when(proveedorService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/proveedores/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor XYZ"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(proveedorService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/proveedores/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(proveedorService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/proveedores/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(proveedorService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/proveedores/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
