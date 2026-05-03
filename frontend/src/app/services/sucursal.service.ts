import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Sucursal } from '../models/sucursal.model';
import { ApiResponse } from '../models/medicion.model';

@Injectable({
  providedIn: 'root'
})
export class SucursalService {
  private readonly apiUrl = `${environment.apiBaseUrl}/sucursales`;
  private readonly uploadUrl = `${environment.apiBaseUrl}/products/upload`;

  constructor(private readonly http: HttpClient) {}

  uploadCsv(file: File, csvType: 'sucursales' | 'conexiones' | 'catalogo'): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('csvType', csvType);
    formData.append('hasHeader', 'true');
    return this.http.post(this.uploadUrl, formData);
  }

  getAll(): Observable<ApiResponse<Sucursal[]>> {
    return this.http.get<ApiResponse<Sucursal[]>>(this.apiUrl);
  }

  getById(id: string): Observable<ApiResponse<Sucursal>> {
    return this.http.get<ApiResponse<Sucursal>>(`${this.apiUrl}/${id}`);
  }

  create(sucursal: Sucursal): Observable<ApiResponse<Sucursal>> {
    return this.http.post<ApiResponse<Sucursal>>(this.apiUrl, sucursal);
  }

  update(id: string, sucursal: Sucursal): Observable<ApiResponse<Sucursal>> {
    return this.http.put<ApiResponse<Sucursal>>(`${this.apiUrl}/${id}`, sucursal);
  }

  delete(id: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/${id}`);
  }
}