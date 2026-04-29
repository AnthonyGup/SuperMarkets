import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout } from 'rxjs';
import { environment } from '../../environments/environment';

export type CsvType = 'sucursales' | 'conexiones' | 'catalogo';

export interface CsvUploadResponse {
  success: boolean;
  message: string;
  recordsLoaded?: number;
  totalErrors?: number;
  errors?: string[];
  stats?: {
    totalLineas: number;
    registrosExitosos: number;
    erroresLinea: number;
    erroresDuplicados: number;
    erroresFecha: number;
    erroresNumeros: number;
    erroresOtros: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class CsvUploadService {
  private readonly uploadUrl = `${environment.apiBaseUrl}/products/upload`;

  constructor(private readonly http: HttpClient) {}

  upload(file: File, csvType: CsvType, hasHeader: boolean): Observable<CsvUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('csvType', csvType);
    formData.append('hasHeader', String(hasHeader));

    return this.http
      .post<CsvUploadResponse>(this.uploadUrl, formData)
      .pipe(timeout(15000));
  }
}