import { Component, OnInit } from '@angular/core';
import { AdopcionService } from 'src/app/service/adopcion.service';
import { MascotaAdopcion, edadLegible } from 'src/app/model/dogtor';

@Component({
  selector: 'app-adopcion-destacada',
  templateUrl: './adopcion-destacada.component.html',
  styleUrls: ['./adopcion-destacada.component.css']
})
export class AdopcionDestacadaComponent implements OnInit {
  mascotas: MascotaAdopcion[] = [];
  total = 0;
  edadLegible = edadLegible;

  constructor(private adopcionService: AdopcionService) {}

  ngOnInit(): void {
    this.adopcionService.catalogo().subscribe({
      next: (mascotas) => {
        this.total = mascotas.length;
        this.mascotas = mascotas.filter(m => m.estado === 'DISPONIBLE').slice(0, 4);
      },
      error: () => this.mascotas = []
    });
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'assets/mascota-placeholder.svg';
  }
}
