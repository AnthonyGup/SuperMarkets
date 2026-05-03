import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/medicion.model';
import { VisualizacionState, VisualizacionSucursal, ProductoTransito } from '../models/visualizacion.model';

@Injectable({
  providedIn: 'root'
})
export class VisualizacionService {
  private readonly apiUrl = `${environment.apiBaseUrl}/visualizacion`;

  constructor(private readonly http: HttpClient) {}

  getEstado(): Observable<ApiResponse<VisualizacionState>> {
    return this.http.get<ApiResponse<VisualizacionState>>(`${this.apiUrl}/estado`);
  }

  getSucursalEstado(sucursalId: string): Observable<ApiResponse<VisualizacionSucursal>> {
    return this.http.get<ApiResponse<VisualizacionSucursal>>(`${this.apiUrl}/sucursal/${sucursalId}`);
  }
}
