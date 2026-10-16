import { Component, OnInit } from '@angular/core';
import { AdopcionService } from 'src/app/service/adopcion.service';
import {
  EstadoSolicitud, MascotaAdopcion, Refugio, SolicitudAdopcion, edadLegible, mensajeError
} from 'src/app/model/dogtor';
import { NotificacionService } from 'src/app/service/notificacion.service';

type Pestana = 'solicitudes' | 'mascotas' | 'refugios';

@Component({
  selector: 'app-adopcion-admin',
  templateUrl: './adopcion-admin.component.html',
  styleUrls: ['./adopcion-admin.component.css']
})
export class AdopcionAdminComponent implements OnInit {
  pestana: Pestana = 'solicitudes';

  solicitudes: SolicitudAdopcion[] = [];
  mascotas: MascotaAdopcion[] = [];
  refugios: Refugio[] = [];

  filtroSolicitudes: EstadoSolicitud | '' = '';
  mensaje = '';
  error = '';

  // Formularios
  mostrarFormMascota = false;
  editandoMascota: MascotaAdopcion | null = null;
  formMascota: MascotaAdopcion = this.mascotaVacia();
  idRefugioForm: number | null = null;

  mostrarFormRefugio = false;
  formRefugio: Refugio = this.refugioVacio();

  edadLegible = edadLegible;

  estadosSolicitud: { valor: EstadoSolicitud; etiqueta: string }[] = [
    { valor: 'PENDIENTE', etiqueta: 'Pendiente' },
    { valor: 'EN_REVISION', etiqueta: 'En revisión' },
    { valor: 'APROBADA', etiqueta: 'Aprobada' },
    { valor: 'RECHAZADA', etiqueta: 'Rechazada' }
  ];

  constructor(
    private notificacion: NotificacionService,private adopcionService: AdopcionService) {}

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.adopcionService.solicitudes().subscribe({
      next: (s) => this.solicitudes = s,
      error: (err) => this.error = mensajeError(err, 'No se pudieron cargar las solicitudes')
    });
    this.adopcionService.todasLasMascotas().subscribe({ next: (m) => this.mascotas = m });
    this.adopcionService.todosLosRefugios().subscribe({ next: (r) => this.refugios = r });
  }

  get solicitudesFiltradas(): SolicitudAdopcion[] {
    return this.filtroSolicitudes
      ? this.solicitudes.filter(s => s.estado === this.filtroSolicitudes)
      : this.solicitudes;
  }

  get refugiosValidados(): Refugio[] {
    return this.refugios.filter(r => r.validado);
  }

  contar(estado: EstadoSolicitud): number {
    return this.solicitudes.filter(s => s.estado === estado).length;
  }

  contarMascotas(estado: string): number {
    return this.mascotas.filter(m => m.estado === estado).length;
  }

  // ---------- Solicitudes ----------

  async cambiarEstado(solicitud: SolicitudAdopcion, estado: EstadoSolicitud): Promise<void> {
    if (estado === 'APROBADA' && !(await this.notificacion.confirmar({
      titulo: 'Confirmar adopción',
      mensaje: `¿Confirmas la adopción de ${solicitud.mascotaAdopcion?.nombre} por ${solicitud.nombreSolicitante}? ` +
               'La mascota saldrá del catálogo y las demás solicitudes se rechazarán.',
      textoConfirmar: 'Sí, aprobar'
    }))) {
      return;
    }
    this.limpiarMensajes();
    this.adopcionService.cambiarEstadoSolicitud(solicitud.idSolicitud!, estado).subscribe({
      next: () => {
        this.mensaje = `Solicitud de ${solicitud.nombreSolicitante} marcada como ${this.etiquetaSolicitud(estado).toLowerCase()}.`;
        this.cargarTodo();
      },
      error: (err) => this.error = mensajeError(err)
    });
  }

  etiquetaSolicitud(estado?: string): string {
    return this.estadosSolicitud.find(e => e.valor === estado)?.etiqueta || estado || '';
  }

  claseSolicitud(estado?: string): string {
    switch (estado) {
      case 'EN_REVISION': return 'dt-badge-gold';
      case 'APROBADA': return 'dt-badge-success';
      case 'RECHAZADA': return 'dt-badge-muted';
      default: return '';
    }
  }

  // ---------- Mascotas ----------

  nuevaMascota(): void {
    this.editandoMascota = null;
    this.formMascota = this.mascotaVacia();
    this.idRefugioForm = this.refugiosValidados[0]?.idRefugio ?? null;
    this.mostrarFormMascota = true;
    this.limpiarMensajes();
  }

  editarMascota(m: MascotaAdopcion): void {
    this.editandoMascota = m;
    this.formMascota = { ...m };
    this.idRefugioForm = m.refugio?.idRefugio ?? null;
    this.mostrarFormMascota = true;
    this.limpiarMensajes();
  }

  guardarMascota(): void {
    const m = this.formMascota;
    if (!m.nombre?.trim() || !m.foto?.trim() || !m.especie) {
      this.error = 'Nombre, especie y foto son obligatorios.';
      return;
    }
    this.limpiarMensajes();
    const peticion = this.editandoMascota
      ? this.adopcionService.actualizarMascota(this.editandoMascota.idMascotaAdopcion!, m)
      : this.adopcionService.publicarMascota(this.idRefugioForm!, m);
    peticion.subscribe({
      next: (guardada) => {
        this.mensaje = this.editandoMascota ? `${guardada.nombre} fue actualizada.` : `${guardada.nombre} ya está publicada en el catálogo.`;
        this.mostrarFormMascota = false;
        this.cargarTodo();
      },
      error: (err) => this.error = mensajeError(err)
    });
  }

  claseMascota(estado?: string): string {
    switch (estado) {
      case 'EN_PROCESO': return 'dt-badge-gold';
      case 'ADOPTADA': return 'dt-badge-success';
      default: return '';
    }
  }

  etiquetaMascota(estado?: string): string {
    switch (estado) {
      case 'EN_PROCESO': return 'En proceso';
      case 'ADOPTADA': return 'Adoptada';
      default: return 'Disponible';
    }
  }

  // ---------- Refugios ----------

  guardarRefugio(): void {
    if (!this.formRefugio.nombre.trim() || !this.formRefugio.ciudad.trim()) {
      this.error = 'Nombre y ciudad del refugio son obligatorios.';
      return;
    }
    this.limpiarMensajes();
    this.adopcionService.registrarRefugio(this.formRefugio).subscribe({
      next: (r) => {
        this.mensaje = `${r.nombre} quedó registrado. Valídalo para que pueda publicar mascotas.`;
        this.mostrarFormRefugio = false;
        this.formRefugio = this.refugioVacio();
        this.cargarTodo();
      },
      error: (err) => this.error = mensajeError(err)
    });
  }

  cambiarValidacion(refugio: Refugio): void {
    this.limpiarMensajes();
    this.adopcionService.validarRefugio(refugio.idRefugio!, !refugio.validado).subscribe({
      next: (r) => {
        this.mensaje = r.validado ? `${r.nombre} fue validado y ya puede publicar.` : `Se retiró la validación de ${r.nombre}.`;
        this.cargarTodo();
      },
      error: (err) => this.error = mensajeError(err)
    });
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'assets/mascota-placeholder.svg';
  }

  private limpiarMensajes(): void {
    this.mensaje = '';
    this.error = '';
  }

  private mascotaVacia(): MascotaAdopcion {
    return {
      nombre: '', especie: 'PERRO', raza: '', edadMeses: 12, tamano: 'MEDIANO', sexo: 'Hembra',
      ciudad: '', descripcion: '', estadoSalud: '', foto: ''
    };
  }

  private refugioVacio(): Refugio {
    return { nombre: '', ciudad: '', telefono: '', correo: '', descripcion: '' };
  }
}
