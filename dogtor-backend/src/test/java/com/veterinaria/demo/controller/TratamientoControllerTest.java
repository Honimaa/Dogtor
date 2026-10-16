package com.veterinaria.demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.demo.controlador.TratamientoController;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.seguridad.JWTAuthenticationFilter;
import com.veterinaria.demo.seguridad.JWTGenerator;
import com.veterinaria.demo.servicio.CargaMedicamentosService;
import com.veterinaria.demo.servicio.MascotaService;
import com.veterinaria.demo.servicio.TratamientoService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TratamientoController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@RunWith(SpringRunner.class)
public class TratamientoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TratamientoService tratamientoService;

    @MockBean
    private MascotaService mascotaService;

    @MockBean
    private CargaMedicamentosService cargaMedicamentosService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JWTGenerator jwtGenerator;


    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void TratamientoController_mostrarTratamientos_ListaTratamientos() throws Exception {
        // Arrange
        List<Tratamiento> tratamientos = Arrays.asList(
            new Tratamiento(1, "TRAT-001", new Date(), "Consulta", null, null, null, null),
            new Tratamiento(2, "TRAT-002", new Date(), "Cirugía", null, null, null, null)
        );
        
        when(tratamientoService.obtenerTodosTratamientos()).thenReturn(tratamientos);

        // Act & Assert
        mockMvc.perform(get("/tratamiento"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].codigo").value("TRAT-001"))
            .andExpect(jsonPath("$[1].codigo").value("TRAT-002"));
    }

    @Test
    public void TratamientoController_detalleTratamiento_TratamientoExistente() throws Exception {
        // Arrange
        Tratamiento tratamiento = new Tratamiento(1, "TRAT-001", new Date(), "Consulta", null, null, null, null);
        when(tratamientoService.obtenerTratamientoPorId(1)).thenReturn(tratamiento);

        // Act & Assert
        mockMvc.perform(get("/tratamiento/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.codigo").value("TRAT-001"));
    }

    @Test
    public void TratamientoController_detalleTratamiento_TratamientoNoExistente() throws Exception {
        // Arrange
        when(tratamientoService.obtenerTratamientoPorId(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/tratamiento/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void TratamientoController_crearTratamiento_TratamientoCreado() throws Exception {
        // Arrange
        Tratamiento tratamiento = new Tratamiento(null, "TRAT-001", new Date(), "Consulta", null, null, null, null);
        Tratamiento tratamientoCreado = new Tratamiento(1, "TRAT-001", new Date(), "Consulta", null, null, null, null);
        
        when(tratamientoService.crearTratamiento(
            any(Tratamiento.class), 
            anyInt(), 
            anyInt(), 
            anyInt(), 
            anyList()))
            .thenReturn(tratamientoCreado);

        // Act & Assert
        mockMvc.perform(post("/tratamiento/crear")
                .param("idMascota", "1")
                .param("idServicio", "1")
                .param("idVeterinario", "1")
                .param("idsMedicamentos", "1,2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tratamiento)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.idTratamiento").value(1))
            .andExpect(jsonPath("$.codigo").value("TRAT-001"));
    }

    @Test
    public void TratamientoController_editarTratamiento_TratamientoActualizado() throws Exception {
        // Arrange
        Tratamiento tratamientoActualizado = new Tratamiento(1, "TRAT-001-MOD", new Date(), "Consulta modificada", null, null, null, null);
        
        when(tratamientoService.editarTratamiento(
            eq(1), 
            any(Tratamiento.class), 
            anyInt(), 
            anyInt(), 
            anyInt(), 
            anyList()))
            .thenReturn(tratamientoActualizado);

        // Act & Assert
        mockMvc.perform(put("/tratamiento/editar/1")
                .param("idMascota", "1")
                .param("idServicio", "1")
                .param("idVeterinario", "1")
                .param("idsMedicamentos", "1,2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tratamientoActualizado)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.codigo").value("TRAT-001-MOD"));
    }

    @Test
    public void TratamientoController_eliminarTratamiento_TratamientoEliminado() throws Exception {
        // Arrange
        doNothing().when(tratamientoService).eliminarTratamientoPorId(1);

        // Act & Assert
        mockMvc.perform(delete("/tratamiento/eliminar/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Tratamiento eliminado correctamente"));
    }

    @Test
    public void TratamientoController_obtenerTratamientosPorMascota_ListaTratamientos() throws Exception {
        // Arrange
        Mascota mascota = new Mascota(1, "Firulais", "Labrador", 5, 20.0f, null, null, null, null, null, 1, null,null);
        List<Tratamiento> tratamientos = Arrays.asList(
            new Tratamiento(1, "TRAT-001", new Date(), "Consulta", null, mascota, null, null)
        );
        
        when(mascotaService.obtenerMascotaPorId(1)).thenReturn(mascota);
        when(tratamientoService.obtenerTratamientosPorMascota(1)).thenReturn(tratamientos);

        // Act & Assert
        mockMvc.perform(get("/tratamiento/por-mascota/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].codigo").value("TRAT-001"));
    }

    @Test
    public void TratamientoController_contarTratamientosUltimos30Dias_ConteoCorrecto() throws Exception {
        // Arrange
        when(tratamientoService.contarTratamientosUltimos30Dias()).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/tratamiento/cantidad-ultimos-30-dias"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").value(5));
    }

    @Test
    public void TratamientoController_obtenerMedicamentosMasUsados_MapaMedicamentos() throws Exception {
        // Arrange
        Map<String, Integer> medicamentos = new HashMap<>();
        medicamentos.put("Paracetamol", 10);
        medicamentos.put("Ibuprofeno", 5);
        
        when(tratamientoService.obtenerMedicamentosMasUsadosUltimos30Dias()).thenReturn(medicamentos);

        // Act & Assert
        mockMvc.perform(get("/tratamiento/medicamentos-mas-usados"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.Paracetamol").value(10))
            .andExpect(jsonPath("$.Ibuprofeno").value(5));
    }
}