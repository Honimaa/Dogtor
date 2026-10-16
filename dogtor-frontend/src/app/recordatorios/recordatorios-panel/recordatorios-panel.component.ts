import { Component, Input, OnChanges } from '@angular/core';
import { Observable } from 'rxjs';
import { RecordatorioService } from 'src/app/service/recordatorio.service';
import { Recordatorio } from 'src/app/model/dogtor';

/**
 * Panel de recordatorios de citas, vacunas y controles (REQ-REC-01..03).
 * Con idCliente muestra los del propietario; sin él, los de toda la clínica.
 */
@Component({
  selector: 'app-recordatorios-panel',
  templateUrl: './recordatorios-panel.component.html',
  styleUrls: ['./recordatorios-panel.component.css']
})
export class RecordatoriosPanelComponent implements OnChanges {
  @Input() idCliente: number | null = null;
  @Input() titulo = 'Recordatorios';
  @Input() limite = 6;

  recordatorios: Recordatorio[] = [];
  cargando = true;
  verTodos = false;

  constructor(private recordatorioService: RecordatorioService) {}

  ngOnChanges(): void {
    this.cargando = true;
    const fuente: Observable<Recordatorio[]> = this.idCliente
      ? this.recordatorioService.porCliente(this.idCliente)
      : this.recordatorioService.deLaClinica();
    fuente.subscribe({
      next: (r) => {
        this.recordatorios = r;
        this.cargando = false;
      },
      error: () => {
        this.recordatorios = [];
        this.cargando = false;
      }
    });
  }

  get visibles(): Recordatorio[] {
    return this.verTodos ? this.recordatorios : this.recordatorios.slice(0, this.limite);
  }

  get urgentes(): number {
    return this.recordatorios.filter(r => r.urgente).length;
  }

  icono(tipo: string): string {
    switch (tipo) {
      case 'CITA': return 'calendar-outline';
      case 'VACUNA': return 'shield-checkmark-outline';
      default: return 'repeat-outline';
    }
  }
}
