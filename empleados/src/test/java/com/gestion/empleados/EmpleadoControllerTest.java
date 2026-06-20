package com.gestion.empleados;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.empleados.Assemblers.EmpleadoModelAssembler;
import com.gestion.empleados.Controller.EmpleadoController;
import com.gestion.empleados.DTO.EmpleadoDTO;
import com.gestion.empleados.Model.Empleado;
import com.gestion.empleados.Security.JwtFilter;
import com.gestion.empleados.Security.JwtUtil;
import com.gestion.empleados.Service.EmpleadoService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmpleadoController.class)
@Import({EmpleadoModelAssembler.class, JwtFilter.class})
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Empleado empleado;

    @BeforeEach
    void setUp() {
        empleado = new Empleado(1, "Juan", "Pérez", "juan@test.com", "912345678", "ACTIVO", 1, 1);
    }

    // ===== GET /empleados =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_retornaColeccionConHateoas() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(empleado));

        mockMvc.perform(get("/api/v1/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.empleadoList[0].nombre").value("Juan"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listar_listaVacia_retornaColeccionVacia() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    // ===== GET /empleados/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtener_empleadoExistente_retorna200ConHateoas() throws Exception {
        when(service.obtenerPorId(1)).thenReturn(Optional.of(empleado));

        mockMvc.perform(get("/api/v1/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.empleados").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void obtener_empleadoInexistente_retorna404() throws Exception {
        when(service.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/empleados/99"))
                .andExpect(status().isNotFound());
    }

    // ===== POST /empleados/crear =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_datosValidos_retorna200() throws Exception {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setEmail("juan@test.com");
        dto.setDepartamentoId(1);
        dto.setCargoId(1);

        when(service.crearEmpleado(any(EmpleadoDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/empleados/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Empleado creado correctamente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_departamentoOCargoInexistente_retorna404() throws Exception {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Ghost");
        dto.setApellido("User");
        dto.setDepartamentoId(999);
        dto.setCargoId(999);

        when(service.crearEmpleado(any(EmpleadoDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/empleados/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== PUT /empleados/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_empleadoExistente_retorna200() throws Exception {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Juan Updated");
        dto.setApellido("Pérez");
        dto.setEmail("juan@test.com");
        dto.setDepartamentoId(1);
        dto.setCargoId(1);

        when(service.actualizar(eq(1), any(EmpleadoDTO.class))).thenReturn(Optional.of(empleado));

        mockMvc.perform(put("/api/v1/empleados/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_empleadoInexistente_retorna404() throws Exception {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Ghost");

        when(service.actualizar(eq(99), any(EmpleadoDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/empleados/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== DELETE /empleados/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_empleadoExistente_retorna204() throws Exception {
        when(service.eliminar(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/empleados/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_empleadoInexistente_retorna404() throws Exception {
        when(service.eliminar(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/empleados/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
