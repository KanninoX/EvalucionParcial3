package com.gestion.productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.productos.Assemblers.ProductoModelAssembler;
import com.gestion.productos.Controller.ProductoController;
import com.gestion.productos.DTO.ProductoDTO;
import com.gestion.productos.Model.Producto;
import com.gestion.productos.Security.JwtFilter;
import com.gestion.productos.Security.JwtUtil;
import com.gestion.productos.Service.ProductoService;
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

@WebMvcTest(ProductoController.class)
@Import({ProductoModelAssembler.class, JwtFilter.class})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private JwtUtil jwtUtil;

    private Producto productoEjemplo() {
        return new Producto(1L, "Laptop Pro", "LAP-001", "ACTIVO");
    }

    private ProductoDTO dtoEjemplo() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Laptop Pro");
        dto.setCodigo("LAP-001");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(productoService.obtenerTodos()).thenReturn(List.of(productoEjemplo()));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.productoList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(productoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(Optional.of(productoEjemplo()));

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop Pro"))
                .andExpect(jsonPath("$.codigo").value("LAP-001"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(productoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(productoService.crear(any())).thenReturn(productoEjemplo());

        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Laptop Pro"));
    }

    @Test
    @WithMockUser
    void crear_sinNombre_retorna400() throws Exception {
        ProductoDTO dto = new ProductoDTO();

        mockMvc.perform(post("/api/v1/productos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Producto actualizado = new Producto(1L, "Laptop Pro X", "LAP-002", "ACTIVO");
        when(productoService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/productos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laptop Pro X"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(productoService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/productos/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(productoService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/productos/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(productoService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/productos/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
