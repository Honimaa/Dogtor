import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from 'src/app/service/auth.service';
import { ClienteService } from 'src/app/service/cliente.service';
import { NotificacionService } from 'src/app/service/notificacion.service';
import { mensajeError } from 'src/app/model/dogtor';
import { comprimirImagen } from 'src/app/utils/imagen';

/** Registro público de propietarios de mascotas. */
@Component({
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrls: ['../login.component.css', './registro.component.css']
})
export class RegistroComponent {
  datos = { nombre: '', cedula: '', correo: '', celular: '', nombreUsuario: '', contrasena: '', foto: '' };
  confirmacion = '';
  aceptaDatos = false;
  enviando = false;
  error = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private clienteService: ClienteService,
    private notificacion: NotificacionService,
    private router: Router
  ) {}

  async elegirFoto(event: Event): Promise<void> {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (archivo) {
      try {
        this.datos.foto = await comprimirImagen(archivo, 500);
      } catch {
        this.error = 'No se pudo leer la imagen.';
      }
    }
  }

  registrar(): void {
    this.error = '';
    const d = this.datos;
    if (!d.nombre.trim() || !d.cedula.trim() || !d.correo.trim() || !d.celular.trim() || !d.nombreUsuario.trim() || !d.contrasena) {
      this.error = 'Completa todos los campos.';
      return;
    }
    if (d.contrasena !== this.confirmacion) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }
    if (!this.aceptaDatos) {
      this.error = 'Debes aceptar el tratamiento de tus datos personales.';
      return;
    }

    this.enviando = true;
    this.http.post('http://localhost:8082/auth/registro', d).pipe(
      // Inicia sesión automáticamente con la cuenta recién creada
      switchMap(() => this.authService.iniciarSesion(d.nombreUsuario.trim(), d.contrasena)),
      switchMap((sesion: any) => {
        localStorage.setItem('token', sesion.token);
        localStorage.setItem('userRole', sesion.roles[0]);
        return this.clienteService.clienteHome();
      })
    ).subscribe({
      next: (cliente) => {
        this.authService.login({ id: cliente.idCliente, tipo: 'Cliente', nombre: cliente.nombre, foto: cliente.foto });
        this.notificacion.exito(`¡Bienvenido a Dogtor, ${cliente.nombre}! Ya puedes registrar a tus mascotas.`);
        this.router.navigate(['/mascotas-cliente', cliente.idCliente]);
      },
      error: (err) => {
        this.enviando = false;
        this.error = mensajeError(err, 'No se pudo crear la cuenta. Inténtalo de nuevo.');
      }
    });
  }
}
