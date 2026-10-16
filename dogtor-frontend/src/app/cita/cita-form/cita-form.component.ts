import { Component, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from 'src/app/service/mascota.service';
import { VeterinarioService } from 'src/app/service/veterinario.service';
import { ServicioService } from 'src/app/service/servicio.service';
import { CitaService } from 'src/app/service/cita.service';
import { CitaCL } from 'src/app/model/cita-cl';
import { MascotaCL } from 'src/app/model/mascota-cl';
import { ServicioCL } from 'src/app/model/servicio-cl';
import { VeterinarioCL } from 'src/app/model/veterinario-cl';
import { AuthService } from 'src/app/service/auth.service';
import { mensajeError } from 'src/app/model/dogtor';
import { NotificacionService } from 'src/app/service/notificacion.service';

@Component({
  selector: 'app-cita-form',
  templateUrl: './cita-form.component.html',
  styleUrls: ['./cita-form.component.css']
})
export class CitaFormComponent implements OnInit {
  @ViewChild('citaForm') citaForm!: NgForm;
  @Output() addCitaEvent = new EventEmitter<CitaCL>();

  userType: string | null = null;

  Formcita: CitaCL = new CitaCL(
    undefined,
    new Date(),
    '',
    new MascotaCL(),
    new VeterinarioCL(),
    new ServicioCL(),
    'AGENDADA'
  );

  modoEdicion = false;
  errors: string[] = [];
  guardando = false;

  // Flujo en 3 pasos (requerimiento de usabilidad: agendar en no más de 3 pasos)
  paso = 1;
  fecha = '';
  hora = '';
  hoy = this.formatearFecha(new Date());
  horarios: string[] = [];
  cargandoHorarios = false;

  mascotas: MascotaCL[] = [];
  servicios: ServicioCL[] = [];
  veterinarios: VeterinarioCL[] = [];

  idVeterinario?: number;
  veterinarioAsignado?: VeterinarioCL;

  tiposConsulta = [
    { valor: 'CONTROL', etiqueta: 'Control' },
    { valor: 'VACUNACION', etiqueta: 'Vacunación' },
    { valor: 'URGENCIA', etiqueta: 'Urgencia' },
    { valor: 'CIRUGIA', etiqueta: 'Cirugía' }
  ];

  estadosCita = ['AGENDADA', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA'];

  constructor(
    private notificacion: NotificacionService,
    private route: ActivatedRoute,
    private router: Router,
    private mascotaService: MascotaService,
    private veterinarioService: VeterinarioService,
    private servicioService: ServicioService,
    private citaService: CitaService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userType = this.authService.getUserType();
    this.Formcita.tipoConsulta = 'CONTROL';
    const idCita = Number(this.route.snapshot.paramMap.get('id'));

    if (!isNaN(idCita) && idCita > 0) {
      this.modoEdicion = true;
      this.loadCita(idCita);
    } else if (this.userType === 'Veterinario') {
      this.idVeterinario = history.state.idVeterinario || this.authService.getUserId() || undefined;
      if (this.idVeterinario) {
        this.loadVeterinario(this.idVeterinario);
      }
    }

    this.loadMascotas();
    this.loadServicios();
    this.loadVeterinarios();
  }

  get veterinariosActivos(): VeterinarioCL[] {
    return this.veterinarios.filter(v => v.estado !== 0 || v.idVeterinario === this.Formcita.veterinario?.idVeterinario);
  }

  private loadCita(idCita: number): void {
    this.citaService.obtenerCitaPorId(idCita).subscribe({
      next: (cita) => {
        this.Formcita = CitaCL.fromBackendData(cita);
        const fechaHora = new Date(this.Formcita.fechaHora);
        this.fecha = this.formatearFecha(fechaHora);
        this.hora = `${String(fechaHora.getHours()).padStart(2, '0')}:${String(fechaHora.getMinutes()).padStart(2, '0')}`;
        this.cargarHorarios();
      },
      error: () => this.errors = ['No se pudo cargar la cita']
    });
  }

  private loadVeterinario(idVet: number): void {
    this.veterinarioService.obtenerVeterinarioPorId(idVet).subscribe({
      next: (veterinario) => {
        this.veterinarioAsignado = veterinario;
        this.Formcita.veterinario = veterinario;
      },
      error: (err) => console.error('Error cargando veterinario:', err)
    });
  }

  private loadMascotas(): void {
    if (this.userType === 'Cliente') {
      const idCliente = history.state.idCliente || this.authService.getUserId();
      if (!idCliente) {
        return;
      }
      this.mascotaService.getMascotasPorCliente(idCliente).subscribe({
        next: (mascotas) => this.mascotas = mascotas,
        error: (err) => console.error('Error cargando mascotas:', err)
      });
    } else {
      this.mascotaService.findAll().subscribe({
        next: (mascotas) => this.mascotas = mascotas,
        error: (err) => console.error('Error cargando mascotas:', err)
      });
    }
  }

  private loadServicios(): void {
    this.servicioService.findAll().subscribe({
      next: (servicios) => this.servicios = servicios,
      error: (err) => console.error('Error cargando servicios:', err)
    });
  }

  private loadVeterinarios(): void {
    this.veterinarioService.obtenerTodosVeterinarios().subscribe({
      next: (veterinarios) => this.veterinarios = veterinarios,
      error: (err) => console.error('Error cargando veterinarios:', err)
    });
  }

  // ---------- Pasos ----------

  paso1Valido(): boolean {
    return !!this.Formcita.mascota?.idMascota && !!this.Formcita.servicio?.idServicio && !!this.Formcita.tipoConsulta;
  }

  irAPaso(paso: number): void {
    this.errors = [];
    this.paso = paso;
  }

  onVeterinarioChange(): void {
    this.hora = '';
    this.cargarHorarios();
  }

  cargarHorarios(): void {
    const idVet = this.Formcita.veterinario?.idVeterinario;
    if (!idVet || !this.fecha) {
      this.horarios = [];
      return;
    }
    this.cargandoHorarios = true;
    const horaActual = this.hora;
    this.citaService.obtenerDisponibilidad(idVet, this.fecha).subscribe({
      next: (horarios) => {
        // En edición se conserva el horario actual de la cita aunque esté "ocupado" por ella misma
        this.horarios = this.modoEdicion && horaActual && !horarios.includes(horaActual)
          ? [horaActual, ...horarios].sort()
          : horarios;
        if (!this.horarios.includes(this.hora)) {
          this.hora = '';
        }
        this.cargandoHorarios = false;
      },
      error: () => {
        this.horarios = [];
        this.cargandoHorarios = false;
      }
    });
  }

  doctorSeleccionado(): VeterinarioCL | undefined {
    const id = this.Formcita.veterinario?.idVeterinario;
    return this.veterinarios.find(v => v.idVeterinario === id) || this.veterinarioAsignado;
  }

  nombreMascota(): string {
    return this.mascotas.find(m => m.idMascota === this.Formcita.mascota?.idMascota)?.nombre || '';
  }

  nombreServicio(): string {
    return this.servicios.find(s => s.idServicio === this.Formcita.servicio?.idServicio)?.nombre || '';
  }

  etiquetaTipo(): string {
    return this.tiposConsulta.find(t => t.valor === this.Formcita.tipoConsulta)?.etiqueta || '';
  }

  fechaHoraSeleccionada(): Date | null {
    return this.fecha && this.hora ? new Date(`${this.fecha}T${this.hora}`) : null;
  }

  // ---------- Guardado ----------

  guardarCita(): void {
    this.errors = [];
    const fechaHora = this.fechaHoraSeleccionada();
    if (!fechaHora) {
      this.errors = ['Selecciona una fecha y un horario disponible'];
      return;
    }
    this.Formcita.fechaHora = fechaHora;
    this.Formcita.sede = this.doctorSeleccionado()?.sede || this.Formcita.sede;

    const validation = this.Formcita.validate();
    if (!validation.isValid) {
      this.errors = validation.errors;
      return;
    }
    if (!this.Formcita.veterinario?.idVeterinario) {
      this.errors = ['Debes seleccionar un doctor'];
      return;
    }

    this.guardando = true;
    const peticion = this.modoEdicion
      ? this.citaService.actualizarCita(this.Formcita.idCita, this.Formcita)
      : this.citaService.crearCita(this.Formcita);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.notificacion.exito(this.modoEdicion ? 'Cita actualizada correctamente' : '¡Cita agendada! Te enviaremos un recordatorio.');
        this.volver();
      },
      error: (err) => {
        this.guardando = false;
        this.errors = [mensajeError(err, 'No se pudo guardar la cita')];
        // Si el horario se ocupó mientras tanto, refrescamos la disponibilidad
        if (err.status === 409) {
          this.paso = 2;
          this.cargarHorarios();
        }
      }
    });
  }

  volver(): void {
    if (window.history.length > 1) {
      window.history.back();
    } else {
      this.router.navigate(['/home']);
    }
  }

  private formatearFecha(fecha: Date): string {
    const local = new Date(fecha.getTime() - fecha.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 10);
  }
}
