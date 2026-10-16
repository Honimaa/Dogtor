package com.veterinaria.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.veterinaria.demo.entidad.Cliente;
import com.veterinaria.demo.entidad.Mascota;
import com.veterinaria.demo.entidad.Medicamento;
import com.veterinaria.demo.entidad.Servicio;
import com.veterinaria.demo.entidad.Tratamiento;
import com.veterinaria.demo.entidad.Veterinario;
import com.veterinaria.demo.repositorio.ClienteRepository;
import com.veterinaria.demo.repositorio.MascotaRepository;
import com.veterinaria.demo.repositorio.MedicamentoRepository;
import com.veterinaria.demo.repositorio.ServicioRepository;
import com.veterinaria.demo.repositorio.TratamientoRepository;
import com.veterinaria.demo.repositorio.VeterinarioRepository;
import com.veterinaria.demo.servicio.TratamientoService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class TratamientoServiceTestNaive {

    @Autowired
    private TratamientoService tratamientoService;
    
    @Autowired
    private TratamientoRepository tratamientoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private VeterinarioRepository veterinarioRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;
    
    @BeforeEach
    public void setUp() {
        clienteRepository.save(new Cliente(
            null, "Juan Pérez", "juan@example.com", "3216549870",
            "foto.jpg", "1234567891", "juanp2", "1234",
            null, null, null,null));
            
        mascotaRepository.save(new Mascota(
            null, "Firulais", "Labrador", 5, 20.0f,
            "Ninguna", "mascota.jpg", new Date(), new Date(),
            null, 1, clienteRepository.findAll().get(0),null));
        
        veterinarioRepository.save(new Veterinario(
            null, "Dra. Laura", "987654321", "General",
            "fotoVet.jpg", "Sede A", 1, 0,
            "laura.vet8", "pass8", null, null, null));
        
        servicioRepository.save(new Servicio(
            null, "Consulta General", "Chequeo médico completo", 60.0f,
            "front.jpg", "back.jpg", null, null, null));
        
        // Tratamiento reciente (hoy)
        tratamientoRepository.save(new Tratamiento(
            null, "TRAT-001", new Date(),
            "Consulta reciente", 
            veterinarioRepository.findAll().get(0), 
            mascotaRepository.findAll().get(0), 
            servicioRepository.findAll().get(0), 
            null));
        
        // Tratamiento antiguo (hace 60 días)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -60);
        tratamientoRepository.save(new Tratamiento(
            null, "TRAT-002", calendar.getTime(),
            "Consulta antigua", 
            veterinarioRepository.findAll().get(0), 
            mascotaRepository.findAll().get(0), 
            servicioRepository.findAll().get(0), 
            null));
    }

    @Test
    public void TratamientoService_crearTratamiento_Tratamiento() {
        // Arrange
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setCodigo("TRT001");
        tratamiento.setFecha(new Date());
        tratamiento.setDetalles("Consulta de rutina");

        Integer idMascota = mascotaRepository.findAll().get(0).getIdMascota();
        Integer idServicio = servicioRepository.findAll().get(0).getIdServicio();
        Integer idVeterinario = veterinarioRepository.findAll().get(0).getIdVeterinario();
        
        Medicamento medicamento1 = new Medicamento();
        medicamento1.setNombre("Paracetamol");
        medicamento1.setPrecioCompra(10.0f);
        medicamento1.setPrecioVenta(15.0f);
        medicamento1.setUnidadesDisponibles(10);
        medicamento1.setUnidadesVendidas(0);
        medicamento1 = medicamentoRepository.save(medicamento1);

        Medicamento medicamento2 = new Medicamento();
        medicamento2.setNombre("Ibuprofeno");
        medicamento2.setPrecioCompra(12.0f);
        medicamento2.setPrecioVenta(18.0f);
        medicamento2.setUnidadesDisponibles(8);
        medicamento2.setUnidadesVendidas(0);
        medicamento2 = medicamentoRepository.save(medicamento2);

        List<Integer> idsMedicamentos = Arrays.asList(medicamento1.getIdMedicamento(), medicamento2.getIdMedicamento());

        // Act
        Tratamiento newTratamiento = tratamientoService.crearTratamiento(
            tratamiento, 
            idMascota, 
            idServicio, 
            idVeterinario, 
            idsMedicamentos
        );

        // Assert
        assertThat(newTratamiento).isNotNull();
        assertThat(newTratamiento.getIdTratamiento()).isNotNull();
        assertThat(newTratamiento.getCodigo()).isEqualTo("TRT001");        
        assertThat(newTratamiento.getMascota()).isNotNull();
        assertThat(newTratamiento.getMascota().getIdMascota()).isEqualTo(idMascota);        
        assertThat(newTratamiento.getServicio()).isNotNull();
        assertThat(newTratamiento.getServicio().getIdServicio()).isEqualTo(idServicio);        
        assertThat(newTratamiento.getVeterinario()).isNotNull();
        assertThat(newTratamiento.getVeterinario().getIdVeterinario()).isEqualTo(idVeterinario);        
        assertThat(newTratamiento.getTratamientoMedicamentos()).hasSize(2);        
        Medicamento updatedMed1 = medicamentoRepository.findById(medicamento1.getIdMedicamento()).orElse(null);
        Medicamento updatedMed2 = medicamentoRepository.findById(medicamento2.getIdMedicamento()).orElse(null);        
        assertThat(updatedMed1.getUnidadesDisponibles()).isEqualTo(9);
        assertThat(updatedMed1.getUnidadesVendidas()).isEqualTo(1);        
        assertThat(updatedMed2.getUnidadesDisponibles()).isEqualTo(7);
        assertThat(updatedMed2.getUnidadesVendidas()).isEqualTo(1);
    }

    @Test
    public void TratamientoService_obtenerTodosTratamientos_NotEmptyListTratamientos() {
        // Act
        List<Tratamiento> tratamientos = tratamientoService.obtenerTodosTratamientos();
        
        // Assert
        assertThat(tratamientos).isNotEmpty();
        assertThat(tratamientos.size()).isEqualTo(12);
        assertThat(tratamientos)
            .extracting(Tratamiento::getCodigo)
            .containsExactlyInAnyOrder("TRAT001", "TRAT002", "TRAT003", "TRAT004", "TRAT005", "TRAT006", "TRAT007", "TRAT008", "TRAT009", "TRAT010", "TRAT-001", "TRAT-002");
    }

    @Test
    public void TratamientoService_obtenerTratamientoPorId_Tratamiento() {
        // Arrange
        Integer id = tratamientoRepository.findByCodigo("TRAT-001").get(0).getIdTratamiento();
        
        // Act
        Tratamiento encontrado = tratamientoService.obtenerTratamientoPorId(id);
        
        // Assert
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getCodigo()).isEqualTo("TRAT-001");
    }

    @Test
    public void TratamientoService_contarTratamientosUltimos30Dias_Count() {
        // Act
        long count = tratamientoService.contarTratamientosUltimos30Dias();
        
        // Assert
        assertThat(count).isEqualTo(6);
    }

    @Test
    public void TratamientoService_obtenerMedicamentosMasUsadosUltimos30Dias_NotEmptyListMedicamentos() {
        // Act
        Map<String, Integer> resultados = tratamientoService.obtenerMedicamentosMasUsadosUltimos30Dias();
        
        // Assert
        assertThat(resultados).isNotNull();
    }

    @Test
    public void TratamientoService_obtenerTop3MedicamentosMasVendidos_NotEmptyListMedicamentos() {
        // Act
        List<Map<String, Object>> resultados = tratamientoService.obtenerTop3MedicamentosMasVendidos();
        
        // Assert
        assertThat(resultados).isNotNull();
        assertThat(resultados.size()).isLessThanOrEqualTo(3);
    }
}