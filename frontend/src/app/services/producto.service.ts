import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Producto } from '../models/producto.model';
import { ApiResponse } from '../models/medicion.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private readonly apiUrl = `${environment.apiBaseUrl}/productos`;

  constructor(private readonly http: HttpClient) {}

  getBySucursal(sucursalId: string): Observable<ApiResponse<Producto[]>> {
    const params = new HttpParams().set('sucursal', sucursalId);
    return this.http.get<ApiResponse<Producto[]>>(this.apiUrl, { params });
  }

  buscar(params: { sucursalId: string; nombre?: string; barcode?: string; categoria?: string; fechaInicio?: string; fechaFin?: string }): Observable<ApiResponse<Producto[]>> {
    let httpParams = new HttpParams().set('sucursal', params.sucursalId);
    if (params.nombre) httpParams = httpParams.set('nombre', params.nombre);
    if (params.barcode) httpParams = httpParams.set('barcode', params.barcode);
    if (params.categoria) httpParams = httpParams.set('categoria', params.categoria);
    if (params.fechaInicio) httpParams = httpParams.set('fechaInicio', params.fechaInicio);
    if (params.fechaFin) httpParams = httpParams.set('fechaFin', params.fechaFin);

    return this.http.get<ApiResponse<Producto[]>>(`${this.apiUrl}/buscar`, { params: httpParams });
  }

  create(producto: Producto): Observable<ApiResponse<Producto>> {
    return this.http.post<ApiResponse<Producto>>(this.apiUrl, producto);
  }

  delete(nombre: string, sucursalId: string): Observable<ApiResponse<string>> {
    const params = new HttpParams().set('sucursal', sucursalId);
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/${nombre}`, { params });
  }
}