import { Component } from '@angular/core';

@Component({
  selector: 'app-preguntas',
  templateUrl: './preguntas.component.html',
  styleUrls: ['./preguntas.component.css']
})
export class PreguntasComponent {
  // Creamos un arreglo de preguntas con respuestas y un estado de apertura (true o false)
  faqs = [
    {
      question: "¿Cómo agendo una cita?",
      answer: "Inicia sesión, entra a «Agendar cita» y en tres pasos eliges a tu mascota, el doctor y uno de sus horarios disponibles. Solo verás espacios libres, así que nunca habrá cruces de agenda.",
      open: false
    },
    {
      question: "¿Qué hago si mi mascota tiene una urgencia?",
      answer: "En la sección «Urgencias 24h» verás en tiempo real qué sedes tienen un doctor de turno. También puedes llamar a nuestra línea 24/7: (+57) 315 446 7811.",
      open: false
    },
    {
      question: "¿Puedo ver la historia clínica de mi mascota?",
      answer: "Sí. Desde «Mis mascotas» puedes consultar sus vacunas, diagnósticos, tratamientos, cirugías y alergias, y descargar un resumen en PDF. Solo los doctores autorizados pueden modificarla.",
      open: false
    },
    {
      question: "¿Cómo funciona la adopción?",
      answer: "Los refugios y fundaciones validados por Dogtor publican a sus mascotas. Eliges la que te enamore, envías tu solicitud sin necesidad de registrarte y el refugio te contacta para continuar el proceso.",
      open: false
    },
    {
      question: "¿Me recordarán las vacunas y controles?",
      answer: "Sí. Dogtor te avisa de tus citas 24 horas antes y de las vacunas o controles próximos según el plan definido por el doctor.",
      open: false
    }
  ];

  // Método para manejar el estado de apertura/cierre de la respuesta
  toggleAnswer(index: number) {
    this.faqs[index].open = !this.faqs[index].open;
  }
}
