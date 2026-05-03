import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ResultadoMedicion, ApiResponse } from '../models/medicion.model';

@Injectable({
  providedIn: 'root'
})
export class MedicionService {
  private readonly apiUrl = `${environment.apiBaseUrl}/medicion`;

  constructor(private readonly http: HttpClient) {}

  medirBusqueda(sucursalId: string, tipo: string, nombre: string): Observable<ApiResponse<ResultadoMedicion>> {
    const params = new HttpParams()
      .set('sucursal', sucursalId)
      .set('tipo', tipo)
      .set('nombre', nombre);
    return this.http.get<ApiResponse<ResultadoMedicion>>(`${this.apiUrl}/busqueda`, { params });
  }

  comparar(sucursalId: string, operacion: string, nombre?: string): Observable<ApiResponse<ResultadoMedicion[]>> {
    let params = new HttpParams()
      .set('sucursal', sucursalId)
      .set('operacion', operacion);
    if (nombre) params = params.set('nombre', nombre);
    return this.http.get<ApiResponse<ResultadoMedicion[]>>(`${this.apiUrl}/comparar`, { params });
  }
}