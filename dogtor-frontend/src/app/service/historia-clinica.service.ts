import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventoClinico } from '../model/dogtor';

@Injectable({
  providedIn: 'root'
})
export class HistoriaClinicaService {
  private baseUrl = 'http://localhost:8082/historia-clinica';
  constructor(private http: HttpClient) { }

  obtenerHistorial(idMascota: number, tipo?: string): Observable<EventoClinico[]> {
    let params = new HttpParams();
    if (tipo) {
      params = params.set('tipo', tipo);
    }
    return this.http.get<EventoClinico[]>(`${this.baseUrl}/mascota/${idMascota}`, { params });
  }

  obtenerAlergias(idMascota: number): Observable<EventoClinico[]> {
    return this.http.get<EventoClinico[]>(`${this.baseUrl}/mascota/${idMascota}/alergias`);
  }

  registrarEvento(idMascota: number, evento: EventoClinico, idVeterinario?: number | null): Observable<EventoClinico> {
    let params = new HttpParams();
    if (idVeterinario) {
      params = params.set('idVeterinario', idVeterinario);
    }
    return this.http.post<EventoClinico>(`${this.baseUrl}/mascota/${idMascota}`, evento, { params });
  }

  eliminarEvento(idEvento: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${idEvento}`);
  }
}
