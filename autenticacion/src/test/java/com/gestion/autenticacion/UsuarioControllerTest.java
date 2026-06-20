package com.gestion.autenticacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.autenticacion.Assemblers.UsuarioModelAssembler;
import com.gestion.autenticacion.Controller.UsuarioController;
import com.gestion.autenticacion.DTO.UsuarioDTO;
import com.gestion.autenticacion.Model.Usuario;
import com.gestion.autenticacion.Security.JwtFilter;
import com.gestion.autenticacion.Security.JwtUtil;
import com.gestion.autenticacion.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@Import({UsuarioModelAssembler.class, JwtFilter.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1, "jdoe", "pass123", "ADMIN", "ACTIVO", 1);
    }

    // ===== GET /usuarios =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_retornaListaConHateoas() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/autenticacion/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioList[0].username").value("jdoe"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_retornaListaVacia() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/autenticacion/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    // ===== GET /usuarios/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtener_usuarioExistente_retorna200ConHateoas() throws Exception {
        when(service.obtenerPorId(1)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/autenticacion/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jdoe"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.usuarios").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtener_usuarioInexistente_retorna404() throws Exception {
        when(service.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/autenticacion/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    // ===== POST /registrar =====

    @Test
    void registrar_empleadoValido_retorna200() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("jdoe");
        dto.setPassword("pass123");
        dto.setEmpleadoId(1);

        when(service.registrar(any(UsuarioDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/autenticacion/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario registrado correctamente"));
    }

    @Test
    void registrar_empleadoInexistente_retorna404() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("ghost");
        dto.setPassword("pass");
        dto.setEmpleadoId(999);

        when(service.registrar(any(UsuarioDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/autenticacion/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== POST /login =====

    @Test
    void login_credencialesCorrectas_retornaToken() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("jdoe");
        dto.setPassword("pass123");

        when(service.login("jdoe", "pass123")).thenReturn(Optional.of("mock.jwt.token"));

        mockMvc.perform(post("/api/v1/autenticacion/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void login_credencialesIncorrectas_retorna401() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("jdoe");
        dto.setPassword("wrong");

        when(service.login("jdoe", "wrong")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/autenticacion/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas"));
    }

    // ===== DELETE /usuarios/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_usuarioExistente_retorna204() throws Exception {
        when(service.eliminar(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/autenticacion/usuarios/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_usuarioInexistente_retorna404() throws Exception {
        when(service.eliminar(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/autenticacion/usuarios/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
