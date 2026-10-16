import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MedicamentoService } from 'src/app/service/medicamento.service';
import { AlertasInventario, MedicamentoInventario } from 'src/app/model/dogtor';

/** Alertas de bajo stock, próximos a vencer y vencidos (REQ-INV-01, 02, 07). */
@Component({
  selector: 'app-inventario-alertas',
  templateUrl: './inventario-alertas.component.html',
  styleUrls: ['./inventario-alertas.component.css']
})
export class InventarioAlertasComponent implements OnInit {
  @Input() compacto = false;
  @Input() dias = 60;
  @Output() seleccionar = new EventEmitter<MedicamentoInventario>();

  alertas: AlertasInventario = { bajoStock: [], porVencer: [], vencidos: [] };
  pestana: keyof AlertasInventario = 'bajoStock';
  cargando = true;

  constructor(private medicamentoService: MedicamentoService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.medicamentoService.obtenerAlertas(this.dias).subscribe({
      next: (a) => {
        this.alertas = a;
        this.cargando = false;
        if (!a.bajoStock.length) {
          this.pestana = a.vencidos.length ? 'vencidos' : 'porVencer';
        }
      },
      error: () => this.cargando = false
    });
  }

  get total(): number {
    return this.alertas.bajoStock.length + this.alertas.porVencer.length + this.alertas.vencidos.length;
  }

  get lista(): MedicamentoInventario[] {
    const lista = this.alertas[this.pestana];
    return this.compacto ? lista.slice(0, 5) : lista;
  }

  diasPara(fecha?: string | null): number {
    if (!fecha) {
      return 0;
    }
    return Math.ceil((new Date(fecha).getTime() - Date.now()) / 86400000);
  }
}
