import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type TipoAviso = 'exito' | 'error' | 'info' | 'advertencia';

export interface Aviso {
  id: number;
  tipo: TipoAviso;
  mensaje: string;
}

export interface Confirmacion {
  titulo: string;
  mensaje: string;
  textoConfirmar: string;
  textoCancelar: string;
  peligro: boolean;
  resolver: (respuesta: boolean) => void;
}

/**
 * Reemplaza alert() y confirm() del navegador por mensajes propios con la paleta de Dogtor.
 * Los muestra el componente <app-notificaciones>, incluido una sola vez en app.component.html.
 */
@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private siguienteId = 1;
  readonly avisos$ = new BehaviorSubject<Aviso[]>([]);
  readonly confirmacion$ = new BehaviorSubject<Confirmacion | null>(null);

  exito(mensaje: string): void {
    this.mostrar('exito', mensaje);
  }

  error(mensaje: string): void {
    this.mostrar('error', mensaje, 6000);
  }

  info(mensaje: string): void {
    this.mostrar('info', mensaje);
  }

  advertencia(mensaje: string): void {
    this.mostrar('advertencia', mensaje, 5000);
  }

  cerrar(id: number): void {
    this.avisos$.next(this.avisos$.value.filter(a => a.id !== id));
  }

  /** Pide confirmación y resuelve true si la persona acepta. */
  confirmar(opciones: { titulo?: string; mensaje: string; textoConfirmar?: string; textoCancelar?: string; peligro?: boolean }): Promise<boolean> {
    return new Promise(resolve => {
      this.confirmacion$.next({
        titulo: opciones.titulo ?? '¿Estás seguro?',
        mensaje: opciones.mensaje,
        textoConfirmar: opciones.textoConfirmar ?? 'Confirmar',
        textoCancelar: opciones.textoCancelar ?? 'Cancelar',
        peligro: opciones.peligro ?? false,
        resolver: (respuesta: boolean) => {
          this.confirmacion$.next(null);
          resolve(respuesta);
        }
      });
    });
  }

  private mostrar(tipo: TipoAviso, mensaje: string, duracion = 4000): void {
    const aviso: Aviso = { id: this.siguienteId++, tipo, mensaje };
    this.avisos$.next([...this.avisos$.value, aviso]);
    setTimeout(() => this.cerrar(aviso.id), duracion);
  }
}
