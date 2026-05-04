import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SucursalService } from '../../services/sucursal.service';

interface CargaResult {
  success: boolean;
  message: string;
  recordsLoaded: number;
  totalErrors: number;
  errors?: string[];
}

@Component({
  selector: 'app-carga-csv',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="carga-csv-page">
      <h1>Carga de Datos CSV</h1>
      <p class="subtitle">Sube los archivos CSV para cargar sucursales, conexiones y catálogo de productos</p>
      
      <div class="orden-carga">
        <p class="orden-title">Orden de carga recomendado:</p>
        <ol>
          <li><strong>Sucursales</strong> - Definir nodos de la red</li>
          <li><strong>Conexiones</strong> - Definir rutas entre sucursales</li>
          <li><strong>Catálogo</strong> - Cargar productos a las sucursales</li>
        </ol>
      </div>

      <div class="upload-sections">
        <div class="upload-card" [class.success]="resultados['sucursales']?.success" [class.error]="resultados['sucursales']?.success === false">
          <div class="card-header">
            <div class="card-icon sucursales">S</div>
            <div class="card-title">
              <h3>Sucursales</h3>
              <span class="card-description">Sucursales de la red</span>
            </div>
          </div>
          
          <div class="card-body">
            <p class="formato-label">Formato esperado ({{ tieneEncabezado['sucursales'] ? 'primera línea = encabezado' : 'sin encabezado' }}):</p>
            <pre class="formato-csv">ID,Nombre,Ubicación,t_ingreso,t_traspaso,t_despacho
SUC001,Zona 1 Guatemala,Ciudad de Guatemala,10,5,15</pre>
            
            <div class="file-upload">
              <label class="file-label">
                <input type="file" (change)="onFileSelected($event, 'sucursales')" accept=".csv">
                <span class="file-name">{{ archivos['sucursales']?.name || 'Seleccionar archivo CSV' }}</span>
              </label>
            </div>

            <div class="header-toggle">
              <label class="checkbox-label">
                <input type="checkbox" [(ngModel)]="tieneEncabezado['sucursales']">
                Primera línea es encabezado
              </label>
            </div>

            <button class="btn-upload" (click)="subirArchivo('sucursales')" [disabled]="!archivos['sucursales'] || cargando['sucursales']">
              {{ cargando['sucursales'] ? 'Cargando...' : 'Cargar Sucursales' }}
            </button>

            <div class="resultado" *ngIf="resultados['sucursales']">
              <div class="resultado-icon" [class.success]="resultados['sucursales'].success" [class.error]="!resultados['sucursales'].success">
                {{ resultados['sucursales'].success ? '✓' : '✗' }}
              </div>
              <div class="resultado-texto">
                <strong>{{ resultados['sucursales'].message }}</strong>
                <p>Registros cargados: {{ resultados['sucursales'].recordsLoaded }}</p>
                <p *ngIf="resultados['sucursales'].totalErrors > 0">Errores: {{ resultados['sucursales'].totalErrors }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="upload-card" [class.success]="resultados['conexiones']?.success" [class.error]="resultados['conexiones']?.success === false">
          <div class="card-header">
            <div class="card-icon conexiones">C</div>
            <div class="card-title">
              <h3>Conexiones</h3>
              <span class="card-description">Rutas entre sucursales</span>
            </div>
          </div>
          
          <div class="card-body">
            <p class="formato-label">Formato esperado ({{ tieneEncabezado['conexiones'] ? 'primera línea = encabezado' : 'sin encabezado' }}):</p>
            <pre class="formato-csv">OrigenID,DestinoID,Tiempo,Costo
SUC001,SUC002,15,50</pre>
            
            <div class="file-upload">
              <label class="file-label">
                <input type="file" (change)="onFileSelected($event, 'conexiones')" accept=".csv">
                <span class="file-name">{{ archivos['conexiones']?.name || 'Seleccionar archivo CSV' }}</span>
              </label>
            </div>

            <div class="header-toggle">
              <label class="checkbox-label">
                <input type="checkbox" [(ngModel)]="tieneEncabezado['conexiones']">
                Primera línea es encabezado
              </label>
            </div>

            <button class="btn-upload" (click)="subirArchivo('conexiones')" [disabled]="!archivos['conexiones'] || cargando['conexiones']">
              {{ cargando['conexiones'] ? 'Cargando...' : 'Cargar Conexiones' }}
            </button>

            <div class="resultado" *ngIf="resultados['conexiones']">
              <div class="resultado-icon" [class.success]="resultados['conexiones'].success" [class.error]="!resultados['conexiones'].success">
                {{ resultados['conexiones'].success ? '✓' : '✗' }}
              </div>
              <div class="resultado-texto">
                <strong>{{ resultados['conexiones'].message }}</strong>
                <p>Registros cargados: {{ resultados['conexiones'].recordsLoaded }}</p>
                <p *ngIf="resultados['conexiones'].totalErrors > 0">Errores: {{ resultados['conexiones'].totalErrors }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="upload-card" [class.success]="resultados['catalogo']?.success" [class.error]="resultados['catalogo']?.success === false">
          <div class="card-header">
            <div class="card-icon catalogo">P</div>
            <div class="card-title">
              <h3>Catálogo de Productos</h3>
              <span class="card-description">Inventario de productos</span>
            </div>
          </div>
          
          <div class="card-body">
            <p class="formato-label">Formato esperado ({{ tieneEncabezado['catalogo'] ? 'primera línea = encabezado' : 'sin encabezado' }}):</p>
            <pre class="formato-csv">SucursalID,Nombre,CodigoBarra,Categoria,FechaCaducidad,Marca,Precio,Stock
SUC001,Leche Pil,7501234567891,Lacteos,2027-06-15,PIL,12.50,100</pre>
            
            <div class="file-upload">
              <label class="file-label">
                <input type="file" (change)="onFileSelected($event, 'catalogo')" accept=".csv">
                <span class="file-name">{{ archivos['catalogo']?.name || 'Seleccionar archivo CSV' }}</span>
              </label>
            </div>

            <div class="header-toggle">
              <label class="checkbox-label">
                <input type="checkbox" [(ngModel)]="tieneEncabezado['catalogo']">
                Primera línea es encabezado
              </label>
            </div>

            <button class="btn-upload" (click)="subirArchivo('catalogo')" [disabled]="!archivos['catalogo'] || cargando['catalogo']">
              {{ cargando['catalogo'] ? 'Cargando...' : 'Cargar Catálogo' }}
            </button>

            <div class="resultado" *ngIf="resultados['catalogo']">
              <div class="resultado-icon" [class.success]="resultados['catalogo'].success" [class.error]="!resultados['catalogo'].success">
                {{ resultados['catalogo'].success ? '✓' : '✗' }}
              </div>
              <div class="resultado-texto">
                <strong>{{ resultados['catalogo'].message }}</strong>
                <p>Registros cargados: {{ resultados['catalogo'].recordsLoaded }}</p>
                <p *ngIf="resultados['catalogo'].totalErrors > 0">Errores: {{ resultados['catalogo'].totalErrors }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .carga-csv-page { max-width: 1200px; margin: 0 auto; }
    h1 { color: #2c3e50; margin-bottom: 8px; }
    .subtitle { color: #7f8c8d; margin-bottom: 24px; }

    .orden-carga { background: #fff3cd; border: 1px solid #ffc107; border-radius: 8px; padding: 16px; margin-bottom: 24px; }
    .orden-title { margin: 0 0 8px; color: #856404; font-weight: 600; }
    .orden-carga ol { margin: 8px 0 0; padding-left: 20px; color: #856404; }
    .orden-carga li { margin-bottom: 4px; }

    .upload-sections { display: grid; grid-template-columns: repeat(auto-fit, minmax(350px, 1fr)); gap: 24px; }

    .upload-card { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: all 0.3s; border: 2px solid transparent; }
    .upload-card.success { border-color: #27ae60; }
    .upload-card.error { border-color: #e74c3c; }

    .card-header { display: flex; align-items: center; gap: 12px; padding: 16px; background: #f8f9fa; border-bottom: 1px solid #eee; }
    .card-icon { width: 48px; height: 48px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 20px; }
    .card-icon.sucursales { background: #3498db; }
    .card-icon.conexiones { background: #9b59b6; }
    .card-icon.catalogo { background: #27ae60; }
    .card-title h3 { margin: 0; color: #2c3e50; font-size: 16px; }
    .card-description { font-size: 13px; color: #7f8c8d; }

    .card-body { padding: 20px; }

    .formato-label { font-size: 13px; color: #555; margin-bottom: 8px; font-weight: 500; }
    .formato-csv { background: #f8f9fa; padding: 12px; border-radius: 6px; font-size: 11px; color: #666; margin-bottom: 16px; overflow-x: auto; white-space: nowrap; }

    .file-upload { margin-bottom: 16px; }
    .file-label { display: flex; align-items: center; gap: 8px; cursor: pointer; }
    .file-label input[type="file"] { display: none; }
    .file-name { background: #3498db; color: white; padding: 10px 16px; border-radius: 6px; font-size: 13px; transition: background 0.2s; }
    .file-label:hover .file-name { background: #2980b9; }

    .header-toggle { margin-bottom: 16px; }
    .checkbox-label { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 13px; color: #555; }
    .checkbox-label input[type="checkbox"] { width: 16px; height: 16px; cursor: pointer; }

    .btn-upload { width: 100%; background: #3498db; color: white; border: none; padding: 12px; border-radius: 6px; cursor: pointer; font-weight: 500; font-size: 14px; transition: background 0.2s; }
    .btn-upload:hover:not(:disabled) { background: #2980b9; }
    .btn-upload:disabled { background: #bdc3c7; cursor: not-allowed; }

    .resultado { margin-top: 16px; padding: 12px; background: #f8f9fa; border-radius: 8px; display: flex; gap: 12px; align-items: flex-start; }
    .resultado-icon { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 16px; color: white; }
    .resultado-icon.success { background: #27ae60; }
    .resultado-icon.error { background: #e74c3c; }
    .resultado-texto { flex: 1; }
    .resultado-texto strong { font-size: 13px; color: #2c3e50; display: block; margin-bottom: 4px; }
    .resultado-texto p { margin: 2px 0; font-size: 12px; color: #666; }
  `]
})
export class CargaCsvComponent {
  archivos: { [key: string]: File | null } = {
    sucursales: null,
    conexiones: null,
    catalogo: null
  };

  tieneEncabezado: { [key: string]: boolean } = {
    sucursales: true,
    conexiones: true,
    catalogo: true
  };

  cargando: { [key: string]: boolean } = {
    sucursales: false,
    conexiones: false,
    catalogo: false
  };

  resultados: { [key: string]: CargaResult | null } = {
    sucursales: null,
    conexiones: null,
    catalogo: null
  };

  constructor(
    private readonly sucursalService: SucursalService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  onFileSelected(event: Event, tipo: string): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.archivos[tipo] = input.files[0];
      this.resultados[tipo] = null;
      this.cdr.detectChanges();
    }
  }

  subirArchivo(tipo: string): void {
    const archivo = this.archivos[tipo];
    if (!archivo) return;

    this.cargando[tipo] = true;
    this.resultados[tipo] = null;
    this.cdr.detectChanges();

    this.sucursalService.uploadCsv(archivo, tipo as 'sucursales' | 'conexiones' | 'catalogo', this.tieneEncabezado[tipo]).subscribe({
      next: (res: any) => {
        this.resultados[tipo] = {
          success: res.success,
          message: res.message,
          recordsLoaded: res.recordsLoaded || 0,
          totalErrors: res.totalErrors || 0,
          errors: res.errors
        };
        this.cargando[tipo] = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.resultados[tipo] = {
          success: false,
          message: 'Error al cargar el archivo: ' + (err.error?.message || err.message),
          recordsLoaded: 0,
          totalErrors: 1
        };
        this.cargando[tipo] = false;
        this.cdr.detectChanges();
      }
    });
  }
}