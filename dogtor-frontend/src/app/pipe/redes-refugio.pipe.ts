import { Pipe, PipeTransform } from '@angular/core';
import { Refugio, RedSocial, redesDeRefugio } from '../model/dogtor';

/**
 * Enlaces a las redes sociales de un refugio.
 * Es un pipe puro: solo se recalcula cuando cambia el refugio. Llamar a una función desde la plantilla
 * creaba enlaces nuevos en cada detección de cambios y los íconos se redibujaban sin parar (la página se congelaba).
 */
@Pipe({ name: 'redesRefugio' })
export class RedesRefugioPipe implements PipeTransform {
  transform(refugio: Refugio | null | undefined): RedSocial[] {
    return redesDeRefugio(refugio);
  }
}
