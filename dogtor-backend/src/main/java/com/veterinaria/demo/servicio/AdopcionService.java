package com.veterinaria.demo.servicio;

import java.util.List;

import com.veterinaria.demo.entidad.MascotaAdopcion;
import com.veterinaria.demo.entidad.Refugio;
import com.veterinaria.demo.entidad.SolicitudAdopcion;

public interface AdopcionService {
    List<MascotaAdopcion> catalogo(String especie, String tamano, String ciudad, String edad);
    MascotaAdopcion obtenerMascota(Integer id);
    List<MascotaAdopcion> obtenerTodasMascotas();
    MascotaAdopcion publicarMascota(Integer idRefugio, MascotaAdopcion mascota);
    MascotaAdopcion actualizarMascota(Integer id, MascotaAdopcion mascota);

    SolicitudAdopcion enviarSolicitud(Integer idMascota, SolicitudAdopcion solicitud);
    List<SolicitudAdopcion> obtenerSolicitudes();
    List<SolicitudAdopcion> obtenerSolicitudesPorMascota(Integer idMascota);
    SolicitudAdopcion cambiarEstadoSolicitud(Integer idSolicitud, String estado);

    List<Refugio> obtenerRefugiosValidados();
    List<Refugio> obtenerTodosRefugios();
    Refugio registrarRefugio(Refugio refugio);
    Refugio cambiarValidacionRefugio(Integer idRefugio, boolean validado);
    /** Crea la cuenta del refugio o, si ya tiene, le asigna una contraseña nueva. */
    Refugio asignarCuentaRefugio(Integer idRefugio, String nombreUsuario, String contrasena);
    void cambiarContrasenaRefugio(String nombreUsuario, String actual, String nueva);

    // Panel del refugio: cada refugio solo ve y modifica lo suyo
    Refugio refugioDeUsuario(String nombreUsuario);
    Refugio actualizarPerfilRefugio(Integer idRefugio, Refugio datos);
    List<MascotaAdopcion> mascotasDeRefugio(Integer idRefugio);
    List<SolicitudAdopcion> solicitudesDeRefugio(Integer idRefugio);
    MascotaAdopcion actualizarMascotaDeRefugio(Integer idRefugio, Integer idMascota, MascotaAdopcion datos);
    SolicitudAdopcion cambiarEstadoSolicitudDeRefugio(Integer idRefugio, Integer idSolicitud, String estado);
}
