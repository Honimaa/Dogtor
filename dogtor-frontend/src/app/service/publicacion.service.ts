import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Publicacion } from '../model/publicacion';

@Injectable({
  providedIn: 'root'
})
export class PublicacionService {
  private baseUrl = 'http://localhost:8082/publicaciones';
  constructor(private http: HttpClient) { }

  listar(tipo?: 'PERDIDA' | 'ENCONTRADA'): Observable<Publicacion[]> {
    const params = tipo ? new HttpParams().set('tipo', tipo) : undefined;
    return this.http.get<Publicacion[]>(this.baseUrl, { params });
  }

  publicar(publicacion: Publicacion): Observable<Publicacion> {
    return this.http.post<Publicacion>(this.baseUrl, publicacion);
  }

  marcarResuelta(id: number): Observable<Publicacion> {
    return this.http.put<Publicacion>(`${this.baseUrl}/${id}/resuelta`, null);
  }

  reabrir(id: number): Observable<Publicacion> {
    return this.http.put<Publicacion>(`${this.baseUrl}/${id}/reabrir`, null);
  }
}
