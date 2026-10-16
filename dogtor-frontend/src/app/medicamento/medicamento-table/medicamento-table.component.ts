import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MedicamentoCL } from 'src/app/model/medicamento-cl';
import { MedicamentoService } from 'src/app/service/medicamento.service';
import * as XLSX from 'xlsx';
import * as FileSaver from 'file-saver';
import { InventarioAlertasComponent } from '../inventario-alertas/inventario-alertas.component';
import { MedicamentoInventario, mensajeError } from 'src/app/model/dogtor';


@Component({
  selector: 'app-medicamento-table',
  templateUrl: './medicamento-table.component.html',
  styleUrls: ['./medicamento-table.component.css']
})
export class MedicamentoTableComponent implements OnInit {
  medicamentos: MedicamentoCL[] = [];
  nombreMedicamentoBuscar: string = '';
  opcionOrdenamiento: string = 'id'; // Filtro predeterminado por ID

  // Dogtor: entradas de inventario
  @ViewChild('alertas') alertas?: InventarioAlertasComponent;
  entrada: MedicamentoInventario | MedicamentoCL | null = null;
  formEntrada = { cantidad: 0, lote: '', fechaVencimiento: '', stockMinimo: 10, categoria: 'MEDICAMENTO' };
  guardandoEntrada = false;
  errorEntrada = '';

  constructor(
    private medicamentoService: MedicamentoService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cargarMedicamentos();
  }

  cargarMedicamentos(): void {
    this.medicamentoService.findAll().subscribe({
      next: (medicamentos: MedicamentoCL[]) => {
        this.medicamentos = medicamentos;
        this.ordenarMedicamentos();  // Ordenar al cargar
      },
      error: (err) => {
        console.error('Error al cargar los medicamentos', err);
      }
    });
  }

  buscarMedicamento(): void {
    const nombre = this.nombreMedicamentoBuscar.trim();
    if (nombre === '') {
      this.cargarMedicamentos(); // Si no hay nombre, recarga todos los medicamentos
    } else {
      this.medicamentoService.buscarPorNombre(nombre).subscribe({
        next: (resultados: MedicamentoCL[]) => {
          this.medicamentos = resultados;
          this.ordenarMedicamentos(); // Ordenar los resultados
        },
        error: (err) => {
          console.error('Error al buscar medicamentos', err);
        }
      });
    }
  }

  ordenarMedicamentos(): void {
    switch (this.opcionOrdenamiento) {
      case 'id':
        this.medicamentos.sort((a, b) => a.idMedicamento - b.idMedicamento);
        break;
      case 'precioCompraAsc':
        this.medicamentos.sort((a, b) => a.precioCompra - b.precioCompra);
        break;
      case 'precioCompraDesc':
        this.medicamentos.sort((a, b) => b.precioCompra - a.precioCompra);
        break;
      case 'precioVentaAsc':
        this.medicamentos.sort((a, b) => a.precioVenta - b.precioVenta);
        break;
      case 'precioVentaDesc':
        this.medicamentos.sort((a, b) => b.precioVenta - a.precioVenta);
        break;
      default:
        break;
    }
  }


    exportarExcel(): void {
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.medicamentos);
    const workbook: XLSX.WorkBook = { Sheets: { 'Medicamentos': worksheet }, SheetNames: ['Medicamentos'] };
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });

    const data: Blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
    FileSaver.saveAs(data, 'medicamentos.xlsx');
  }

  // ---------- Dogtor: inventario ----------

  bajoStock(m: MedicamentoCL): boolean {
    return m.stockMinimo != null && m.unidadesDisponibles < m.stockMinimo;
  }

  estadoVencimiento(m: MedicamentoCL): 'vencido' | 'porVencer' | 'ok' {
    if (!m.fechaVencimiento) {
      return 'ok';
    }
    const dias = (new Date(m.fechaVencimiento).getTime() - Date.now()) / 86400000;
    return dias < 0 ? 'vencido' : dias <= 60 ? 'porVencer' : 'ok';
  }

  abrirEntrada(m: MedicamentoInventario | MedicamentoCL): void {
    this.entrada = m;
    this.errorEntrada = '';
    this.formEntrada = {
      cantidad: 0,
      lote: m.lote || '',
      fechaVencimiento: m.fechaVencimiento ? m.fechaVencimiento.slice(0, 10) : '',
      stockMinimo: m.stockMinimo ?? 10,
      categoria: m.categoria || 'MEDICAMENTO'
    };
  }

  guardarEntrada(): void {
    if (!this.entrada) {
      return;
    }
    if (this.formEntrada.cantidad < 0 || this.formEntrada.stockMinimo < 0) {
      this.errorEntrada = 'Las cantidades no pueden ser negativas.';
      return;
    }
    this.guardandoEntrada = true;
    const datos: Partial<MedicamentoInventario> = {
      lote: this.formEntrada.lote,
      stockMinimo: this.formEntrada.stockMinimo,
      categoria: this.formEntrada.categoria,
      fechaVencimiento: this.formEntrada.fechaVencimiento || null
    };
    this.medicamentoService.actualizarInventario(this.entrada.idMedicamento, datos, this.formEntrada.cantidad || 0).subscribe({
      next: () => {
        this.guardandoEntrada = false;
        this.entrada = null;
        this.cargarMedicamentos();
        this.alertas?.cargar();
      },
      error: (err) => {
        this.guardandoEntrada = false;
        this.errorEntrada = mensajeError(err, 'No se pudo registrar la entrada.');
      }
    });
  }
}
