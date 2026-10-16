import { Component } from '@angular/core';

interface RazaPeso {
  nombre: string;
  min: number;
  max: number;
}

@Component({
  selector: 'app-calculadora-peso',
  templateUrl: './calculadora-peso.component.html',
  styleUrls: ['./calculadora-peso.component.css']
})
export class CalculadoraPesoComponent {
  pesoActual: number | null = null;
  especie: 'perro' | 'gato' | '' = '';
  raza = '';
  mensaje = '';
  detalle = '';
  resultado: 'bajo' | 'ideal' | 'alto' | '' = '';
  rango: RazaPeso | null = null;

  // Rangos de peso adulto aproximados (kg). Varían según sexo, edad y contextura de cada animal.
  razasPerro: RazaPeso[] = [
    { nombre: 'Chihuahua', min: 1.5, max: 3 },
    { nombre: 'Yorkshire terrier', min: 2, max: 3.2 },
    { nombre: 'Pomerania', min: 1.8, max: 3.5 },
    { nombre: 'Poodle toy', min: 2, max: 4 },
    { nombre: 'Shih Tzu', min: 4, max: 7.5 },
    { nombre: 'Pug', min: 6, max: 8 },
    { nombre: 'Schnauzer miniatura', min: 5, max: 9 },
    { nombre: 'Shiba Inu', min: 8, max: 11 },
    { nombre: 'Bulldog francés', min: 8, max: 14 },
    { nombre: 'Beagle', min: 9, max: 11 },
    { nombre: 'Cocker spaniel', min: 12, max: 15 },
    { nombre: 'Border collie', min: 14, max: 20 },
    { nombre: 'Husky siberiano', min: 16, max: 27 },
    { nombre: 'Bulldog inglés', min: 18, max: 25 },
    { nombre: 'Dálmata', min: 20, max: 32 },
    { nombre: 'Poodle estándar', min: 20, max: 32 },
    { nombre: 'Pastor belga malinois', min: 20, max: 30 },
    { nombre: 'Pastor alemán', min: 22, max: 40 },
    { nombre: 'Boxer', min: 25, max: 32 },
    { nombre: 'Golden retriever', min: 25, max: 34 },
    { nombre: 'Labrador retriever', min: 25, max: 36 },
    { nombre: 'Rottweiler', min: 35, max: 60 },
    { nombre: 'Criollo / mestizo pequeño', min: 3, max: 10 },
    { nombre: 'Criollo / mestizo mediano', min: 10, max: 25 },
    { nombre: 'Criollo / mestizo grande', min: 25, max: 45 }
  ];

  razasGato: RazaPeso[] = [
    { nombre: 'Doméstico / criollo', min: 3.5, max: 5.5 },
    { nombre: 'Siamés', min: 2.5, max: 5.5 },
    { nombre: 'Snowshoe', min: 3, max: 5.5 },
    { nombre: 'Angora turco', min: 2.5, max: 5 },
    { nombre: 'Esfinge', min: 3, max: 5 },
    { nombre: 'Persa', min: 3, max: 6 },
    { nombre: 'Bengalí', min: 3.5, max: 7 },
    { nombre: 'British shorthair', min: 4, max: 8 },
    { nombre: 'Ragdoll', min: 4.5, max: 9 },
    { nombre: 'Maine Coon', min: 5.5, max: 11 }
  ];

  get razas(): RazaPeso[] {
    return this.especie === 'perro' ? this.razasPerro : this.especie === 'gato' ? this.razasGato : [];
  }

  cambiarEspecie(): void {
    this.raza = '';
    this.limpiarResultado();
  }

  calcularPesoIdeal(): void {
    this.limpiarResultado();
    const rango = this.razas.find(r => r.nombre === this.raza);
    if (!this.especie || !rango) {
      this.mensaje = 'Selecciona la especie y la raza de tu mascota.';
      return;
    }
    if (!this.pesoActual || this.pesoActual <= 0) {
      this.mensaje = 'Ingresa el peso actual de tu mascota en kilogramos.';
      return;
    }

    this.rango = rango;
    const peso = this.pesoActual;
    if (peso < rango.min) {
      this.resultado = 'bajo';
      this.mensaje = 'Tu mascota está por debajo del peso ideal.';
      this.detalle = `Le faltan aproximadamente ${this.formato(rango.min - peso)} kg para llegar al rango de un ${rango.nombre} adulto.`;
    } else if (peso > rango.max) {
      this.resultado = 'alto';
      this.mensaje = 'Tu mascota tiene sobrepeso.';
      this.detalle = `Supera en aproximadamente ${this.formato(peso - rango.max)} kg el rango de un ${rango.nombre} adulto.`;
    } else {
      this.resultado = 'ideal';
      this.mensaje = '¡Tu mascota está en su peso ideal!';
      this.detalle = `Está dentro del rango esperado para un ${rango.nombre} adulto.`;
    }
  }

  icono(): string {
    return this.resultado === 'ideal' ? 'checkmark-circle' : this.resultado === 'bajo' ? 'trending-down' : 'trending-up';
  }

  private limpiarResultado(): void {
    this.mensaje = '';
    this.detalle = '';
    this.resultado = '';
    this.rango = null;
  }

  private formato(valor: number): string {
    return valor.toLocaleString('es-CO', { maximumFractionDigits: 1 });
  }
}
