import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ServicioCL } from '../model/servicio-cl';
import { map } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ServicioService {
  private apiUrl = 'http://localhost:8082/api/servicios';

  constructor(private http: HttpClient) { }

  findAll(): Observable<ServicioCL[]> {
    return this.http.get<ServicioCL[]>(this.apiUrl);
  }

  obtenerServicios(): Observable<ServicioCL[]> {
    return this.http.get<ServicioCL[]>(this.apiUrl).pipe(
      map(response => response.map(item => ServicioCL.fromBackendData(item)))
    );
  }

  obtenerServicioPorId(id: number): Observable<ServicioCL> {
    return this.http.get<ServicioCL>(`${this.apiUrl}/${id}`)
  }

  obtenerVentasTotales(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/ventas-totales`);
  }

  obtenerGananciasTotales(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/ganancias-totales`);
  }

  obtenerServiciosTodos(): Observable<ServicioCL[]> {
    return this.http.get<ServicioCL[]>(this.apiUrl);
  }

  crearServicio(datos: Partial<ServicioCL>): Observable<ServicioCL> {
    return this.http.post<ServicioCL>(this.apiUrl, datos);
  }

  actualizarServicio(id: number, datos: Partial<ServicioCL>): Observable<ServicioCL> {
    return this.http.put<ServicioCL>(`${this.apiUrl}/${id}`, datos);
  }

  getServicioPorTratamiento(id:number): Observable<ServicioCL>{
    return this.http.get<ServicioCL>(`${this.apiUrl}/por-tratamiento/${id}`);
  }
}