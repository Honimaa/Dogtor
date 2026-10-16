package com.veterinaria.demo.entidad;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.veterinaria.demo.repositorio.*;

import jakarta.transaction.Transactional;

/**
 * Datos de ejemplo de los módulos de Dogtor: urgencias 24h, inventario, historia clínica,
 * recordatorios y adopción. Se ejecuta después de DataBaseInit.
 */
@Component
@Transactional
@Profile("default")
@Order(2)
public class DogtorDataInit implements ApplicationRunner {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private EventoClinicoRepository eventoClinicoRepository;

    @Autowired
    private RefugioRepository refugioRepository;

    @Autowired
    private MascotaAdopcionRepository mascotaAdopcionRepository;

    @Autowired
    private SolicitudAdopcionRepository solicitudAdopcionRepository;

    @Autowired
    private PublicacionMascotaRepository publicacionRepository;

    @Override
    public void run(ApplicationArguments args) {
        inicializarDoctores();
        inicializarMascotas();
        inicializarInventario();
        inicializarCitas();
        inicializarHistoriasClinicas();
        inicializarAdopcion();
        inicializarPublicaciones();
        System.out.println("Dogtor: módulos de historia clínica, inventario, urgencias y adopción inicializados.");
    }

    private void inicializarDoctores() {
        List<Veterinario> veterinarios = veterinarioRepository.findAll();
        for (int i = 0; i < veterinarios.size(); i++) {
            Veterinario v = veterinarios.get(i);
            v.setTarjetaProfesional("TP-" + (20450 + i * 37));
            // Un doctor por sede cubre el turno de urgencias 24 horas
            boolean urgencias = i == 0 || i == 3 || i == 6;
            v.setUrgencias24h(urgencias);
            v.setHorario(urgencias ? "Turno de urgencias 24/7" : (i % 2 == 0 ? "Lun a Vie · 8:00 a 17:00" : "Mar a Sáb · 9:00 a 18:00"));
        }
        veterinarioRepository.saveAll(veterinarios);
    }

    // Fechas coherentes: nacimiento según la edad, ingreso como paciente en el pasado y,
    // si la mascota está inactiva (dada de baja), su fecha de salida.
    private void inicializarMascotas() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        for (Mascota m : mascotas) {
            int id = m.getIdMascota();
            int edad = m.getEdad() != null ? m.getEdad() : 1;
            m.setFechaNacimiento(dias(-(edad * 365L) - (id * 37L) % 300));
            long diasIngreso = Math.min(edad * 365L - 30, 60 + (id * 53L) % 700);
            m.setFechaIngreso(dias(-Math.max(diasIngreso, 20)));
            // Las mascotas de carlosg (cliente de prueba) quedan activas
            if (id <= 3) {
                m.setEstado(1);
            }
            if (m.getEstado() != null && m.getEstado() == 0) {
                m.setFechaSalida(dias(-((id * 11L) % 15 + 1)));
            } else {
                m.setFechaSalida(null);
            }
        }
        mascotaRepository.saveAll(mascotas);
    }

    private void inicializarInventario() {
        List<Medicamento> medicamentos = medicamentoRepository.findAll();
        String[] categorias = {"MEDICAMENTO", "MEDICAMENTO", "MEDICAMENTO", "INSUMO_MEDICO", "ALIMENTO", "ACCESORIO"};
        for (int i = 0; i < medicamentos.size(); i++) {
            Medicamento m = medicamentos.get(i);
            // Productos de alta rotación con mínimo 10; el resto del catálogo con mínimo 1
            m.setStockMinimo(m.getUnidadesDisponibles() >= 20 ? 10 : 1);
            m.setLote("L" + (2400 + i));
            m.setCategoria(categorias[i % categorias.length]);
            // Escenarios de alerta: vencidos, próximos a vencer y bajo stock
            int dias = switch (i % 41) {
                case 0 -> -12;
                case 1 -> 18;
                case 2 -> 45;
                default -> 120 + (i * 13) % 400;
            };
            m.setFechaVencimiento(dias(dias));
            if (i % 7 == 3) {
                m.setUnidadesDisponibles(Math.min(m.getUnidadesDisponibles(), 4));
            }
        }
        medicamentoRepository.saveAll(medicamentos);
    }

    private void inicializarCitas() {
        String[][] motivos = {
                {"CONTROL", "Control de otitis y revisión de oídos"},
                {"CONTROL", "Seguimiento de problemas respiratorios"},
                {"VACUNACION", "Refuerzo anual de vacunas"}
        };
        List<Cita> citas = citaRepository.findAll();
        for (int i = 0; i < citas.size(); i++) {
            String[] motivo = motivos[i % motivos.length];
            citas.get(i).setTipoConsulta(motivo[0]);
            citas.get(i).setMotivo(motivo[1]);
        }
        citaRepository.saveAll(citas);
    }

    private void inicializarHistoriasClinicas() {
        Veterinario vet1 = veterinarioRepository.findById(1).orElse(null);
        Veterinario vet2 = veterinarioRepository.findById(2).orElse(null);
        Veterinario vet4 = veterinarioRepository.findById(4).orElse(null);

        mascotaRepository.findById(1).ifPresent(m -> eventoClinicoRepository.saveAll(List.of(
                evento(m, vet1, "VACUNA", "Vacuna antirrábica", "Dosis anual aplicada sin reacciones adversas.", -340, 25),
                evento(m, vet2, "VACUNA", "Vacuna múltiple (DHPPi+L)", "Refuerzo anual. Lote V-8821.", -200, 165),
                evento(m, vet1, "DIAGNOSTICO", "Otitis externa", "Inflamación del conducto auditivo derecho. Se toma muestra para citología.", -20, null),
                evento(m, vet1, "TRATAMIENTO", "Tratamiento tópico para otitis", "Limpieza diaria y gotas óticas por 10 días. Control al terminar.", -20, 4),
                evento(m, vet1, "ALERGIA", "Alergia a la penicilina", "Reacción cutánea documentada en 2024. Evitar betalactámicos.", -400, null)
        )));

        mascotaRepository.findById(2).ifPresent(m -> eventoClinicoRepository.saveAll(List.of(
                evento(m, vet2, "VACUNA", "Vacuna antirrábica", "Dosis anual.", -370, -5),
                evento(m, vet2, "DIAGNOSTICO", "Síndrome braquicefálico", "Dificultad respiratoria leve propia de la raza. Se recomienda control de peso.", -60, null),
                evento(m, vet4, "CIRUGIA", "Estafilectomía", "Corrección de paladar blando elongado. Recuperación favorable.", -45, 15)
        )));

        mascotaRepository.findById(3).ifPresent(m -> eventoClinicoRepository.saveAll(List.of(
                evento(m, vet1, "ALERGIA", "Alergia alimentaria al pollo", "Dermatitis y prurito al consumir proteína de pollo.", -150, null),
                evento(m, vet1, "DIAGNOSTICO", "Dermatitis alérgica", "Lesiones en abdomen y patas. Dieta hipoalergénica.", -150, null),
                evento(m, vet2, "VACUNA", "Vacuna contra la tos de las perreras", "Aplicación intranasal.", -170, 12)
        )));

        for (int id = 4; id <= 12; id++) {
            Veterinario vet = id % 2 == 0 ? vet2 : vet4;
            int idMascota = id;
            mascotaRepository.findById(idMascota).ifPresent(m -> eventoClinicoRepository.saveAll(List.of(
                    evento(m, vet, "VACUNA", "Vacuna antirrábica", "Dosis anual.", -300 + idMascota * 9, 65 + idMascota * 9),
                    evento(m, vet, "DIAGNOSTICO", "Chequeo general",
                            m.getEnfermedad() != null ? "Hallazgo principal: " + m.getEnfermedad() + "." : "Sin hallazgos relevantes.",
                            -30 - idMascota, null)
            )));
        }
    }

    private void inicializarAdopcion() {
        Refugio huellitas = refugioRepository.save(Refugio.builder()
                .nombre("Fundación Huellitas de Amor").ciudad("Bogotá")
                .telefono("3104567890").correo("contacto@huellitasdeamor.org")
                .descripcion("Rescatamos perros y gatos en situación de calle en Bogotá y los preparamos para un hogar responsable.")
                .validado(true).build());
        Refugio patas = refugioRepository.save(Refugio.builder()
                .nombre("Refugio Cuatro Patas").ciudad("Medellín")
                .telefono("3159876543").correo("adopciones@cuatropatas.org")
                .descripcion("Hogar de paso para animales rescatados del Valle de Aburrá.")
                .validado(true).build());
        Refugio gatos = refugioRepository.save(Refugio.builder()
                .nombre("Michis Felices").ciudad("Cali")
                .telefono("3201112233").correo("hola@michisfelices.org")
                .descripcion("Fundación especializada en el rescate y esterilización de gatos.")
                .validado(true).build());
        refugioRepository.save(Refugio.builder()
                .nombre("Amigos Peludos Tunja").ciudad("Tunja")
                .telefono("3005554433").correo("amigospeludos@gmail.com")
                .descripcion("Refugio en proceso de validación.")
                .validado(false).build());

        List<MascotaAdopcion> mascotas = mascotaAdopcionRepository.saveAll(List.of(
                adopcion("Canela", "PERRO", "Pastor australiano mestiza", 8, "MEDIANO", "Hembra", huellitas,
                        "Juguetona y muy cariñosa. Se lleva bien con niños y otros perros.",
                        "Vacunada, desparasitada y esterilizada.",
                        "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=600"),
                adopcion("Rocky", "PERRO", "Beagle", 36, "MEDIANO", "Macho", huellitas,
                        "Tranquilo, leal y con un olfato increíble. Ideal para casa con patio.",
                        "Vacunado y esterilizado.",
                        "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600"),
                adopcion("Toby", "PERRO", "Pug", 96, "PEQUENO", "Macho", patas,
                        "Abuelito dulce que busca un hogar tranquilo para sus años dorados.",
                        "Vacunado. Requiere control cardiológico semestral.",
                        "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=600"),
                adopcion("Luna", "GATO", "Criolla", 5, "PEQUENO", "Hembra", gatos,
                        "Curiosa y sociable. Usa arenero sin problema.",
                        "Primera dosis de vacunas aplicada.",
                        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600"),
                adopcion("Simón", "GATO", "Atigrado", 24, "MEDIANO", "Macho", gatos,
                        "Independiente pero muy mimoso cuando entra en confianza.",
                        "Vacunado, esterilizado y con prueba negativa de leucemia felina.",
                        "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=600"),
                adopcion("Kira", "PERRO", "Jack Russell terrier mestiza", 18, "PEQUENO", "Hembra", patas,
                        "Activa e inteligente. Necesita paseos diarios y mucho juego.",
                        "Vacunada y esterilizada.",
                        "https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=600"),
                adopcion("Mía", "GATO", "Criolla atigrada", 4, "PEQUENO", "Hembra", gatos,
                        "Juguetona y muy limpia, ideal para apartamento.",
                        "Primera dosis de vacunas aplicada.",
                        "https://images.unsplash.com/photo-1592194996308-7b43878e84a6?w=600"),
                adopcion("Bruno", "PERRO", "Bulldog francés", 10, "PEQUENO", "Macho", huellitas,
                        "Rescatado de un criadero informal, ya aprendió a pasear con correa.",
                        "Vacunado y desparasitado. Pendiente esterilización.",
                        "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600")
        ));

        MascotaAdopcion rocky = mascotas.get(1);
        rocky.setEstado("EN_PROCESO");
        mascotaAdopcionRepository.save(rocky);

        solicitudAdopcionRepository.saveAll(List.of(
                SolicitudAdopcion.builder().nombreSolicitante("Laura Rincón").correo("laura.rincon@gmail.com")
                        .telefono("3014445566").ciudad("Bogotá").estado("EN_REVISION").fecha(dias(-3))
                        .mensaje("Tenemos casa con patio y experiencia con beagles.")
                        .mascotaAdopcion(rocky).build(),
                SolicitudAdopcion.builder().nombreSolicitante("Andrés Mejía").correo("andres.mejia@hotmail.com")
                        .telefono("3127778899").ciudad("Bogotá").estado("PENDIENTE").fecha(dias(-1))
                        .mensaje("Me encantaría darle un hogar a Canela, vivo con mi familia.")
                        .mascotaAdopcion(mascotas.get(0)).build(),
                SolicitudAdopcion.builder().nombreSolicitante("Paola Herrera").correo("paoh@gmail.com")
                        .telefono("3189990011").ciudad("Cali").estado("PENDIENTE").fecha(new Date())
                        .mensaje("Busco una gatita tranquila para mi apartamento.")
                        .mascotaAdopcion(mascotas.get(6)).build()
        ));
    }

    // Ejemplos de "Dogtor Encuentra"
    private void inicializarPublicaciones() {
        publicacionRepository.saveAll(List.of(
                PublicacionMascota.builder().tipo("PERDIDA").nombreMascota("Lucas").especie("Perro")
                        .descripcion("Beagle macho de 3 años, collar rojo con placa. Responde a su nombre y es muy amigable.")
                        .lugar("Parque El Virrey, cerca a la carrera 15").ciudad("Bogotá").fechaSuceso(dias(-2))
                        .foto("https://images.dog.ceo/breeds/beagle/n02088364_959.jpg")
                        .autorUsuario("carlosg").contactoNombre("Carlos Gómez").contactoTelefono("3104458812")
                        .fechaPublicacion(dias(-2)).resuelta(false).build(),
                PublicacionMascota.builder().tipo("ENCONTRADA").nombreMascota("").especie("Gato")
                        .descripcion("Gata atigrada joven, muy dócil. Tiene collar azul sin placa. La tengo en casa a salvo.")
                        .lugar("Barrio Chapinero Alto, calle 57").ciudad("Bogotá").fechaSuceso(dias(-1))
                        .foto("https://images.unsplash.com/photo-1574158622682-e40e69881006?w=600")
                        .autorUsuario("juanp").contactoNombre("Julián Prieto").contactoTelefono("3157720190")
                        .fechaPublicacion(dias(-1)).resuelta(false).build(),
                PublicacionMascota.builder().tipo("PERDIDA").nombreMascota("Nieve").especie("Perro")
                        .descripcion("Husky hembra de ojos azules, se asustó con pólvora y salió corriendo.")
                        .lugar("Conjunto Los Almendros, Envigado").ciudad("Medellín").fechaSuceso(dias(-6))
                        .foto("https://images.dog.ceo/breeds/husky/n02110185_4294.jpg")
                        .autorUsuario("luciam").contactoNombre("Camila Restrepo").contactoTelefono("3006612094")
                        .fechaPublicacion(dias(-6)).resuelta(true).build()
        ));
    }

    private EventoClinico evento(Mascota mascota, Veterinario vet, String tipo, String titulo, String descripcion,
                                 int diasFecha, Integer diasProxima) {
        return EventoClinico.builder()
                .mascota(mascota)
                .veterinario(vet)
                .tipo(tipo)
                .titulo(titulo)
                .descripcion(descripcion)
                .fecha(dias(diasFecha))
                .fechaProxima(diasProxima != null ? dias(diasProxima) : null)
                .fechaRegistro(dias(diasFecha))
                .build();
    }

    private MascotaAdopcion adopcion(String nombre, String especie, String raza, int edadMeses, String tamano,
                                     String sexo, Refugio refugio, String descripcion, String salud, String foto) {
        return MascotaAdopcion.builder()
                .nombre(nombre).especie(especie).raza(raza).edadMeses(edadMeses).tamano(tamano).sexo(sexo)
                .ciudad(refugio.getCiudad()).descripcion(descripcion).estadoSalud(salud).foto(foto)
                .estado("DISPONIBLE").fechaPublicacion(dias(-edadMeses % 20)).refugio(refugio)
                .build();
    }

    private Date dias(long dias) {
        return new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(dias));
    }
}
