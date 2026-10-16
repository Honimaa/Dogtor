import { Component } from '@angular/core';

@Component({
  selector: 'app-quienes-somos',
  templateUrl: './quienes-somos.component.html',
  styleUrls: ['./quienes-somos.component.css']
})
export class QuienesSomosComponent {
  activeTab = 0;

  // Misión y visión del Documento de Formulación del Proyecto Dogtor
  tabs = [
    {
      title: 'Misión',
      content: 'Facilitar la gestión de las clínicas veterinarias y fortalecer los procesos de adopción responsable en Colombia, mediante una plataforma web integral, confiable y fácil de usar que centralice la información clínica, administrativa y de bienestar animal, mejorando la calidad del servicio para doctores, propietarios de mascotas y organizaciones de protección animal.'
    },
    {
      title: 'Visión',
      content: 'En 2030, Dogtor S.A.S. será la plataforma de referencia en gestión veterinaria y adopción de mascotas en Colombia y en la región andina, reconocida por conectar de manera eficiente a clínicas, propietarios y fundaciones de protección animal, y por contribuir de forma medible a la reducción del abandono animal mediante la tecnología.'
    },
    {
      title: 'Valores',
      content: 'Bienestar animal, confianza, cercanía, responsabilidad con los datos de nuestros usuarios y compromiso con la adopción responsable.'
    },
    {
      title: 'Nuestro nombre',
      content: 'Dogtor une "dog" y "doctor": la atención médica profesional con la calidez de quien quiere a su mascota. Nuestro beagle y nuestros colores, azul y dorado, transmiten confianza, cuidado y cercanía.'
    }
  ];
sedes = [
    {
      name: 'Los Pinos',
      image: 'https://images.unsplash.com/photo-1628009368231-7bb7cfcb0def?w=600',
      description: 'Rodeada de naturaleza, perfecta para el bienestar de tus mascotas.',
      address: 'Calle 123 #45-67, Bogotá',
      flipped: false
    },
    {
      name: 'Patitas Felices',
      image: 'https://www.paxinasgalegas.es/imagenes/adsg-acivo_img552467t0.jpg',
      description: 'Un lugar lleno de amor, juegos y atención personalizada.',
      address: 'Avenida 7 #89-01, Bogotá',
      flipped: false
    },
    {
      name: 'Huellas',
      image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSV9gHpgK6rA-4QK_7jf62obGHv1HHgJjwBOg&s',
      description: 'Instalaciones modernas y cálidas para una atención de calidad.',
      address: 'Carrera 5 #23-45, Bogotá',
      flipped: false
    }
  ];

  toggleFlip(sede: any) {
    sede.flipped = !sede.flipped;
  }

  selectTab(index: number) {
    this.activeTab = index;
  }
}