import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import jsPDF from 'jspdf';
import { MascotaService } from 'src/app/service/mascota.service';
import { HistoriaClinicaService } from 'src/app/service/historia-clinica.service';
import { AuthService } from 'src/app/service/auth.service';
import { MascotaCL } from 'src/app/model/mascota-cl';
import { EventoClinico, TIPOS_EVENTO, TipoEventoClinico, mensajeError } from 'src/app/model/dogtor';
import { NotificacionService } from 'src/app/service/notificacion.service';

@Component({
  selector: 'app-historia-clinica',
  templateUrl: './historia-clinica.component.html',
  styleUrls: ['./historia-clinica.component.css'],
  providers: [DatePipe]
})
export class HistoriaClinicaComponent implements OnInit {
  idMascota = 0;
  mascota: MascotaCL | null = null;
  eventos: EventoClinico[] = [];
  alergias: EventoClinico[] = [];
  filtro: TipoEventoClinico | '' = '';
  cargando = true;
  error = '';

  userType: string | null = null;
  puedeEditar = false;

  mostrarForm = false;
  guardando = false;
  errorForm = '';
  nuevo: EventoClinico = this.eventoVacio();

  tipos = TIPOS_EVENTO;

  constructor(
    private notificacion: NotificacionService,
    private route: ActivatedRoute,
    private mascotaService: MascotaService,
    private historiaService: HistoriaClinicaService,
    private authService: AuthService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.userType = this.authService.getUserType();
    // REQ-HCL-07: solo doctores (y administradores) editan; el propietario consulta en modo lectura
    this.puedeEditar = this.userType === 'Veterinario' || this.userType === 'Admin';
    this.idMascota = Number(this.route.snapshot.paramMap.get('id'));

    this.mascotaService.getMascota(this.idMascota).subscribe({
      next: (m) => this.mascota = m,
      error: () => this.error = 'No encontramos la mascota.'
    });
    this.cargarAlergias();
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.historiaService.obtenerHistorial(this.idMascota, this.filtro || undefined).subscribe({
      next: (eventos) => {
        this.eventos = eventos;
        this.cargando = false;
      },
      error: (err) => {
        this.error = mensajeError(err, 'No se pudo cargar la historia clínica.');
        this.cargando = false;
      }
    });
  }

  cargarAlergias(): void {
    this.historiaService.obtenerAlergias(this.idMascota).subscribe({
      next: (alergias) => this.alergias = alergias
    });
  }

  filtrar(tipo: TipoEventoClinico | ''): void {
    this.filtro = tipo;
    this.cargar();
  }

  abrirFormulario(tipo: TipoEventoClinico = 'DIAGNOSTICO'): void {
    this.nuevo = this.eventoVacio();
    this.nuevo.tipo = tipo;
    this.errorForm = '';
    this.mostrarForm = true;
  }

  guardar(): void {
    if (!this.nuevo.titulo.trim()) {
      this.errorForm = 'El título es obligatorio.';
      return;
    }
    if (!this.nuevo.fecha) {
      this.errorForm = 'La fecha es obligatoria.';
      return;
    }
    this.guardando = true;
    this.errorForm = '';
    const idVeterinario = this.userType === 'Veterinario' ? this.authService.getUserId() : null;
    const evento: EventoClinico = { ...this.nuevo, fechaProxima: this.nuevo.fechaProxima || null };

    this.historiaService.registrarEvento(this.idMascota, evento, idVeterinario).subscribe({
      next: () => {
        this.guardando = false;
        this.mostrarForm = false;
        this.cargar();
        this.cargarAlergias();
      },
      error: (err) => {
        this.guardando = false;
        this.errorForm = err.status === 403
          ? 'Solo los doctores autorizados pueden modificar la historia clínica.'
          : mensajeError(err, 'No se pudo registrar el evento.');
      }
    });
  }

  async eliminar(evento: EventoClinico): Promise<void> {
    if (!evento.idEvento || !(await this.notificacion.confirmar({
      titulo: 'Eliminar evento',
      mensaje: `¿Eliminar "${evento.titulo}" de la historia clínica? Esta acción no se puede deshacer.`,
      textoConfirmar: 'Eliminar',
      peligro: true
    }))) {
      return;
    }
    this.historiaService.eliminarEvento(evento.idEvento).subscribe({
      next: () => {
        this.cargar();
        this.cargarAlergias();
      },
      error: (err) => this.notificacion.error(mensajeError(err, 'No se pudo eliminar el evento.'))
    });
  }

  infoTipo(tipo: string) {
    return this.tipos.find(t => t.valor === tipo) || { valor: tipo, etiqueta: tipo, icono: 'document-outline' };
  }

  nombresAlergias(): string {
    return this.alergias.map(a => a.titulo).join(', ');
  }

  estaVencida(evento: EventoClinico): boolean {
    return !!evento.fechaProxima && new Date(evento.fechaProxima) < new Date();
  }

  imprimir(): void {
    window.print();
  }

  // REQ-HCL-08: resumen descargable en PDF
  descargarPdf(): void {
    const doc = new jsPDF();
    const margen = 15;
    const ancho = doc.internal.pageSize.getWidth() - margen * 2;
    let y = 20;

    const salto = (alto: number) => {
      if (y + alto > 280) {
        doc.addPage();
        y = 20;
      }
    };

    doc.setFillColor(29, 149, 219);
    doc.rect(0, 0, doc.internal.pageSize.getWidth(), 28, 'F');
    doc.setTextColor(255, 255, 255);
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(18);
    doc.text('Dogtor', margen, 13);
    doc.setFontSize(11);
    doc.setFont('helvetica', 'normal');
    doc.text('Resumen de historia clínica', margen, 21);
    doc.text(this.datePipe.transform(new Date(), 'dd/MM/yyyy') || '', margen + ancho, 21, { align: 'right' });

    y = 40;
    doc.setTextColor(42, 78, 104);
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(15);
    doc.text(this.mascota?.nombre || `Mascota #${this.idMascota}`, margen, y);
    y += 7;
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    const datos = [
      this.mascota?.raza && `Raza: ${this.mascota.raza}`,
      this.mascota?.edad != null && `Edad: ${this.mascota.edad} años`,
      this.mascota?.peso && `Peso: ${this.mascota.peso} kg`,
      this.mascota?.cliente?.nombre && `Propietario: ${this.mascota.cliente.nombre}`
    ].filter(Boolean).join('   ·   ');
    doc.text(datos, margen, y);
    y += 10;

    if (this.alergias.length) {
      doc.setFillColor(253, 236, 234);
      const texto = 'ALERGIAS: ' + this.alergias.map(a => a.titulo).join(', ');
      const lineas = doc.splitTextToSize(texto, ancho - 6);
      doc.rect(margen, y - 5, ancho, lineas.length * 5 + 5, 'F');
      doc.setTextColor(231, 76, 60);
      doc.setFont('helvetica', 'bold');
      doc.text(lineas, margen + 3, y);
      y += lineas.length * 5 + 8;
    }

    doc.setTextColor(42, 78, 104);
    this.eventos.forEach((e) => {
      const descripcion = doc.splitTextToSize(e.descripcion || '', ancho - 4);
      salto(18 + descripcion.length * 5);
      doc.setDrawColor(226, 232, 240);
      doc.line(margen, y - 4, margen + ancho, y - 4);
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(10);
      doc.setTextColor(29, 149, 219);
      doc.text(`${this.infoTipo(e.tipo).etiqueta.toUpperCase()}  ·  ${this.datePipe.transform(e.fecha, 'dd/MM/yyyy', 'UTC')}`, margen, y);
      y += 5;
      doc.setTextColor(42, 78, 104);
      doc.setFontSize(11);
      doc.text(e.titulo, margen, y);
      y += 5;
      doc.setFont('helvetica', 'normal');
      doc.setFontSize(9.5);
      if (descripcion.length) {
        doc.text(descripcion, margen, y);
        y += descripcion.length * 4.6;
      }
      const pie = [
        e.veterinario?.nombre && `Registrado por ${e.veterinario.nombre}`,
        e.fechaProxima && `Próxima dosis/control: ${this.datePipe.transform(e.fechaProxima, 'dd/MM/yyyy', 'UTC')}`
      ].filter(Boolean).join('   ·   ');
      if (pie) {
        doc.setTextColor(90, 102, 122);
        doc.text(pie, margen, y);
        doc.setTextColor(42, 78, 104);
        y += 5;
      }
      y += 6;
    });

    if (!this.eventos.length) {
      doc.text('Sin eventos clínicos registrados.', margen, y);
    }

    doc.save(`historia-clinica-${(this.mascota?.nombre || this.idMascota).toString().toLowerCase()}.pdf`);
  }

  private eventoVacio(): EventoClinico {
    return {
      tipo: 'DIAGNOSTICO',
      titulo: '',
      descripcion: '',
      fecha: new Date().toISOString().slice(0, 10),
      fechaProxima: ''
    };
  }
}
