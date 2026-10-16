import { Component } from '@angular/core';
import jsPDF from 'jspdf';

interface Segmento {
  premio: string;
  etiqueta: string;
  color: string;
  path: string;
  textoX: number;
  textoY: number;
  textoRotacion: number;
}

interface DatosEnvio {
  nombre: string;
  telefono: string;
  correo: string;
  direccion: string;
  ciudad: string;
  nombreMascota: string;
  cumpleMascota: string;
}

@Component({
  selector: 'app-ruleta-premios',
  templateUrl: './ruleta-premios.component.html',
  styleUrls: ['./ruleta-premios.component.css']
})
export class RuletaPremiosComponent {
  // Premios que se envían a domicilio (Dogtor no tiene sedes físicas para reclamarlos)
  premios = [
    { premio: 'Juguete sorpresa', etiqueta: 'Juguete', color: '#72c0ea' },
    { premio: 'Kit de snacks saludables', etiqueta: 'Snacks', color: '#e2b979' },
    { premio: 'Collar personalizado', etiqueta: 'Collar', color: '#1d95db' },
    { premio: 'Placa con su nombre', etiqueta: 'Placa', color: '#ffcb77' },
    { premio: 'Imán con la foto de tu mascota', etiqueta: 'Imán', color: '#529bcb' },
    { premio: 'Bandana Dogtor', etiqueta: 'Bandana', color: '#ff9999' },
    { premio: 'Peluche mordedor', etiqueta: 'Peluche', color: '#a084ca' }
  ];

  segmentos: Segmento[] = this.construirSegmentos();

  rotacion = 0;
  girando = false;
  premioGanado: string | null = null;

  // Datos de envío del premio
  datos: DatosEnvio = { nombre: '', telefono: '', correo: '', direccion: '', ciudad: '', nombreMascota: '', cumpleMascota: '' };
  errorEnvio = '';
  enviado = false;
  codigo = '';

  girarRuleta(): void {
    // Un solo giro por mascota
    if (this.girando || this.premioGanado) {
      return;
    }
    this.girando = true;
    this.premioGanado = null;

    const n = this.premios.length;
    const angulo = 360 / n;
    const indice = Math.floor(Math.random() * n);
    // El indicador está arriba: se gira la rueda para dejar el centro del premio debajo de él
    const destino = 360 - (indice * angulo + angulo / 2);
    const actual = ((this.rotacion % 360) + 360) % 360;
    const vueltas = (Math.floor(Math.random() * 3) + 5) * 360;
    this.rotacion += vueltas + ((destino - actual + 360) % 360);

    setTimeout(() => {
      this.premioGanado = this.premios[indice].premio;
      this.girando = false;
    }, 4200);
  }

  confirmarEnvio(): void {
    const d = this.datos;
    if (!d.nombre.trim() || !d.telefono.trim() || !d.correo.trim() || !d.direccion.trim() || !d.ciudad.trim() || !d.nombreMascota.trim()) {
      this.errorEnvio = 'Completa todos los campos obligatorios para enviarte el premio.';
      return;
    }
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(d.correo)) {
      this.errorEnvio = 'Ingresa un correo electrónico válido.';
      return;
    }
    this.errorEnvio = '';
    this.codigo = 'DOG-' + Math.random().toString(36).slice(2, 8).toUpperCase();
    this.enviado = true;
    this.generarComprobantePDF();
  }

  generarComprobantePDF(): void {
    if (!this.premioGanado) {
      return;
    }
    const d = this.datos;
    const doc = new jsPDF();

    doc.setFillColor(245, 248, 251);
    doc.rect(10, 10, 190, 277, 'F');
    doc.setDrawColor('#1d95db');
    doc.setLineWidth(3);
    doc.rect(15, 15, 180, 267);

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(26);
    doc.setTextColor('#1d95db');
    doc.text('¡Premio de cumpleaños!', 105, 42, { align: 'center' });
    doc.setDrawColor('#e2b979');
    doc.setLineWidth(2);
    doc.line(50, 49, 160, 49);

    doc.setFontSize(14);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor('#2a4e68');
    doc.text(`${d.nombreMascota} ha ganado:`, 105, 68, { align: 'center' });
    doc.setFont('helvetica', 'bolditalic');
    doc.setFontSize(20);
    doc.setTextColor('#1d95db');
    doc.text(this.premioGanado, 105, 82, { align: 'center' });

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.setTextColor('#2a4e68');
    doc.text(`Código de envío: ${this.codigo}`, 105, 96, { align: 'center' });

    doc.setFontSize(13);
    doc.text('Datos de envío', 30, 118);
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(11);
    doc.setTextColor('#333');
    const filas = [
      ['Destinatario', d.nombre],
      ['Dirección', d.direccion],
      ['Ciudad', d.ciudad],
      ['Teléfono', d.telefono],
      ['Correo', d.correo]
    ];
    filas.forEach(([etiqueta, valor], i) => {
      doc.setFont('helvetica', 'bold');
      doc.text(`${etiqueta}:`, 30, 130 + i * 9);
      doc.setFont('helvetica', 'normal');
      doc.text(valor, 70, 130 + i * 9, { maxWidth: 115 });
    });

    doc.setDrawColor('#72c0ea');
    doc.setLineWidth(1);
    doc.line(30, 182, 180, 182);
    doc.setFontSize(11);
    doc.setTextColor('#2a4e68');
    doc.text('No necesitas acudir a ningún lugar: enviaremos el premio a la dirección indicada.', 30, 194, { maxWidth: 150 });
    doc.text('Te contactaremos por teléfono o correo para coordinar la entrega.', 30, 206, { maxWidth: 150 });

    doc.setFontSize(9.5);
    doc.setTextColor('#555');
    doc.text('* Tiempo estimado de entrega: 5 a 8 días hábiles.', 30, 226);
    doc.text('* Envío gratuito a ciudades principales de Colombia.', 30, 233);
    doc.text('* Un premio por mascota al año. No acumulable con otras promociones.', 30, 240);

    doc.setFontSize(9);
    doc.setTextColor('#999');
    doc.text('© 2026 Dogtor S.A.S. Todos los derechos reservados.', 105, 278, { align: 'center' });

    doc.save(`premio-dogtor-${this.codigo}.pdf`);
  }

  reiniciar(): void {
    this.premioGanado = null;
    this.enviado = false;
    this.codigo = '';
    this.datos = { nombre: '', telefono: '', correo: '', direccion: '', ciudad: '', nombreMascota: '', cumpleMascota: '' };
  }

  // Porciones de la rueda en SVG (centro 0,0, radio 100). El ángulo 0 está arriba y crece en sentido horario.
  private construirSegmentos(): Segmento[] {
    const n = this.premios.length;
    const angulo = 360 / n;
    const punto = (grados: number, radio: number) => {
      const rad = (grados * Math.PI) / 180;
      return { x: +(radio * Math.sin(rad)).toFixed(3), y: +(-radio * Math.cos(rad)).toFixed(3) };
    };
    return this.premios.map((p, i) => {
      const inicio = punto(i * angulo, 100);
      const fin = punto((i + 1) * angulo, 100);
      const medio = i * angulo + angulo / 2;
      const texto = punto(medio, 62);
      return {
        ...p,
        path: `M 0 0 L ${inicio.x} ${inicio.y} A 100 100 0 0 1 ${fin.x} ${fin.y} Z`,
        textoX: texto.x,
        textoY: texto.y,
        textoRotacion: medio
      };
    });
  }
}
