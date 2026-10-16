import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SedeUrgencias } from '../model/dogtor';

@Injectable({
  providedIn: 'root'
})
export class UrgenciasService {
  private baseUrl = 'http://localhost:8082/veterinario';
  constructor(private http: HttpClient) { }

  obtenerSedes(): Observable<SedeUrgencias[]> {
    return this.http.get<SedeUrgencias[]>(`${this.baseUrl}/urgencias`);
  }
}
