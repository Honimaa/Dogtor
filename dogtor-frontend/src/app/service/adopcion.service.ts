import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MascotaAdopcion, Refugio, SolicitudAdopcion } from '../model/dogtor';

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

  // Administración
  todasLasMascotas(): Observable<MascotaAdopcion[]> {
    return this.http.get<MascotaAdopcion[]>(`${this.baseUrl}/admin/mascotas`);
  }

  publicarMascota(idRefugio: number, mascota: MascotaAdopcion): Observable<MascotaAdopcion> {
    return this.http.post<MascotaAdopcion>(`${this.baseUrl}/admin/mascotas`, mascota, {
      params: { idRefugio }
    });
  }

  actualizarMascota(id: number, mascota: MascotaAdopcion): Observable<MascotaAdopcion> {
    return this.http.put<MascotaAdopcion>(`${this.baseUrl}/admin/mascotas/${id}`, mascota);
  }

  solicitudes(): Observable<SolicitudAdopcion[]> {
    return this.http.get<SolicitudAdopcion[]>(`${this.baseUrl}/admin/solicitudes`);
  }

  cambiarEstadoSolicitud(id: number, estado: string): Observable<SolicitudAdopcion> {
    return this.http.put<SolicitudAdopcion>(`${this.baseUrl}/admin/solicitudes/${id}`, null, {
      params: { estado }
    });
  }

  todosLosRefugios(): Observable<Refugio[]> {
    return this.http.get<Refugio[]>(`${this.baseUrl}/admin/refugios`);
  }

  registrarRefugio(refugio: Refugio): Observable<Refugio> {
    return this.http.post<Refugio>(`${this.baseUrl}/admin/refugios`, refugio);
  }

  validarRefugio(id: number, validado: boolean): Observable<Refugio> {
    return this.http.put<Refugio>(`${this.baseUrl}/admin/refugios/${id}/validacion`, null, {
      params: { validado }
    });
  }
}
