package com.veterinaria.demo.repository;

import com.veterinaria.demo.entidad.*;
import com.veterinaria.demo.repositorio.*;
import com.veterinaria.demo.servicio.CargaMedicamentosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class TratamientoRepositoryTest {

        @MockBean
        private CargaMedicamentosService cargaMedicamentosService;

        @Autowired
        private TratamientoRepository tratamientoRepository;

        @Autowired
        private MascotaRepository mascotaRepository;

        @Autowired
        private VeterinarioRepository veterinarioRepository;

        @Autowired
        private ServicioRepository servicioRepository;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private MedicamentoRepository medicamentoRepository;

        @Autowired
        private TratamientoMedicamentoRepository tratamientoMedicamentoRepository;

        @BeforeEach
        public void setUp() {
                Cliente cliente1 = clienteRepository.save(new Cliente(
                                null, "Juan Pérez", "juan@example.com", "3216549870",
                                "foto1.jpg", "123456789", "juanp", "1234",
                                null, null, null,null));

                Cliente cliente2 = clienteRepository.save(new Cliente(
                                null, "María Gómez", "maria@example.com", "3101234567",
                                "foto2.jpg", "987654321", "mariag", "5678",
                                null, null, null,null));

                Mascota mascota1 = mascotaRepository.save(new Mascota(
                                null, "Firulais", "Labrador", 5, 20.0f,
                                "Ninguna", "mascota1.jpg", new Date(), new Date(),
                                null, 1, cliente1,null));

                Mascota mascota2 = mascotaRepository.save(new Mascota(
                                null, "Michi", "Siamés", 3, 5.0f,
                                "Alergia a pescado", "mascota2.jpg", new Date(), new Date(),
                                null, 1, cliente1, null));

                Mascota mascota3 = mascotaRepository.save(new Mascota(
                                null, "Rex", "Pastor Alemán", 2, 25.0f,
                                "Ninguna", "mascota3.jpg", new Date(), new Date(),
                                null, 1, cliente2, null));

                Veterinario veterinario1 = veterinarioRepository.save(new Veterinario(
                                null, "Dra. Laura", "987654321", "General",
                                "fotoVet1.jpg", "Sede A", 1, 0,
                                "laura.vet", "pass", null, null, null));

                Veterinario veterinario2 = veterinarioRepository.save(new Veterinario(
                                null, "Dr. Carlos", "987654322", "Cirujano",
                                "fotoVet2.jpg", "Sede B", 1, 0,
                                "carlos.vet", "pass", null, null, null));

                Servicio servicio1 = servicioRepository.save(new Servicio(
                                null, "Consulta General", "Chequeo médico completo", 60.0f,
                                "front1.jpg", "back1.jpg", Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList()));

                Servicio servicio2 = servicioRepository.save(new Servicio(
                                null, "Cirugía", "Intervención quirúrgica", 300.0f,
                                "front2.jpg", "back2.jpg", Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList()));

                Servicio servicio3 = servicioRepository.save(new Servicio(
                                null, "Vacunación", "Aplicación de vacunas", 45.0f,
                                "front3.jpg", "back3.jpg", Collections.emptyList(),
                                Collections.emptyList(), Collections.emptyList()));

                tratamientoRepository.save(new Tratamiento(
                                null, "TRAT-001", new Date(),
                                "Desparasitación", veterinario1, mascota1, servicio1,
                                Collections.emptyList()));

                tratamientoRepository.save(new Tratamiento(
                                null, "TRAT-002", new Date(System.currentTimeMillis() - 86400000),
                                "Vacunación", veterinario1, mascota1, servicio3,
                                Collections.emptyList()));

                tratamientoRepository.save(new Tratamiento(
                                null, "TRAT-003", new Date(),
                                "Consulta", veterinario1, mascota2, servicio1,
                                Collections.emptyList()));

                tratamientoRepository.save(new Tratamiento(
                                null, "TRAT-004", new Date(System.currentTimeMillis() - 2592000000L),
                                "Cirugía", veterinario2, mascota3, servicio2,
                                Collections.emptyList()));

                tratamientoRepository.save(new Tratamiento(
                                null, "TRAT-005", new Date(),
                                "Limpieza", veterinario2, mascota1, servicio1,
                                Collections.emptyList()));
        }

        // CRUD
        @Test
        public void TratamientoRepository_save_Tratamiento() {
                // Arrange
                Tratamiento tratamiento = new Tratamiento(
                                null,
                                "TRAT-006",
                                new Date(),
                                "Nuevo tratamiento",
                                veterinarioRepository.findAll().get(0),
                                mascotaRepository.findAll().get(0),
                                servicioRepository.findAll().get(0),
                                Collections.emptyList());

                // Act
                Tratamiento guardado = tratamientoRepository.save(tratamiento);

                // Assert
                assertThat(guardado.getIdTratamiento()).isNotNull();
                assertThat(guardado.getCodigo()).isEqualTo("TRAT-006");
                assertThat(guardado.getMascota().getNombre()).isEqualTo("Firulais");
                assertThat(guardado.getVeterinario().getNombre()).isEqualTo("Dra. Laura");
                assertThat(guardado.getServicio().getNombre()).isEqualTo("Consulta General");
        }

        @Test
        public void TratamientoRepository_findAll_NotEmptyList() {
                // Arrange
                Tratamiento tratamiento = new Tratamiento(
                                null,
                                "TRAT-006",
                                new Date(),
                                "Nuevo tratamiento",
                                veterinarioRepository.findAll().get(0),
                                mascotaRepository.findAll().get(0),
                                servicioRepository.findAll().get(0),
                                Collections.emptyList());

                // Act
                tratamientoRepository.save(tratamiento);
                List<Tratamiento> tratamientos = tratamientoRepository.findAll();

                // Assert
                assertThat(tratamientos)
                                .extracting(Tratamiento::getCodigo)
                                .containsExactlyInAnyOrder("TRAT-001", "TRAT-002", "TRAT-003", "TRAT-004", "TRAT-005",
                                                "TRAT-006");
                assertThat(tratamientos).isNotEmpty();
                assertThat(tratamientos.size()).isEqualTo(6);
        }

        @Test
        public void TratamientoRepository_findById_FindWrongIndex() {
                // Arrange
                int index = -1;

                // Act
                Optional<Tratamiento> tratamiento = tratamientoRepository.findById(index);

                // Assert
                assertThat(tratamiento).isEmpty();
        }

        @Test
        public void TratamientoRepository_deleteById_EmptyTratamiento() {
                // Arrange
                int index = 2;

                // Act
                tratamientoRepository.deleteById(index);

                // Assert
                assertThat(tratamientoRepository.findById(index)).isEmpty();
        }

        @Test
        public void TratamientoRepository_updateByCodigo_Tratamiento() {
                // Arrange
                Tratamiento newTratamiento = tratamientoRepository.save(new Tratamiento(
                                null,
                                "TRAT-006",
                                new Date(),
                                "Original Description",
                                veterinarioRepository.findAll().get(0),
                                mascotaRepository.findAll().get(0),
                                servicioRepository.findAll().get(0),
                                Collections.emptyList()));

                int id = newTratamiento.getIdTratamiento();
                String newCodigo = "TRAT-007";

                // Act
                Optional<Tratamiento> tratamientoOpt = tratamientoRepository.findById(id);
                Tratamiento tratamiento = tratamientoOpt.get();
                tratamiento.setCodigo(newCodigo);
                Tratamiento updated = tratamientoRepository.save(tratamiento);

                // Assert
                assertThat(updated).isNotNull();
                assertThat(updated.getCodigo()).isEqualTo(newCodigo);
        }

        // Queries
        @Test
        public void TratamientoRepository_findByServicioNombreIn_NotEmptyListTratamientos() {
                // Arrange
                String servicio = "Consulta General";

                // Act
                List<Tratamiento> tratamientos = tratamientoRepository
                                .findByServicioNombreIn(Collections.singletonList(servicio));

                // Assert
                assertThat(tratamientos).isNotEmpty();
                assertThat(tratamientos.size()).isEqualTo(3);
        }

        @Test
        public void TratamientoRepository_findMascotasByVeterinarioId_NotEmptyListMascotas() {
                // Arrange
                int index = 0;

                // Act
                List<Mascota> mascotas = tratamientoRepository
                                .findMascotasByVeterinarioId(
                                                veterinarioRepository.findAll().get(index).getIdVeterinario());

                // Assert
                assertThat(mascotas).isNotEmpty();
                assertThat(mascotas.size()).isEqualTo(2);
        }

        @Test
        public void TratamientoRepository_findByMascota_NotEmptyListTratamientos() {
                // Arrange
                int index = 0;

                // Act
                List<Tratamiento> tratamientos = tratamientoRepository
                                .findByMascota(mascotaRepository.findAll().get(index));

                // Assert
                assertThat(tratamientos).isNotEmpty();
                assertThat(tratamientos.size()).isEqualTo(3);
        }

        @Test
        public void TratamientoRepository_countByFechaBetween_Count() {
                // Arrange
                Date fechaInicio = new Date(System.currentTimeMillis() - 86400000);
                Date fechaFin = new Date(System.currentTimeMillis() + 86400000);

                // Act
                long count = tratamientoRepository.countByFechaBetween(fechaInicio, fechaFin);

                // Assert
                assertThat(count).isEqualTo(4);
        }

        @Test
        public void TratamientoRepository_findByFechaBetween_NotEmptyListTratamientos() {
                // Arrange
                Date fechaInicio = new Date(System.currentTimeMillis() - 86400000);
                Date fechaFin = new Date(System.currentTimeMillis() + 86400000);

                // Act
                List<Tratamiento> tratamientos = tratamientoRepository.findByFechaBetween(fechaInicio, fechaFin);

                // Assert
                assertThat(tratamientos).isNotEmpty();
                assertThat(tratamientos.size()).isEqualTo(4);
        }

        @Test
        public void TratamientoRepository_findMedicamentosMasUsados_NotEmptyListMedicamentos() {
                // Arrange
                Date fechaInicio = new Date(System.currentTimeMillis() - 86400000);
                Date fechaFin = new Date(System.currentTimeMillis() + 86400000);

                // Act
                List<Object[]> resultados = tratamientoRepository.findMedicamentosMasUsados(fechaInicio, fechaFin);

                // Assert
                assertThat(resultados).isNotNull();
        }

        @Test
        public void TratamientoRepository_findTop3MedicamentosMasVendidos_NotEmptyListMedicamentos() {
                // Arrange
                Medicamento medicamento = new Medicamento();
                medicamento.setNombre("Paracetamol");
                medicamento.setPrecioCompra(10.5f);
                medicamento.setPrecioVenta(15.0f);
                medicamento.setUnidadesDisponibles(100);
                medicamento.setUnidadesVendidas(0);
                medicamento.setTratamientoMedicamentos(Collections.emptyList());

                medicamento = medicamentoRepository.save(medicamento);

                Tratamiento tratamiento = new Tratamiento();
                tratamiento.setCodigo("TRAT-006");
                tratamiento.setFecha(new Date());
                tratamiento.setDetalles("Tratamiento con medicamento");
                tratamiento.setVeterinario(veterinarioRepository.findAll().get(0));
                tratamiento.setMascota(mascotaRepository.findAll().get(0));
                tratamiento.setServicio(servicioRepository.findAll().get(0));
                tratamiento.setTratamientoMedicamentos(Collections.emptyList());

                tratamiento = tratamientoRepository.save(tratamiento);

                TratamientoMedicamento tm = new TratamientoMedicamento();
                tm.setTratamiento(tratamiento);
                tm.setMedicamento(medicamento);
                tm.setCantidad(100);

                tratamientoMedicamentoRepository.save(tm);

                // Act
                List<Map<String, Object>> resultados = tratamientoRepository.findTop3MedicamentosMasVendidos();

                // Assert
                assertThat(resultados).isNotNull();
                assertThat(resultados).hasSize(1);
                assertThat(resultados.get(0).get("medicamento")).isEqualTo("Paracetamol");
                assertThat(resultados.get(0).get("total")).isEqualTo(100L);
        }

        @Test
        public void TratamientoRepository_findByVeterinarioId_ShouldReturnTratamientos() {
                // Arrange
                int index = 0;

                // Act
                List<Tratamiento> tratamientos = tratamientoRepository
                                .findByVeterinarioId(veterinarioRepository.findAll().get(index).getIdVeterinario());

                // Assert
                assertThat(tratamientos).isNotEmpty();
                assertThat(tratamientos.size()).isEqualTo(3);
        }
}