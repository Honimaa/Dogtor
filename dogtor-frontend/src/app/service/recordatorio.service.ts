import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recordatorio } from '../model/dogtor';

@Injectable({
  providedIn: 'root'
})
export class RecordatorioService {
  private baseUrl = 'http://localhost:8082/recordatorios';
  constructor(private http: HttpClient) { }

  porCliente(idCliente: number): Observable<Recordatorio[]> {
    return this.http.get<Recordatorio[]>(`${this.baseUrl}/cliente/${idCliente}`);
  }

  deLaClinica(): Observable<Recordatorio[]> {
    return this.http.get<Recordatorio[]>(`${this.baseUrl}/clinica`);
  }
}
