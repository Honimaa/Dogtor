package com.veterinaria.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.TratamientoRepository;
import com.veterinaria.demo.servicio.TratamientoServiceImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TratamientoServiceMockTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    private Tratamiento tratamientoReciente;
    private Tratamiento tratamientoAntiguo;
    private Veterinario veterinario;

    @BeforeEach
    public void setUp() {
        veterinario = new Veterinario();
        veterinario.setIdVeterinario(1);
        veterinario.setNombre("Dra. Laura");

        // Tratamiento reciente (hoy)
        tratamientoReciente = new Tratamiento();
        tratamientoReciente.setIdTratamiento(1);
        tratamientoReciente.setCodigo("TRAT-001");
        tratamientoReciente.setFecha(new Date());
        tratamientoReciente.setVeterinario(veterinario);

        // Tratamiento antiguo (hace 60 días)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -60);
        tratamientoAntiguo = new Tratamiento();
        tratamientoAntiguo.setIdTratamiento(2);
        tratamientoAntiguo.setCodigo("TRAT-002");
        tratamientoAntiguo.setFecha(calendar.getTime());
        tratamientoAntiguo.setVeterinario(veterinario);
    }

    @Test
    public void TratamientoService_obtenerTodosTratamientos_NotEmptyListTratamientos() {
        // Arrange
        when(tratamientoRepository.findAll())
            .thenReturn(Arrays.asList(tratamientoReciente, tratamientoAntiguo));

        // Act
        List<Tratamiento> tratamientos = tratamientoService.obtenerTodosTratamientos();
        
        // Assert
        assertThat(tratamientos).hasSize(2);
        assertThat(tratamientos)
            .extracting(Tratamiento::getCodigo)
            .containsExactly("TRAT-001", "TRAT-002");
    }

    @Test
    public void TratamientoService_obtenerTratamientoPorId_Tratamiento() {
        // Arrange
        when(tratamientoRepository.findById(1))
            .thenReturn(Optional.of(tratamientoReciente));

        // Act
        Tratamiento encontrado = tratamientoService.obtenerTratamientoPorId(1);
        
        // Assert
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getCodigo()).isEqualTo("TRAT-001");
    }

    @Test
    public void TratamientoService_contarTratamientosUltimos30Dias_Count() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        Date fechaFin = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaInicio = new Date(cal.getTime().getTime());
        
        when(tratamientoRepository.countByFechaBetween(
            argThat(date -> Math.abs(date.getTime() - fechaInicio.getTime()) <= 1000),
            argThat(date -> Math.abs(date.getTime() - fechaFin.getTime()) <= 1000)
        )).thenReturn(1L);

        // Act
        long count = tratamientoService.contarTratamientosUltimos30Dias();
        
        // Assert
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void TratamientoService_obtenerTratamientosUltimos30Dias_NotEmptyListTratamientos() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        Date fechaFin = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaInicio = new Date(cal.getTime().getTime());
        
        when(tratamientoRepository.findByFechaBetween(
            argThat(date -> Math.abs(date.getTime() - fechaInicio.getTime()) <= 1000),
            argThat(date -> Math.abs(date.getTime() - fechaFin.getTime()) <= 1000)
        )).thenReturn(Collections.singletonList(tratamientoReciente));

        // Act
        List<Tratamiento> tratamientos = tratamientoService.obtenerTratamientosUltimos30Dias();
        
        // Assert
        assertThat(tratamientos).hasSize(1);
        assertThat(tratamientos.get(0).getCodigo()).isEqualTo("TRAT-001");
    }

    @Test
    public void TratamientoService_obtenerMedicamentosMasUsadosUltimos30Dias_NotEmptyListMedicamentos() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        Date fechaFin = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date fechaInicio = new Date(cal.getTime().getTime());
        
        when(tratamientoRepository.findMedicamentosMasUsados(
            argThat(date -> Math.abs(date.getTime() - fechaInicio.getTime()) <= 1000),
            argThat(date -> Math.abs(date.getTime() - fechaFin.getTime()) <= 1000)
        )).thenReturn(Collections.emptyList());

        // Act
        Map<String, Integer> resultados = tratamientoService.obtenerMedicamentosMasUsadosUltimos30Dias();
        
        // Assert
        assertThat(resultados).isNotNull();
        assertThat(resultados).isEmpty();
    }

    @Test
    public void TratamientoService_obtenerTop3MedicamentosMasVendidos_NotEmptyListMedicamentos() {
        // Arrange
        when(tratamientoRepository.findTop3MedicamentosMasVendidos())
            .thenReturn(Collections.emptyList());

        // Act
        List<Map<String, Object>> resultados = tratamientoService.obtenerTop3MedicamentosMasVendidos();
        
        // Assert
        assertThat(resultados).isNotNull();
        assertThat(resultados).isEmpty();
    }

    @Test
    public void TratamientoService_obtenerTratamientosPorVeterinario_NotEmptyListTratamientos() {
        // Arrange
        when(tratamientoRepository.findByVeterinarioId(1))
            .thenReturn(Arrays.asList(tratamientoReciente, tratamientoAntiguo));

        // Act
        List<Tratamiento> tratamientos = tratamientoService.obtenerTratamientosPorVeterinario(1);
        
        // Assert
        assertThat(tratamientos).hasSize(2);
        assertThat(tratamientos)
            .extracting(t -> t.getVeterinario().getNombre())
            .containsOnly("Dra. Laura");
    }
}