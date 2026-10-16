import { Component, OnInit } from '@angular/core';
import { CambiosPerfil, Perfil, PerfilService } from 'src/app/service/perfil.service';
import { AuthService } from 'src/app/service/auth.service';
import { NotificacionService } from 'src/app/service/notificacion.service';
import { mensajeError } from 'src/app/model/dogtor';
import { comprimirImagen } from 'src/app/utils/imagen';

@Component({
  selector: 'app-mi-perfil',
  templateUrl: './mi-perfil.component.html',
  styleUrls: ['./mi-perfil.component.css']
})
export class MiPerfilComponent implements OnInit {
  perfil: Perfil | null = null;
  form: CambiosPerfil = {};
  fotoNueva: string | null = null;
  cambiarContrasena = false;
  confirmacion = '';
  guardando = false;
  cargando = true;

  constructor(
    private perfilService: PerfilService,
    private authService: AuthService,
    private notificacion: NotificacionService
  ) {}

  ngOnInit(): void {
    this.perfilService.obtener().subscribe({
      next: (p) => {
        this.perfil = p;
        this.reiniciarFormulario();
        this.cargando = false;
      },
      error: (err) => {
        this.cargando = false;
        this.notificacion.error(mensajeError(err, 'No se pudo cargar tu perfil.'));
      }
    });
  }

  get titulo(): string {
    switch (this.perfil?.tipo) {
      case 'Veterinario': return 'Perfil del doctor';
      case 'Admin': return 'Perfil del administrador';
      default: return 'Mi perfil';
    }
  }

  async elegirFoto(event: Event): Promise<void> {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (!archivo) {
      return;
    }
    try {
      this.fotoNueva = await comprimirImagen(archivo, 500);
    } catch (e: any) {
      this.notificacion.error(e.message || 'No se pudo leer la imagen.');
    }
  }

  guardar(): void {
    if (!this.perfil) {
      return;
    }
    if (!this.form.nombre?.trim()) {
      this.notificacion.advertencia('El nombre no puede quedar vacío.');
      return;
    }
    if (this.form.correo && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(this.form.correo)) {
      this.notificacion.advertencia('Ingresa un correo electrónico válido.');
      return;
    }
    const cambios: CambiosPerfil = { ...this.form };
    if (this.fotoNueva) {
      cambios.foto = this.fotoNueva;
    }
    if (this.cambiarContrasena) {
      if (!cambios.contrasenaActual || !cambios.contrasenaNueva) {
        this.notificacion.advertencia('Escribe tu contraseña actual y la nueva.');
        return;
      }
      if (cambios.contrasenaNueva !== this.confirmacion) {
        this.notificacion.advertencia('La nueva contraseña y su confirmación no coinciden.');
        return;
      }
    } else {
      delete cambios.contrasenaActual;
      delete cambios.contrasenaNueva;
    }

    this.guardando = true;
    this.perfilService.actualizar(cambios).subscribe({
      next: (p) => {
        this.perfil = p;
        this.fotoNueva = null;
        this.cambiarContrasena = false;
        this.reiniciarFormulario();
        this.authService.actualizarDatosSesion(p.nombre, p.foto);
        this.guardando = false;
        this.notificacion.exito('Tus datos se guardaron correctamente.');
      },
      error: (err) => {
        this.guardando = false;
        this.notificacion.error(mensajeError(err, 'No se pudieron guardar los cambios.'));
      }
    });
  }

  private reiniciarFormulario(): void {
    const p = this.perfil!;
    this.form = {
      nombre: p.nombre,
      correo: p.correo,
      celular: p.celular,
      especialidad: p.especialidad,
      horario: p.horario,
      contrasenaActual: '',
      contrasenaNueva: ''
    };
    this.confirmacion = '';
  }
}
