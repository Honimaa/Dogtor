import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MedicamentoCL } from '../model/medicamento-cl';
import { AlertasInventario, MedicamentoInventario } from '../model/dogtor';

@Injectable({
  providedIn: 'root'
})
export class MedicamentoService {
  private baseUrl = 'http://localhost:8082/medicamento';
  constructor(private http: HttpClient) { }

  findAll(): Observable<MedicamentoCL[]> {
    return this.http.get<MedicamentoCL[]>(`${this.baseUrl}`);
  }

  getMedicamento(id: number): Observable<MedicamentoCL> {
    return this.http.get<MedicamentoCL>(`${this.baseUrl}/${id}`);
  }

  buscarPorNombre(nombre: string): Observable<MedicamentoCL[]> {
    return this.http.get<MedicamentoCL[]>(`${this.baseUrl}/buscar`, {
      params: { nombre }
    });
  }

  getPorTratamiento(id:number): Observable<MedicamentoCL[]>{
    return this.http.get<MedicamentoCL[]>(`${this.baseUrl}/por-tratamiento/${id}`);
  }

  // Dogtor: inventario
  listarInventario(): Observable<MedicamentoInventario[]> {
    return this.http.get<MedicamentoInventario[]>(`${this.baseUrl}`);
  }

  obtenerAlertas(dias: number = 60): Observable<AlertasInventario> {
    return this.http.get<AlertasInventario>(`${this.baseUrl}/alertas`, { params: { dias } });
  }

  actualizarInventario(id: number, datos: Partial<MedicamentoInventario>, entrada: number = 0): Observable<MedicamentoInventario> {
    return this.http.put<MedicamentoInventario>(`${this.baseUrl}/${id}/inventario`, datos, { params: { entrada } });
  }
}
