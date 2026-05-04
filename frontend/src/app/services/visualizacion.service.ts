import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/medicion.model';
import { VisualizacionState } from '../models/visualizacion.model';
import { TransferenciaActiva, TransferenciaHistorial } from '../models/transferencia.model';

@Injectable({
  providedIn: 'root'
})
export class VisualizacionService {
  private readonly apiUrl = `${environment.apiBaseUrl}/visualizacion`;

  constructor(private readonly http: HttpClient) {}

  getEstado(): Observable<ApiResponse<VisualizacionState>> {
    return this.http.get<ApiResponse<VisualizacionState>>(`${this.apiUrl}/estado`);
  }

  getHistorialTransferencias(): Observable<ApiResponse<TransferenciaHistorial[]>> {
    return this.http.get<ApiResponse<TransferenciaHistorial[]>>(`${environment.apiBaseUrl}/transferencia/historial`);
  }

  getDotEstructura(tipo: string, sucursalId: string): Observable<any> {
    return this.http.get<any>(`${environment.apiBaseUrl}/estructuras/${tipo}?sucursal=${sucursalId}`);
  }
}
