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

  comparar(
    sucursalId: string,
    params: {
      nombre?: string;
      barcode?: string;
      iteraciones?: number;
    } = {}
  ): Observable<ApiResponse<ResultadoMedicion[]>> {
    let httpParams = new HttpParams().set('sucursal', sucursalId);

    if (params.nombre) httpParams = httpParams.set('nombre', params.nombre);
    if (params.barcode) httpParams = httpParams.set('barcode', params.barcode);
    if (params.iteraciones) httpParams = httpParams.set('iteraciones', params.iteraciones.toString());

    return this.http.get<ApiResponse<ResultadoMedicion[]>>(`${this.apiUrl}/comparar`, { params: httpParams });
  }
}