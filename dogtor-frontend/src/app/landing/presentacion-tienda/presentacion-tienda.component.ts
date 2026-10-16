import { Router } from '@angular/router';
import { Component } from '@angular/core';

@Component({
  selector: 'app-presentacion-tienda',
  templateUrl: './presentacion-tienda.component.html',
  styleUrls: ['./presentacion-tienda.component.css']
})
export class PresentacionTiendaComponent {

  constructor(private router: Router) {}

  goToProductos() {
    this.router.navigate(['/productos']);
  }

  sedes = [
    {
      name: 'Los Pinos',
      image: 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=600',
      description: 'Rodeada de naturaleza, perfecta para el bienestar de tus mascotas.',
      address: 'Calle 123 #45-67, Bogotá',
    },
    {
      name: 'Patitas Felices',
      image: 'https://www.paxinasgalegas.es/imagenes/adsg-acivo_img552467t0.jpg',
      description: 'Un lugar lleno de amor, juegos y atención personalizada.',
      address: 'Avenida 7 #89-01, Bogotá',
    },
    {
      name: 'Huellas',
      image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSV9gHpgK6rA-4QK_7jf62obGHv1HHgJjwBOg&s',
      description: 'Instalaciones modernas y cálidas para una atención de calidad.',
      address: 'Carrera 5 #23-45, Bogotá',
    }
  ];
}
