package com.Operaciones.Notificaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Operaciones.Notificaciones.Assemblers.NotificacionModelAssembler;
import com.Operaciones.Notificaciones.Controller.NotificacionController;
import com.Operaciones.Notificaciones.DTO.NotificacionDTO;
import com.Operaciones.Notificaciones.Model.Notificacion;
import com.Operaciones.Notificaciones.Security.JwtFilter;
import com.Operaciones.Notificaciones.Security.JwtUtil;
import com.Operaciones.Notificaciones.Service.NotificacionService;
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

@WebMvcTest(NotificacionController.class)
@Import({NotificacionModelAssembler.class, JwtFilter.class})
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificacionService notificacionService;

    @MockBean
    private JwtUtil jwtUtil;

    private Notificacion notificacionEjemplo() {
        return new Notificacion(1L, "Bienvenida", "Mensaje de bienvenida", "usuario@test.com", "PENDIENTE", LocalDateTime.now());
    }

    private NotificacionDTO dtoEjemplo() {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setTitulo("Bienvenida");
        dto.setMensaje("Mensaje de bienvenida");
        dto.setDestinatario("usuario@test.com");
        dto.setEstado("PENDIENTE");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(notificacionService.obtenerTodos()).thenReturn(List.of(notificacionEjemplo()));

        mockMvc.perform(get("/api/v1/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.notificacionList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(notificacionService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notificaciones"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(notificacionService.obtenerPorId(1L)).thenReturn(Optional.of(notificacionEjemplo()));

        mockMvc.perform(get("/api/v1/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Bienvenida"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(notificacionService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/notificaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(notificacionService.crear(any())).thenReturn(notificacionEjemplo());

        mockMvc.perform(post("/api/v1/notificaciones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Bienvenida"));
    }

    @Test
    @WithMockUser
    void crear_sinTitulo_retorna400() throws Exception {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setMensaje("Mensaje");
        dto.setDestinatario("usuario@test.com");

        mockMvc.perform(post("/api/v1/notificaciones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Notificacion actualizada = new Notificacion(1L, "Actualización", "Nuevo mensaje", "otro@test.com", "ENVIADO", LocalDateTime.now());
        when(notificacionService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizada));

        mockMvc.perform(put("/api/v1/notificaciones/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Actualización"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(notificacionService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/notificaciones/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(notificacionService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/notificaciones/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(notificacionService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/notificaciones/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
