package com.Operaciones.ventas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Operaciones.ventas.Assemblers.VentaModelAssembler;
import com.Operaciones.ventas.Controller.VentaController;
import com.Operaciones.ventas.DTO.VentaDTO;
import com.Operaciones.ventas.Model.Venta;
import com.Operaciones.ventas.Security.JwtFilter;
import com.Operaciones.ventas.Security.JwtUtil;
import com.Operaciones.ventas.Service.VentaService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
@Import({VentaModelAssembler.class, JwtFilter.class})
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private JwtUtil jwtUtil;

    private Venta ventaEjemplo() {
        return new Venta(1L, 1L, 2L, "Laptop", 2, 1500.0, 3000.0, "ACTIVO", LocalDateTime.now());
    }

    private VentaDTO dtoEjemplo() {
        VentaDTO dto = new VentaDTO();
        dto.setClienteId(1L);
        dto.setEmpleadoId(2L);
        dto.setProducto("Laptop");
        dto.setCantidad(2);
        dto.setPrecioUnitario(1500.0);
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(ventaService.obtenerTodos()).thenReturn(List.of(ventaEjemplo()));

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ventaList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(ventaService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(ventaService.obtenerPorId(1L)).thenReturn(Optional.of(ventaEjemplo()));

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto").value("Laptop"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(ventaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/ventas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(ventaService.crear(any())).thenReturn(ventaEjemplo());

        mockMvc.perform(post("/api/v1/ventas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.producto").value("Laptop"));
    }

    @Test
    @WithMockUser
    void crear_sinProducto_retorna400() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setClienteId(1L);
        dto.setCantidad(2);
        dto.setPrecioUnitario(1500.0);

        mockMvc.perform(post("/api/v1/ventas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Venta actualizada = new Venta(1L, 1L, 2L, "Laptop Pro", 3, 2000.0, 6000.0, "ACTIVO", LocalDateTime.now());
        when(ventaService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizada));

        mockMvc.perform(put("/api/v1/ventas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto").value("Laptop Pro"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(ventaService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/ventas/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(ventaService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/ventas/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(ventaService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/ventas/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
