import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClienteCL } from '../model/cliente-cl';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private user: any = null;
  private apiUrl = 'http://localhost:8082/auth'; // Cambia esto si tu API tiene otro puerto o URL base

  constructor(private http: HttpClient) {}

  login(user: any) {
    this.user = user;
    localStorage.setItem('user', JSON.stringify(user));
  }

  logout() {
    this.user = null;
    localStorage.removeItem('token'); // borra el JWT
    localStorage.removeItem('username'); // si guardas otros datos como el nombre de usuario
    localStorage.removeItem('roles');
    localStorage.removeItem('user');
    localStorage.removeItem("userRole"); 
  }

  getUser() {
    if (!this.user) {
      const stored = localStorage.getItem('user');
      this.user = stored ? JSON.parse(stored) : null;
    }
    return this.user;
  }

  getUserId(): number | null {
    return this.getUser()?.id || null;
  }

  getUserType(): string | null {
    return this.getUser()?.tipo || null;
  }

  getUserName(): string {
    return this.getUser()?.nombre || '';
  }

  /** Actualiza el nombre y la foto guardados en la sesión (p. ej. tras editar el perfil). */
  actualizarDatosSesion(nombre: string, foto: string): void {
    const user = this.getUser();
    if (!user) {
      return;
    }
    this.login({ ...user, nombre, foto });
  }

  getUserPhoto(): string {
    return this.getUser()?.foto || 'assets/avatar-placeholder.svg';
  }

  isLoggedIn(): boolean {
    return !!this.getUser();
  }

  /** Nombre de usuario de la sesión, leído del token JWT (campo "sub"). */
  getUsername(): string | null {
    const token = localStorage.getItem('token');
    if (!token) {
      return null;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.sub || null;
    } catch {
      return null;
    }
  }

  /** Nuevo método para buscar el id del cliente por nombre de usuario */
  obtenerIdClientePorNombreUsuario(nombreUsuario: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/idClientePorNombreUsuario/${nombreUsuario}`);
  }


/** Nuevo método para iniciar sesión */
iniciarSesion(username: string, password: string): Observable<any> {
    const credentials = { username, password };
    return this.http.post<any>(`${this.apiUrl}/inicio_sesion`, credentials);
  }

}
