import { Component, OnInit } from '@angular/core';
import { AdopcionService, FiltrosAdopcion } from 'src/app/service/adopcion.service';
import { MascotaAdopcion, SolicitudAdopcion, edadLegible, mensajeError } from 'src/app/model/dogtor';

@Component({
  selector: 'app-adopcion-catalogo',
  templateUrl: './adopcion-catalogo.component.html',
  styleUrls: ['./adopcion-catalogo.component.css']
})
export class AdopcionCatalogoComponent implements OnInit {
  mascotas: MascotaAdopcion[] = [];
  cargando = true;
  error = '';

  filtros: FiltrosAdopcion = { especie: '', tamano: '', ciudad: '', edad: '' };

  especies = [
    { valor: '', etiqueta: 'Todas' },
    { valor: 'PERRO', etiqueta: 'Perros' },
    { valor: 'GATO', etiqueta: 'Gatos' }
  ];
  tamanos = [
    { valor: '', etiqueta: 'Cualquier tamaño' },
    { valor: 'PEQUENO', etiqueta: 'Pequeño' },
    { valor: 'MEDIANO', etiqueta: 'Mediano' },
    { valor: 'GRANDE', etiqueta: 'Grande' }
  ];
  edades = [
    { valor: '', etiqueta: 'Cualquier edad' },
    { valor: 'CACHORRO', etiqueta: 'Cachorro (< 1 año)' },
    { valor: 'ADULTO', etiqueta: 'Adulto (1 a 7 años)' },
    { valor: 'SENIOR', etiqueta: 'Senior (> 7 años)' }
  ];

  // Detalle y solicitud
  seleccionada: MascotaAdopcion | null = null;
  mostrarFormulario = false;
  enviando = false;
  enviada = false;
  errorSolicitud = '';
  solicitud: SolicitudAdopcion = this.solicitudVacia();

  edadLegible = edadLegible;

  constructor(private adopcionService: AdopcionService) {}

  ngOnInit(): void {
    this.buscar();
  }

  buscar(): void {
    this.cargando = true;
    this.error = '';
    this.adopcionService.catalogo(this.filtros).subscribe({
      next: (mascotas) => {
        this.mascotas = mascotas;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No pudimos cargar el catálogo de adopción. Verifica tu conexión e inténtalo de nuevo.';
        this.cargando = false;
      }
    });
  }

  setEspecie(valor: string): void {
    this.filtros.especie = valor;
    this.buscar();
  }

  limpiarFiltros(): void {
    this.filtros = { especie: '', tamano: '', ciudad: '', edad: '' };
    this.buscar();
  }

  hayFiltros(): boolean {
    return Object.values(this.filtros).some(v => !!v);
  }

  abrir(mascota: MascotaAdopcion): void {
    this.seleccionada = mascota;
    this.mostrarFormulario = false;
    this.enviada = false;
    this.errorSolicitud = '';
    this.solicitud = this.solicitudVacia();
  }

  cerrar(): void {
    this.seleccionada = null;
  }

  enviarSolicitud(): void {
    if (!this.seleccionada?.idMascotaAdopcion) {
      return;
    }
    const s = this.solicitud;
    if (!s.nombreSolicitante.trim() || !s.correo.trim() || !s.telefono.trim()) {
      this.errorSolicitud = 'Nombre, correo y teléfono son obligatorios.';
      return;
    }
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(s.correo)) {
      this.errorSolicitud = 'Ingresa un correo electrónico válido.';
      return;
    }
    this.enviando = true;
    this.errorSolicitud = '';
    this.adopcionService.enviarSolicitud(this.seleccionada.idMascotaAdopcion, s).subscribe({
      next: () => {
        this.enviando = false;
        this.enviada = true;
      },
      error: (err) => {
        this.enviando = false;
        this.errorSolicitud = mensajeError(err, 'No pudimos enviar tu solicitud.');
      }
    });
  }

  etiquetaTamano(tamano?: string): string {
    return this.tamanos.find(t => t.valor === tamano)?.etiqueta || '';
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'assets/mascota-placeholder.svg';
  }

  private solicitudVacia(): SolicitudAdopcion {
    return { nombreSolicitante: '', correo: '', telefono: '', ciudad: '', mensaje: '' };
  }
}
