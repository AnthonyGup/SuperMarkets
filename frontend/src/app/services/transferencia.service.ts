import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TransferenciaRequest, RutaResponse, ColaEstado } from '../models/transferencia.model';
import { ApiResponse } from '../models/medicion.model';

@Injectable({
  providedIn: 'root'
})
export class TransferenciaService {
  private readonly apiUrl = `${environment.apiBaseUrl}/transferencia`;

  constructor(private readonly http: HttpClient) {}

  calcularRuta(origen: string, destino: string, criterio: string): Observable<ApiResponse<RutaResponse>> {
    const params = new HttpParams()
      .set('origen', origen)
      .set('destino', destino)
      .set('criterio', criterio);
    return this.http.get<ApiResponse<RutaResponse>>(`${this.apiUrl}/ruta`, { params });
  }

  getColas(sucursalId: string): Observable<ApiResponse<ColaEstado>> {
    const params = new HttpParams().set('sucursal', sucursalId);
    return this.http.get<ApiResponse<ColaEstado>>(`${this.apiUrl}/cola`, { params });
  }

  transferir(data: TransferenciaRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(this.apiUrl, data);
  }
}