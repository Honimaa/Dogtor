import { Component, OnDestroy, OnInit } from '@angular/core';
import { UrgenciasService } from 'src/app/service/urgencias.service';
import { SedeUrgencias } from 'src/app/model/dogtor';

@Component({
  selector: 'app-urgencias-page',
  templateUrl: './urgencias-page.component.html',
  styleUrls: ['./urgencias-page.component.css']
})
export class UrgenciasPageComponent implements OnInit, OnDestroy {
  sedes: SedeUrgencias[] = [];
  cargando = true;
  error = false;
  actualizado = new Date();

  // Direcciones de las sedes (fase posterior al MVP: integración con servicio de mapas)
  direcciones: Record<string, string> = {
    'Los Pinos': 'Calle 123 #45-67, Bogotá',
    'Patitas Felices': 'Avenida 7 #89-01, Bogotá',
    'Huellas': 'Carrera 5 #23-45, Bogotá'
  };

  private intervalo: any;

  constructor(private urgenciasService: UrgenciasService) {}

  ngOnInit(): void {
    this.cargar();
    // Disponibilidad en tiempo real: refresco automático cada minuto
    this.intervalo = setInterval(() => this.cargar(), 60000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalo);
  }

  cargar(): void {
    this.urgenciasService.obtenerSedes().subscribe({
      next: (sedes) => {
        this.sedes = sedes;
        this.cargando = false;
        this.error = false;
        this.actualizado = new Date();
      },
      error: () => {
        this.cargando = false;
        this.error = true;
      }
    });
  }

  mapa(sede: string): string {
    const direccion = this.direcciones[sede] || `${sede} Bogotá`;
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent('Veterinaria ' + direccion)}`;
  }
}
