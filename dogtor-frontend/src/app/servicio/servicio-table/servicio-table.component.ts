import { Component, OnInit } from '@angular/core';
import { ServicioService } from 'src/app/service/servicio.service';
import { ServicioCL } from 'src/app/model/servicio-cl';
import { mensajeError } from 'src/app/model/dogtor';
import { NotificacionService } from 'src/app/service/notificacion.service';

@Component({
  selector: 'app-servicio-table',
  templateUrl: './servicio-table.component.html',
  styleUrls: ['./servicio-table.component.css']
})
export class ServicioTableComponent implements OnInit {
  servicios: ServicioCL[] = [];
  opcionOrdenamiento: string = 'id'; // Inicialmente ordenamos por ID

  // Dogtor: crear y editar servicios
  editando: ServicioCL | null = null;
  esNuevo = false;
  form = { nombre: '', descripcion: '', precioBase: 0, imagenFrontal: '' };
  guardando = false;
  errorForm = '';

  constructor(
    private servicioService: ServicioService,
    private notificacion: NotificacionService
  ) {}

  ngOnInit(): void {
    this.cargarServicios();
  }

  cargarServicios(): void {
    this.servicioService.findAll().subscribe({
      next: (servicios: ServicioCL[]) => {
        this.servicios = servicios;
        this.ordenarServicios();
      },
      error: (err) => {
        console.error('Error al cargar los servicios', err);
      }
    });
  }

  ordenarServicios(): void {
    if (this.opcionOrdenamiento === 'id') {
      this.servicios.sort((a, b) => a.idServicio - b.idServicio);
    } else if (this.opcionOrdenamiento === 'precioAsc') {
      this.servicios.sort((a, b) => a.precioBase - b.precioBase);
    } else if (this.opcionOrdenamiento === 'precioDesc') {
      this.servicios.sort((a, b) => b.precioBase - a.precioBase);
    }
  }

  abrirNuevo(): void {
    this.esNuevo = true;
    this.errorForm = '';
    this.editando = new ServicioCL();
    this.form = { nombre: '', descripcion: '', precioBase: 0, imagenFrontal: '' };
  }

  abrirEditar(servicio: ServicioCL): void {
    this.esNuevo = false;
    this.errorForm = '';
    this.editando = servicio;
    this.form = {
      nombre: servicio.nombre,
      descripcion: servicio.descripcion,
      precioBase: servicio.precioBase,
      imagenFrontal: servicio.imagenFrontal || ''
    };
  }

  cerrar(): void {
    if (!this.guardando) {
      this.editando = null;
    }
  }

  guardar(): void {
    if (!this.editando) {
      return;
    }
    if (!this.form.nombre?.trim()) {
      this.errorForm = 'El nombre del servicio es obligatorio.';
      return;
    }
    if (!this.form.descripcion?.trim()) {
      this.errorForm = 'La descripción es obligatoria.';
      return;
    }
    if (this.form.precioBase == null || this.form.precioBase < 0) {
      this.errorForm = 'El precio base debe ser mayor o igual a 0.';
      return;
    }
    const imagen = this.form.imagenFrontal?.trim();
    // Si la imagen trasera era la misma que la frontal, se mantienen iguales
    const mismaImagen = !this.editando.imagenTrasera || this.editando.imagenTrasera === this.editando.imagenFrontal;
    const datos: Partial<ServicioCL> = {
      nombre: this.form.nombre.trim(),
      descripcion: this.form.descripcion.trim(),
      precioBase: this.form.precioBase,
      imagenFrontal: imagen,
      imagenTrasera: mismaImagen ? imagen : this.editando.imagenTrasera
    };
    this.guardando = true;
    const peticion = this.esNuevo
      ? this.servicioService.crearServicio(datos)
      : this.servicioService.actualizarServicio(this.editando.idServicio, datos);
    peticion.subscribe({
      next: () => {
        this.notificacion.exito(this.esNuevo ? `"${datos.nombre}" se agregó a los servicios.` : `"${datos.nombre}" se actualizó.`);
        this.guardando = false;
        this.editando = null;
        this.cargarServicios();
      },
      error: (err) => {
        this.guardando = false;
        this.errorForm = mensajeError(err, 'No se pudo guardar el servicio.');
      }
    });
  }
}
