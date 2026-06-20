package com.Operaciones.Reportes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Operaciones.Reportes.Assemblers.ReporteModelAssembler;
import com.Operaciones.Reportes.Controller.ReporteController;
import com.Operaciones.Reportes.DTO.ReporteDTO;
import com.Operaciones.Reportes.Model.Reporte;
import com.Operaciones.Reportes.Security.JwtFilter;
import com.Operaciones.Reportes.Security.JwtUtil;
import com.Operaciones.Reportes.Service.ReporteService;
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

@WebMvcTest(ReporteController.class)
@Import({ReporteModelAssembler.class, JwtFilter.class})
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReporteService reporteService;

    @MockBean
    private JwtUtil jwtUtil;

    private Reporte reporteEjemplo() {
        return new Reporte(1L, "VENTAS", "Reporte mensual de ventas", "ACTIVO", LocalDateTime.now());
    }

    private ReporteDTO dtoEjemplo() {
        ReporteDTO dto = new ReporteDTO();
        dto.setTipo("VENTAS");
        dto.setDescripcion("Reporte mensual de ventas");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(reporteService.obtenerTodos()).thenReturn(List.of(reporteEjemplo()));

        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reporteList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(reporteService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reportes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(reporteService.obtenerPorId(1L)).thenReturn(Optional.of(reporteEjemplo()));

        mockMvc.perform(get("/api/v1/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("VENTAS"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(reporteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/reportes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(reporteService.crear(any())).thenReturn(reporteEjemplo());

        mockMvc.perform(post("/api/v1/reportes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("VENTAS"));
    }

    @Test
    @WithMockUser
    void crear_sinTipo_retorna400() throws Exception {
        ReporteDTO dto = new ReporteDTO();
        dto.setDescripcion("Sin tipo");

        mockMvc.perform(post("/api/v1/reportes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Reporte actualizado = new Reporte(1L, "INVENTARIO", "Reporte de inventario", "ACTIVO", LocalDateTime.now());
        when(reporteService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/reportes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("INVENTARIO"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(reporteService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/reportes/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(reporteService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/reportes/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(reporteService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/reportes/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
