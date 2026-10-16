import { Component, OnDestroy } from '@angular/core';

import { NotificacionService } from 'src/app/service/notificacion.service';
@Component({
  selector: 'app-generador-nombres',
  templateUrl: './generador-nombres.component.html',
  styleUrls: ['./generador-nombres.component.css']
})
export class GeneradorNombresComponent implements OnDestroy {
  constructor(private notificacion: NotificacionService) {}

  generoMascota: 'macho' | 'hembra' | null = null;
  nombreGenerado: string | null = null;

  // La foto es solo para la experiencia: el nombre se elige al azar
  fotoMascota: string | null = null;
  analizando = false;
  pasoAnalisis = '';
  private temporizadores: any[] = [];

  nombresMacho: string[] = ['Max', 'Rocky', 'Toby', 'Simba', 'Thor', 'Coco', 'Leo', 'Duke', 'Bruno', 'Oliver', 'Milo', 'Zeus', 'Firulais', 'Tango', 'Pancho'];
  nombresHembra: string[] = ['Luna', 'Nala', 'Maya', 'Bella', 'Kiara', 'Daisy', 'Lola', 'Canela', 'Mía', 'Frida', 'Kira', 'Chispa', 'Nube', 'Greta', 'Lupe'];

  private pasos = [
    'Analizando la expresión de su carita…',
    'Detectando el color de su pelaje…',
    'Midiendo su nivel de ternura…',
    'Buscando el nombre perfecto…'
  ];

  subirFoto(event: Event): void {
    const archivo = (event.target as HTMLInputElement).files?.[0];
    if (!archivo || !archivo.type.startsWith('image/')) {
      return;
    }
    const lector = new FileReader();
    lector.onload = () => {
      this.fotoMascota = lector.result as string;
      this.nombreGenerado = null;
    };
    lector.readAsDataURL(archivo);
  }

  quitarFoto(): void {
    this.fotoMascota = null;
    this.nombreGenerado = null;
  }

  generarNombre(): void {
    if (!this.generoMascota) {
      this.notificacion.advertencia('Por favor selecciona si tu mascota es macho o hembra.');
      return;
    }

    this.limpiarTemporizadores();
    this.nombreGenerado = null;
    const lista = this.generoMascota === 'macho' ? this.nombresMacho : this.nombresHembra;
    const nombre = lista[Math.floor(Math.random() * lista.length)];

    if (!this.fotoMascota) {
      this.nombreGenerado = nombre;
      return;
    }

    // Con foto: breve "análisis" animado antes de mostrar el nombre
    this.analizando = true;
    this.pasos.forEach((paso, i) => {
      this.temporizadores.push(setTimeout(() => this.pasoAnalisis = paso, i * 650));
    });
    this.temporizadores.push(setTimeout(() => {
      this.analizando = false;
      this.pasoAnalisis = '';
      this.nombreGenerado = nombre;
    }, this.pasos.length * 650 + 300));
  }

  ngOnDestroy(): void {
    this.limpiarTemporizadores();
  }

  private limpiarTemporizadores(): void {
    this.temporizadores.forEach(t => clearTimeout(t));
    this.temporizadores = [];
  }
}
