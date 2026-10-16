import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-blog',
  templateUrl: './blog.component.html',
  styleUrls: ['./blog.component.css']
})
export class BlogComponent {
  articulos = [
    {
      id: 'Spotify',
      titulo: 'PlayList para relajar a tu mascota',
      descripcion: `La música también es parte del bienestar de tu mascota. Descubre una playlist pensada especialmente para relajar y estimular a tu perro o gato. ¡Haz clic y escúchala ahora!`,
      fecha: new Date(2026, 7, 20),
      autor: 'Dra. Angela Beltrán',
      imagenes: [
        'https://www.aon.es/personales/seguro-perro-gato/wp-content/uploads/sites/2/2023/06/afecta_la_musica_a_las_mascotas.png',
        'https://www.zooplus.es/magazine/wp-content/uploads/2021/03/ejercicio-para-perros.jpeg'
      ],
      ruta: '/playlist'
    },
    {
      id: 'test-salud-mascota',
      titulo: 'Evalúa el estado de salud de tu mascota en 1 minuto',
      descripcion: `¿Notas que tu mascota no se comporta como siempre? Con nuestro test rápido podrás detectar señales comunes de alerta como fatiga, pérdida de apetito o síntomas respiratorios.

Este test no reemplaza una consulta veterinaria, pero puede ayudarte a decidir cuándo es momento de actuar. ¡Hazlo ahora y cuida a quien más te quiere!`,
      fecha: new Date(2026, 4, 1),
      autor: 'Dra. Lina Rodríguez',
      imagenes: [
        'https://cdn.nubika.es/wp-content/uploads/2024/02/14153319/test-inteligencia-perros.png',
        'https://cdn.nubika.es/wp-content/uploads/2024/02/14153319/test-inteligencia-perros.png'
      ],
      ruta: '/test-salud-mascota'
    },
    {
      id: 'edad-humana-mascota',
      titulo: 'Conoce la edad humana de tu mascota',
      descripcion: `
      ¿Sabías que la edad de tu mascota no se calcula igual que la humana? 
      Aquí te ayudamos a conocer cuántos años "humanos" tiene tu perro o gato.
    `,
      fecha: new Date(2026, 1, 26),
      autor: 'Dr. Julio Bermúdez',
      imagenes: [
        'https://urgenciesveterinaries.com/wp-content/uploads/2021/03/perros-braquicefalos-survet-01.jpg'
      ],
      ruta: '/edad-humana-mascota',
      incluyeCalculadoraEdad: true
    },
    {
      id: 'cumpleanios-mascota',
      titulo: '¡Tu mascota está de cumpleaños!',
      descripcion: `
        ¿Hoy es el cumpleaños de tu peludo? 🎉 
        Descubre las sorpresas que Dogtor tiene preparadas para celebrar su día especial. 
        Desde premios hasta recomendaciones personalizadas.
      `,
      fecha: new Date(2026, 2, 30),
      autor: 'Dra. Ana Gómez',
      imagenes: [
        'https://www.wasky.es/wp-content/uploads/cabecera-60.jpg'
      ],
      ruta: '/cumpleanios-mascota',
      incluyeSorpresasCumple: true
    },
    {
      id: 'generador-nombres',
      titulo: '¿Buscas el nombre perfecto para tu mascota?',
      descripcion: `
        ¡Estás en el lugar correcto! 🐾
        Nuestro generador de nombres te ayudará a encontrar el nombre ideal para tu nuevo compañero peludo. 
        Solo dinos si es macho o hembra y te daremos opciones adorables, originales y llenas de personalidad.
      `,
      fecha: new Date(2026, 3, 23),
      autor: 'Dr. Fernando Higuera',
      imagenes: [
        'https://www.zooplus.es/magazine/wp-content/uploads/2019/02/feat-768x496.jpeg'
      ],
      ruta: '/generador-nombres',
      incluyeGeneradorNombres: true
    },
    {
    id: 'calculadora-peso',
    titulo: '¿Tu mascota está en su peso ideal?',
    descripcion: `
      ¡Descúbrelo ahora! ⚖️🐶🐱
      Con nuestra calculadora de peso ideal puedes saber si tu perro o gato está en un rango saludable de peso.
      Solo ingresa su edad, raza, especie y peso actual, y obtendrás una recomendación útil y fácil de entender.
      ¡Cuida su salud con solo unos clics!
    `,
    fecha: new Date(2026, 4, 24),
    autor: 'Dra. Camila Suárez',
    imagenes: [
      'https://imagenes.eltiempo.com/files/image_1200_600/uploads/2022/03/28/62420b2966e43.jpeg'
    ],
    ruta: '/calculadora-peso',
    incluyeCalculadoraPeso: true
  },
  {
    id: 'publicacion',
    titulo:'¿Perdiste o encontraste una mascota?',
    descripcion: `
      ¡Descúbrelo ahora! ⚖️🐶🐱
      Con nuestra calculadora de peso ideal puedes saber si tu perro o gato está en un rango saludable de peso.
      Solo ingresa su edad, raza, especie y peso actual, y obtendrás una recomendación útil y fácil de entender.
      ¡Cuida su salud con solo unos clics!
    `,
    fecha: new Date(2026, 2, 26),
    autor: 'Equipo Dogtor',
    imagenes: [
      'https://media.istockphoto.com/id/1346490163/es/foto/divertido-perro-beagle-mira-atentamente-en-una-lupa.jpg?s=612x612&w=0&k=20&c=wHDUJOoEU_ZjgDMAY9eruZlS8_rF7uTZQ6oxtbK0fF8='
    ],
    ruta: '/publicacion',
    incluyeCalculadoraPeso: true
    },
      {
      id: 'publicacion',
      titulo:'Tienda Dogtor',
      descripcion: `
        ¡Descúbrelo ahora! ⚖️🐶🐱
        Con nuestra calculadora de peso ideal puedes saber si tu perro o gato está en un rango saludable de peso.
        Solo ingresa su edad, raza, especie y peso actual, y obtendrás una recomendación útil y fácil de entender.
        ¡Cuida su salud con solo unos clics!
      `,
      fecha: new Date(2026, 9, 8),
      autor: 'Equipo Dogtor',
      imagenes: [
        'https://st5.depositphotos.com/1594920/62593/i/450/depositphotos_625930692-stock-photo-cat-dog-looking-camera-front.jpg'
      ],
      ruta: '/productos',
      incluyeCalculadoraPeso: true
    }



  ].sort((a, b) => b.fecha.getTime() - a.fecha.getTime()); // Orden descendente por fecha

  constructor(private router: Router) {}

  verArticulo(id: string) {
    this.router.navigate(['/articulo', id]);
  }

  navegar(ruta: string) {
    this.router.navigate([ruta]);
  }
}
