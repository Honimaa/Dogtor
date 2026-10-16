import { Component, HostListener } from '@angular/core';
import { NotificacionService, TipoAviso } from 'src/app/service/notificacion.service';

@Component({
  selector: 'app-notificaciones',
  templateUrl: './notificaciones.component.html',
  styleUrls: ['./notificaciones.component.css']
})
export class NotificacionesComponent {
  constructor(public notificacion: NotificacionService) {}

  icono(tipo: TipoAviso): string {
    switch (tipo) {
      case 'exito': return 'checkmark-circle';
      case 'error': return 'close-circle';
      case 'advertencia': return 'warning';
      default: return 'information-circle';
    }
  }

  @HostListener('document:keydown.escape')
  cancelarConEscape(): void {
    this.notificacion.confirmacion$.value?.resolver(false);
  }
}
