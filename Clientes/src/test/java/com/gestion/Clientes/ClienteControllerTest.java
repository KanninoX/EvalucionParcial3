package com.gestion.Clientes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.Clientes.Assemblers.ClienteModelAssembler;
import com.gestion.Clientes.Controller.ClienteController;
import com.gestion.Clientes.DTO.ClienteDTO;
import com.gestion.Clientes.Model.Cliente;
import com.gestion.Clientes.Security.JwtFilter;
import com.gestion.Clientes.Security.JwtUtil;
import com.gestion.Clientes.Service.ClienteService;
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

@WebMvcTest(ClienteController.class)
@Import({ClienteModelAssembler.class, JwtFilter.class})
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private JwtUtil jwtUtil;

    private Cliente clienteEjemplo() {
        return new Cliente(1L, "Juan", "Pérez", "juan@example.com", "123456789", "Calle 1", "ACTIVO");
    }

    private ClienteDTO dtoEjemplo() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setEmail("juan@example.com");
        dto.setTelefono("123456789");
        dto.setDireccion("Calle 1");
        return dto;
    }

    @Test
    @WithMockUser
    void obtenerTodos_retornaLista() throws Exception {
        when(clienteService.obtenerTodos()).thenReturn(List.of(clienteEjemplo()));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.clienteList").isArray());
    }

    @Test
    @WithMockUser
    void obtenerTodos_listaVacia() throws Exception {
        when(clienteService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void obtenerPorId_existente_retorna200() throws Exception {
        when(clienteService.obtenerPorId(1L)).thenReturn(Optional.of(clienteEjemplo()));

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    @WithMockUser
    void obtenerPorId_inexistente_retorna404() throws Exception {
        when(clienteService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/clientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void crear_retorna201() throws Exception {
        when(clienteService.crear(any())).thenReturn(clienteEjemplo());

        mockMvc.perform(post("/api/v1/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @WithMockUser
    void crear_sinNombre_retorna400() throws Exception {
        ClienteDTO dto = new ClienteDTO();
        dto.setEmail("juan@example.com");
        dto.setApellido("Pérez");

        mockMvc.perform(post("/api/v1/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void actualizar_existente_retorna200() throws Exception {
        Cliente actualizado = new Cliente(1L, "Juan Carlos", "Pérez", "juan@example.com", "123456789", "Calle 2", "ACTIVO");
        when(clienteService.actualizar(anyLong(), any())).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/clientes/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"));
    }

    @Test
    @WithMockUser
    void actualizar_inexistente_retorna404() throws Exception {
        when(clienteService.actualizar(anyLong(), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/clientes/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEjemplo())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void eliminar_existente_retorna204() throws Exception {
        when(clienteService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/clientes/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void eliminar_inexistente_retorna404() throws Exception {
        when(clienteService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/clientes/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
