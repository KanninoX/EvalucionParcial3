package com.Operaciones.Pagos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Operaciones.Pagos.Assemblers.PagoModelAssembler;
import com.Operaciones.Pagos.Controller.PagoController;
import com.Operaciones.Pagos.DTO.PagoDTO;
import com.Operaciones.Pagos.Model.Pago;
import com.Operaciones.Pagos.Security.JwtFilter;
import com.Operaciones.Pagos.Security.JwtUtil;
import com.Operaciones.Pagos.Service.PagoService;
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

@WebMvcTest(PagoController.class)
@Import({PagoModelAssembler.class, JwtFilter.class})
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagoService pagoService;

    @MockBean
    private JwtUtil jwtUtil;

    private Pago pagoEjemplo() {
        return new Pago(1L, 1L, 500.0, "TARJETA", "PENDIENTE", LocalDateTime.now());
    }

    private PagoDTO dtoEjemplo() {
        PagoDTO dto = new PagoDTO();
        dto.setVentaId(1L);
        dto.setMonto(500.0);
        dto.setMetodoPago("TARJETA");
        dto.setEstado("PENDIENTE");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(pagoService.obtenerTodos()).thenReturn(List.of(pagoEjemplo()));

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pagoList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(pagoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(pagoService.obtenerPorId(1L)).thenReturn(Optional.of(pagoEjemplo()));

        mockMvc.perform(get("/api/v1/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(pagoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pagos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(pagoService.crear(any())).thenReturn(pagoEjemplo());

        mockMvc.perform(post("/api/v1/pagos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"));
    }

    @Test
    @WithMockUser
    void crear_sinMetodoPago_retorna400() throws Exception {
        PagoDTO dto = new PagoDTO();
        dto.setVentaId(1L);
        dto.setMonto(500.0);

        mockMvc.perform(post("/api/v1/pagos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Pago actualizado = new Pago(1L, 1L, 750.0, "EFECTIVO", "PAGADO", LocalDateTime.now());
        when(pagoService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/pagos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodoPago").value("EFECTIVO"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(pagoService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/pagos/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(pagoService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/pagos/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(pagoService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/pagos/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
