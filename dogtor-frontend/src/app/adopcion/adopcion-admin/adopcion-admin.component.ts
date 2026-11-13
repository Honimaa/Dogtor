import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AdopcionService, ModoGestion } from 'src/app/service/adopcion.service';
import {
  EstadoSolicitud, MascotaAdopcion, Refugio, SolicitudAdopcion, edadLegible, mensajeError
} from 'src/app/model/dogtor';
import { NotificacionService } from 'src/app/service/notificacion.service';
import { comprimirImagen } from 'src/app/utils/imagen';
import { AuthService } from 'src/app/service/auth.service';

type Pestana = 'solicitudes' | 'mascotas' | 'refugios' | 'perfil';

/**
 * Gestión de adopciones. La usan dos perfiles (según la ruta):
 *  - admin: ve todos los refugios, mascotas y solicitudes, y valida refugios.
 *  - refugio: ve y gestiona solo sus mascotas y solicitudes, y edita los datos de su refugio.
 */
@Component({
  selector: 'app-adopcion-admin',
  templateUrl: './adopcion-admin.component.html',
  styleUrls: ['./adopcion-admin.component.css']
})
export class AdopcionAdminComponent implements OnInit {
  modo: ModoGestion = 'admin';
  pestana: Pestana = 'solicitudes';

  /** Solo en modo refugio: el refugio de la sesión y el formulario de sus datos */
  miRefugio: Refugio | null = null;
  formPerfil: Refugio = this.refugioVacio();

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
  crearCuentaRefugio = true;
  confirmacionRefugio = '';

  /** Admin: crear acceso o poner contraseña nueva a un refugio ya registrado */
  refugioCuenta: Refugio | null = null;
  formCuenta = { nombreUsuario: '', contrasena: '', confirmacion: '' };

  /** Refugio: cambio de su propia contraseña */
  formClave = { actual: '', nueva: '', confirmacion: '' };

  edadLegible = edadLegible;

  estadosSolicitud: { valor: EstadoSolicitud; etiqueta: string }[] = [
    { valor: 'PENDIENTE', etiqueta: 'Pendiente' },
    { valor: 'EN_REVISION', etiqueta: 'En revisión' },
    { valor: 'APROBADA', etiqueta: 'Aprobada' },
    { valor: 'RECHAZADA', etiqueta: 'Rechazada' }
  ];

  constructor(
    private notificacion: NotificacionService,
    private adopcionService: AdopcionService,
    private authService: AuthService,
    private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.modo = this.route.snapshot.data['modo'] === 'refugio' ? 'refugio' : 'admin';
    this.cargarTodo();
  }

  get esRefugio(): boolean {
    return this.modo === 'refugio';
  }

  async elegirLogo(event: Event): Promise<void> {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (!archivo) {
      return;
    }
    try {
      this.formPerfil.logo = await comprimirImagen(archivo, 400);
    } catch (e: any) {
      this.notificacion.error(e.message || 'No se pudo leer la imagen.');
    }
  }

  async elegirFoto(event: Event): Promise<void> {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (!archivo) {
      return;
    }
    try {
      this.formMascota.foto = await comprimirImagen(archivo, 800);
    } catch (e: any) {
      this.notificacion.error(e.message || 'No se pudo leer la imagen.');
    }
  }

  cargarTodo(): void {
    this.adopcionService.solicitudes(this.modo).subscribe({
      next: (s) => this.solicitudes = s,
      error: (err) => this.error = mensajeError(err, 'No se pudieron cargar las solicitudes')
    });
    this.adopcionService.todasLasMascotas(this.modo).subscribe({ next: (m) => this.mascotas = m });
    if (this.esRefugio) {
      this.adopcionService.miRefugio().subscribe({
        next: (r) => {
          this.miRefugio = r;
          this.refugios = [r];
          this.formPerfil = { ...r };
        },
        error: (err) => this.error = mensajeError(err, 'No se pudieron cargar los datos del refugio')
      });
    } else {
      this.adopcionService.todosLosRefugios().subscribe({ next: (r) => this.refugios = r });
    }
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
    this.adopcionService.cambiarEstadoSolicitud(solicitud.idSolicitud!, estado, this.modo).subscribe({
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
      ? this.adopcionService.actualizarMascota(this.editandoMascota.idMascotaAdopcion!, m, this.modo)
      : this.adopcionService.publicarMascota(m, this.modo, this.idRefugioForm ?? undefined);
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

  nuevoRefugio(): void {
    this.formRefugio = this.refugioVacio();
    this.crearCuentaRefugio = true;
    this.confirmacionRefugio = '';
    this.limpiarMensajes();
    this.mostrarFormRefugio = true;
  }

  guardarRefugio(): void {
    const r = this.formRefugio;
    if (!r.nombre.trim() || !r.ciudad.trim()) {
      this.error = 'Nombre y ciudad del refugio son obligatorios.';
      return;
    }
    if (this.crearCuentaRefugio) {
      if (!r.nombreUsuario?.trim() || !r.contrasena) {
        this.error = 'Escribe el usuario y la contraseña de la cuenta, o desmarca «Crear cuenta de acceso».';
        return;
      }
      if (r.contrasena !== this.confirmacionRefugio) {
        this.error = 'Las contraseñas no coinciden.';
        return;
      }
    }
    const datos: Refugio = this.crearCuentaRefugio ? r : { ...r, nombreUsuario: undefined, contrasena: undefined };
    this.limpiarMensajes();
    this.adopcionService.registrarRefugio(datos).subscribe({
      next: (guardado) => {
        this.mensaje = guardado.nombreUsuario
          ? `${guardado.nombre} quedó registrado con el usuario «${guardado.nombreUsuario}». Entrégale sus datos de acceso y valídalo para que pueda publicar.`
          : `${guardado.nombre} quedó registrado. Valídalo para que pueda publicar mascotas.`;
        this.mostrarFormRefugio = false;
        this.formRefugio = this.refugioVacio();
        this.cargarTodo();
      },
      error: (err) => this.error = mensajeError(err)
    });
  }

  abrirCuenta(refugio: Refugio): void {
    this.refugioCuenta = refugio;
    this.formCuenta = { nombreUsuario: refugio.nombreUsuario || '', contrasena: '', confirmacion: '' };
    this.limpiarMensajes();
  }

  guardarCuenta(): void {
    const r = this.refugioCuenta;
    const f = this.formCuenta;
    if (!r) {
      return;
    }
    if ((!r.conCuenta && !f.nombreUsuario.trim()) || !f.contrasena) {
      this.error = 'Completa los datos de acceso.';
      return;
    }
    if (f.contrasena !== f.confirmacion) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }
    this.limpiarMensajes();
    this.adopcionService.asignarCuentaRefugio(r.idRefugio!, f.nombreUsuario.trim(), f.contrasena).subscribe({
      next: (guardado) => {
        this.mensaje = r.conCuenta
          ? `Se asignó una contraseña nueva a ${guardado.nombre} (usuario «${guardado.nombreUsuario}»).`
          : `${guardado.nombre} ya tiene acceso con el usuario «${guardado.nombreUsuario}». Podrá cambiar la contraseña desde su panel.`;
        this.refugioCuenta = null;
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

  // ---------- Mi refugio (modo refugio) ----------

  cambiarContrasena(): void {
    const f = this.formClave;
    if (!f.actual || !f.nueva) {
      this.error = 'Escribe tu contraseña actual y la nueva.';
      return;
    }
    if (f.nueva !== f.confirmacion) {
      this.error = 'Las contraseñas nuevas no coinciden.';
      return;
    }
    this.limpiarMensajes();
    this.adopcionService.cambiarMiContrasena(f.actual, f.nueva).subscribe({
      next: () => {
        this.formClave = { actual: '', nueva: '', confirmacion: '' };
        this.mensaje = 'Tu contraseña quedó actualizada. Úsala la próxima vez que inicies sesión.';
      },
      error: (err) => this.error = mensajeError(err)
    });
  }

  guardarPerfil(): void {
    const p = this.formPerfil;
    if (!p.nombre?.trim() || !p.ciudad?.trim()) {
      this.error = 'Nombre y ciudad del refugio son obligatorios.';
      return;
    }
    this.limpiarMensajes();
    this.adopcionService.actualizarMiRefugio(p).subscribe({
      next: (r) => {
        this.miRefugio = r;
        this.refugios = [r];
        this.formPerfil = { ...r };
        this.authService.actualizarDatosSesion(r.nombre, r.logo || 'assets/avatar-placeholder.svg');
        this.mensaje = 'Los datos del refugio quedaron actualizados.';
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
    return {
      nombre: '', ciudad: '', telefono: '', correo: '', descripcion: '',
      instagram: '', facebook: '', tiktok: '', sitioWeb: '', nombreUsuario: '', contrasena: ''
    };
  }
}
