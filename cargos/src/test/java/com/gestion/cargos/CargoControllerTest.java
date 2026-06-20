package com.gestion.cargos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.cargos.Assemblers.CargoModelAssembler;
import com.gestion.cargos.Controller.CargoController;
import com.gestion.cargos.DTO.CargoDTO;
import com.gestion.cargos.Model.Cargo;
import com.gestion.cargos.Security.JwtFilter;
import com.gestion.cargos.Security.JwtUtil;
import com.gestion.cargos.Service.CargoService;
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

@WebMvcTest(CargoController.class)
@Import({CargoModelAssembler.class, JwtFilter.class})
class CargoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CargoService service;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Cargo cargo;

    @BeforeEach
    void setUp() {
        cargo = new Cargo(1, "Gerente de RRHH", "Gestión del personal", "ACTIVO", 1);
    }

    // ===== GET /cargos (público) =====

    @Test
    void listar_sinAutenticacion_retornaOk() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of(cargo));

        mockMvc.perform(get("/api/v1/cargos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cargoList[0].nombre").value("Gerente de RRHH"))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void listar_variousCargos_retornaColeccion() throws Exception {
        Cargo c2 = new Cargo(2, "Analista", "Análisis de datos", "ACTIVO", 1);
        when(service.obtenerTodos()).thenReturn(List.of(cargo, c2));

        mockMvc.perform(get("/api/v1/cargos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cargoList.length()").value(2));
    }

    // ===== GET /cargos/{id} (público) =====

    @Test
    void obtener_cargoExistente_retorna200ConHateoas() throws Exception {
        when(service.obtenerPorId(1)).thenReturn(Optional.of(cargo));

        mockMvc.perform(get("/api/v1/cargos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Gerente de RRHH"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.cargos").exists());
    }

    @Test
    void obtener_cargoInexistente_retorna404() throws Exception {
        when(service.obtenerPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cargos/99"))
                .andExpect(status().isNotFound());
    }

    // ===== POST /cargos/crear =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_departamentoValido_retorna200() throws Exception {
        CargoDTO dto = new CargoDTO();
        dto.setNombre("Analista Senior");
        dto.setDescripcion("Análisis avanzado");
        dto.setDepartamentoId(1);

        when(service.crearCargo(any(CargoDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/cargos/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cargo creado correctamente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_departamentoInexistente_retorna404() throws Exception {
        CargoDTO dto = new CargoDTO();
        dto.setNombre("Cargo Huérfano");
        dto.setDepartamentoId(999);

        when(service.crearCargo(any(CargoDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/v1/cargos/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== PUT /cargos/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_cargoExistente_retorna200() throws Exception {
        CargoDTO dto = new CargoDTO();
        dto.setNombre("Gerente Actualizado");
        dto.setDepartamentoId(1);

        when(service.actualizar(eq(1), any(CargoDTO.class)))
                .thenReturn(Optional.of(new Cargo(1, "Gerente Actualizado", "Desc", "ACTIVO", 1)));

        mockMvc.perform(put("/api/v1/cargos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Gerente Actualizado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_cargoInexistente_retorna404() throws Exception {
        CargoDTO dto = new CargoDTO();
        dto.setNombre("Ghost");

        when(service.actualizar(eq(99), any(CargoDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/cargos/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ===== DELETE /cargos/{id} =====

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_cargoExistente_retorna204() throws Exception {
        when(service.eliminar(1)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/cargos/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_cargoInexistente_retorna404() throws Exception {
        when(service.eliminar(99)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/cargos/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
