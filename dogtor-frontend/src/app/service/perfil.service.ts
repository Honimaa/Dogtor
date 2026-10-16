import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Perfil {
  usuario: string;
  tipo: 'Cliente' | 'Veterinario' | 'Admin';
  id: number;
  nombre: string;
  cedula?: string;
  correo?: string;
  celular?: string;
  especialidad?: string;
  horario?: string;
  sede?: string;
  foto: string;
}

export interface CambiosPerfil {
  nombre?: string;
  correo?: string;
  celular?: string;
  especialidad?: string;
  horario?: string;
  foto?: string;
  contrasenaActual?: string;
  contrasenaNueva?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PerfilService {
  private baseUrl = 'http://localhost:8082/perfil';
  constructor(private http: HttpClient) { }

  obtener(): Observable<Perfil> {
    return this.http.get<Perfil>(this.baseUrl);
  }

  actualizar(cambios: CambiosPerfil): Observable<Perfil> {
    return this.http.put<Perfil>(this.baseUrl, cambios);
  }
}
