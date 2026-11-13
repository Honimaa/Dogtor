import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MascotaAdopcion, Refugio, SolicitudAdopcion } from '../model/dogtor';

/** Quién gestiona: el administrador (todo) o un refugio (solo lo suyo). */
export type ModoGestion = 'admin' | 'refugio';

export interface FiltrosAdopcion {
  especie?: string;
  tamano?: string;
  ciudad?: string;
  edad?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdopcionService {
  private baseUrl = 'http://localhost:8082/adopcion';
  constructor(private http: HttpClient) { }

  // Público
  catalogo(filtros: FiltrosAdopcion = {}): Observable<MascotaAdopcion[]> {
    let params = new HttpParams();
    Object.entries(filtros).forEach(([clave, valor]) => {
      if (valor) {
        params = params.set(clave, valor);
      }
    });
    return this.http.get<MascotaAdopcion[]>(this.baseUrl, { params });
  }

  detalle(id: number): Observable<MascotaAdopcion> {
    return this.http.get<MascotaAdopcion>(`${this.baseUrl}/${id}`);
  }

  enviarSolicitud(idMascota: number, solicitud: SolicitudAdopcion): Observable<SolicitudAdopcion> {
    return this.http.post<SolicitudAdopcion>(`${this.baseUrl}/${idMascota}/solicitudes`, solicitud);
  }

  refugiosValidados(): Observable<Refugio[]> {
    return this.http.get<Refugio[]>(`${this.baseUrl}/refugios`);
  }

  // Gestión (admin o refugio). El backend decide qué ve cada uno según la sesión.
  todasLasMascotas(modo: ModoGestion = 'admin'): Observable<MascotaAdopcion[]> {
    return this.http.get<MascotaAdopcion[]>(`${this.baseUrl}/${modo}/mascotas`);
  }

  /** El admin indica el refugio; un refugio siempre publica a su propio nombre. */
  publicarMascota(mascota: MascotaAdopcion, modo: ModoGestion = 'admin', idRefugio?: number): Observable<MascotaAdopcion> {
    const opciones = modo === 'admin' && idRefugio != null ? { params: { idRefugio } } : {};
    return this.http.post<MascotaAdopcion>(`${this.baseUrl}/${modo}/mascotas`, mascota, opciones);
  }

  actualizarMascota(id: number, mascota: MascotaAdopcion, modo: ModoGestion = 'admin'): Observable<MascotaAdopcion> {
    return this.http.put<MascotaAdopcion>(`${this.baseUrl}/${modo}/mascotas/${id}`, mascota);
  }

  solicitudes(modo: ModoGestion = 'admin'): Observable<SolicitudAdopcion[]> {
    return this.http.get<SolicitudAdopcion[]>(`${this.baseUrl}/${modo}/solicitudes`);
  }

  cambiarEstadoSolicitud(id: number, estado: string, modo: ModoGestion = 'admin'): Observable<SolicitudAdopcion> {
    return this.http.put<SolicitudAdopcion>(`${this.baseUrl}/${modo}/solicitudes/${id}`, null, {
      params: { estado }
    });
  }

  // Panel del refugio
  miRefugio(): Observable<Refugio> {
    return this.http.get<Refugio>(`${this.baseUrl}/refugio/perfil`);
  }

  actualizarMiRefugio(refugio: Refugio): Observable<Refugio> {
    return this.http.put<Refugio>(`${this.baseUrl}/refugio/perfil`, refugio);
  }

  cambiarMiContrasena(contrasenaActual: string, contrasenaNueva: string): Observable<unknown> {
    return this.http.put(`${this.baseUrl}/refugio/contrasena`, { contrasenaActual, contrasenaNueva });
  }

  // Solo administración

  todosLosRefugios(): Observable<Refugio[]> {
    return this.http.get<Refugio[]>(`${this.baseUrl}/admin/refugios`);
  }

  registrarRefugio(refugio: Refugio): Observable<Refugio> {
    return this.http.post<Refugio>(`${this.baseUrl}/admin/refugios`, refugio);
  }

  /** Crea la cuenta del refugio o, si ya tiene, le asigna una contraseña nueva. */
  asignarCuentaRefugio(id: number, nombreUsuario: string, contrasena: string): Observable<Refugio> {
    return this.http.put<Refugio>(`${this.baseUrl}/admin/refugios/${id}/cuenta`, { nombreUsuario, contrasena });
  }

  validarRefugio(id: number, validado: boolean): Observable<Refugio> {
    return this.http.put<Refugio>(`${this.baseUrl}/admin/refugios/${id}/validacion`, null, {
      params: { validado }
    });
  }
}
