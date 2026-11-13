import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from 'src/app/service/auth.service';
import { AdopcionService } from 'src/app/service/adopcion.service';
import { NotificacionService } from 'src/app/service/notificacion.service';
import { mensajeError } from 'src/app/model/dogtor';
import { comprimirImagen } from 'src/app/utils/imagen';

/**
 * Registro público de refugios y fundaciones.
 * La cuenta queda activa de inmediato, pero el refugio solo publica cuando Dogtor lo valida.
 */
@Component({
  selector: 'app-registro-refugio',
  templateUrl: './registro-refugio.component.html',
  styleUrls: ['../login.component.css', '../registro/registro.component.css']
})
export class RegistroRefugioComponent {
  datos = {
    nombre: '', ciudad: '', telefono: '', correo: '', descripcion: '', logo: '',
    instagram: '', facebook: '', tiktok: '', sitioWeb: '',
    nombreUsuario: '', contrasena: ''
  };
  confirmacion = '';
  aceptaDatos = false;
  enviando = false;
  error = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private adopcionService: AdopcionService,
    private notificacion: NotificacionService,
    private router: Router
  ) {}

  async elegirLogo(event: Event): Promise<void> {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (archivo) {
      try {
        this.datos.logo = await comprimirImagen(archivo, 400);
      } catch {
        this.error = 'No se pudo leer la imagen.';
      }
    }
  }

  registrar(): void {
    this.error = '';
    const d = this.datos;
    if (!d.nombre.trim() || !d.ciudad.trim() || !d.telefono.trim() || !d.correo.trim()
        || !d.nombreUsuario.trim() || !d.contrasena) {
      this.error = 'Completa todos los campos obligatorios.';
      return;
    }
    if (d.contrasena !== this.confirmacion) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }
    if (!this.aceptaDatos) {
      this.error = 'Debes aceptar el tratamiento de los datos personales.';
      return;
    }

    this.enviando = true;
    this.http.post('http://localhost:8082/auth/registro-refugio', d).pipe(
      // Inicia sesión automáticamente con la cuenta recién creada
      switchMap(() => this.authService.iniciarSesion(d.nombreUsuario.trim(), d.contrasena)),
      switchMap((sesion: any) => {
        localStorage.setItem('token', sesion.token);
        localStorage.setItem('userRole', sesion.roles[0]);
        return this.adopcionService.miRefugio();
      })
    ).subscribe({
      next: (refugio) => {
        this.authService.login({
          id: refugio.idRefugio, tipo: 'Refugio', nombre: refugio.nombre,
          foto: refugio.logo || 'assets/avatar-placeholder.svg'
        });
        this.notificacion.exito(`¡Bienvenidos a Dogtor, ${refugio.nombre}! Revisaremos sus datos para habilitar las publicaciones.`);
        this.router.navigate(['/refugio/panel']);
      },
      error: (err) => {
        this.enviando = false;
        this.error = mensajeError(err, 'No se pudo registrar el refugio. Inténtalo de nuevo.');
      }
    });
  }
}
