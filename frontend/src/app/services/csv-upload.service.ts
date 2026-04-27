import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, timeout } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CsvUploadResponse {
  success: boolean;
  message: string;
  productsLoaded?: number;
  totalErrors?: number;
  errors?: string[];
  stats?: {
    totalLineas: number;
    productosExitosos: number;
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

  upload(file: File, hasHeader: boolean): Observable<CsvUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('hasHeader', String(hasHeader));

    return this.http
      .post<CsvUploadResponse>(this.uploadUrl, formData)
      .pipe(timeout(15000));
  }
}
