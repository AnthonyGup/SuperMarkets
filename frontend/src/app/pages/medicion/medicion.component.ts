import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MedicionService } from '../../services/medicion.service';
import { SucursalService } from '../../services/sucursal.service';
import { Sucursal } from '../../models/sucursal.model';
import { ResultadoMedicion } from '../../models/medicion.model';
import { ApiResponse } from '../../models/medicion.model';

@Component({
  selector: 'app-medicion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="medicion-page">
      <h1>Medición de Búsqueda</h1>
      <p class="subtitle">Comparación de tiempos: Lista vs AVL vs Hash</p>

      <div class="medicion-form">
        <div class="form-row">
          <div class="form-group">
            <label>Sucursal</label>
            <select [(ngModel)]="sucursalId" (change)="resultados = []">
              <option value="">Seleccionar...</option>
              <option *ngFor="let s of sucursales" [value]="s.id">{{ s.nombre }}</option>
            </select>
          </div>

          <div class="form-group">
            <label>Nombre del producto</label>
            <input type="text" [(ngModel)]="nombre" placeholder="Dejar vacío para aleatorio">
          </div>

          <div class="form-group">
            <label>Código de barras</label>
            <input type="text" [(ngModel)]="barcode" placeholder="Dejar vacío para aleatorio">
          </div>

          <div class="form-group">
            <label>Iteraciones</label>
            <input type="number" [(ngModel)]="iteraciones" min="1" max="1000">
          </div>
        </div>

        <button class="btn-primary" (click)="ejecutarMedicion()" [disabled]="!sucursalId || ejecutando">
          {{ ejecutando ? 'Ejecutando...' : 'Comparar Búsquedas' }}
        </button>
      </div>

      <div class="resultados-section" *ngIf="resultados.length > 0">
        <h2>Resultados</h2>
        <p class="info">Elementos: {{ totalElementos }} | Iteraciones: {{ iteraciones }} | Clave: {{ claveUsada }}</p>

        <table>
          <thead>
            <tr>
              <th>Estructura</th>
              <th>Complejidad</th>
              <th>Tiempo Promedio (ms)</th>
              <th>Resultado</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let r of resultados" [class.best]="esElMejor(r.tiempoPromedioMs)">
              <td><strong>{{ r.estructura }}</strong></td>
              <td><code>{{ r.complejidad }}</code></td>
              <td class="tiempo">{{ r.tiempoPromedioMs | number:'1.4-4' }}</td>
              <td>{{ r.detalles[0] }}</td>
            </tr>
          </tbody>
        </table>

        <div class="bar-chart">
          <h3>Comparación Visual</h3>
          <div class="bar-item" *ngFor="let r of resultados">
            <span class="bar-label">{{ r.estructura }}</span>
            <div class="bar-container">
              <div class="bar" [style.width.%]="getBarWidth(r.tiempoPromedioMs)" [class.best]="esElMejor(r.tiempoPromedioMs)">
                {{ r.tiempoPromedioMs | number:'1.3-3' }} ms
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" *ngIf="!ejecutando && resultados.length === 0">
        <p>Seleccione una sucursal y ejecute la comparación</p>
      </div>
    </div>
  `,
  styles: [`
    .medicion-page { max-width: 900px; margin: 0 auto; padding: 20px; }
    h1 { color: #2c3e50; margin-bottom: 4px; }
    h2 { color: #34495e; margin: 24px 0 12px; font-size: 18px; }
    h3 { color: #34495e; margin: 16px 0 12px; font-size: 14px; }
    .subtitle { color: #7f8c8d; margin-bottom: 20px; font-size: 14px; }
    .info { color: #7f8c8d; font-size: 13px; margin-bottom: 12px; }

    .medicion-form { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .form-row { display: flex; gap: 16px; flex-wrap: wrap; }
    .form-group { flex: 1; min-width: 150px; }
    .form-group label { display: block; margin-bottom: 6px; font-weight: 500; color: #2c3e50; font-size: 13px; }
    .form-group input, .form-group select { width: 100%; padding: 8px 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 13px; box-sizing: border-box; }
    .form-group input:focus, .form-group select:focus { outline: none; border-color: #3498db; }
    .btn-primary { width: 100%; margin-top: 16px; padding: 12px; background: #3498db; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500; }
    .btn-primary:hover { background: #2980b9; }
    .btn-primary:disabled { background: #bdc3c7; cursor: not-allowed; }

    table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    th { background: #34495e; color: white; padding: 10px 12px; text-align: left; font-size: 13px; }
    td { padding: 10px 12px; border-bottom: 1px solid #eee; font-size: 13px; }
    tr:hover { background: #f8f9fa; }
    tr.best { background: #e8f8f5; }
    td.tiempo { font-family: monospace; font-weight: 600; }
    code { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; font-size: 11px; }

    .bar-chart { background: white; padding: 20px; border-radius: 8px; margin-top: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .bar-item { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; }
    .bar-label { width: 50px; font-weight: 500; color: #2c3e50; font-size: 13px; }
    .bar-container { flex: 1; background: #ecf0f1; border-radius: 4px; height: 24px; }
    .bar { height: 100%; border-radius: 4px; display: flex; align-items: center; padding-left: 8px; color: white; font-size: 11px; font-weight: 500; min-width: 60px; background: #3498db; }
    .bar.best { background: #27ae60; }

    .empty-state { text-align: center; padding: 40px; background: white; border-radius: 8px; color: #7f8c8d; margin-top: 20px; }
  `]
})
export class MedicionComponent implements OnInit {
  sucursales: Sucursal[] = [];
  sucursalId = '';
  nombre = '';
  barcode = '';
  iteraciones = 1;
  ejecutando = false;
  resultados: ResultadoMedicion[] = [];
  totalElementos = 0;
  claveUsada = '';

  constructor(
    private readonly medicionService: MedicionService,
    private readonly sucursalService: SucursalService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.sucursalService.getAll().subscribe({
      next: (res: ApiResponse<Sucursal[]>) => {
        this.sucursales = res.data || [];
        this.cdr.detectChanges();
      }
    });
  }

  ejecutarMedicion(): void {
    if (!this.sucursalId) return;
    this.ejecutando = true;

    const params: any = {};
    if (this.nombre) params.nombre = this.nombre;
    if (this.barcode) params.barcode = this.barcode;
    if (this.iteraciones > 1) params.iteraciones = this.iteraciones;

    this.medicionService.comparar(this.sucursalId, params).subscribe({
      next: (res: ApiResponse<ResultadoMedicion[]>) => {
        this.resultados = res.data || [];
        if (this.resultados.length > 0) {
          this.totalElementos = this.resultados[0].cantidadElementos;
          this.claveUsada = this.resultados[0].claveBusqueda || '-';
        }
        this.ejecutando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.ejecutando = false;
        this.cdr.detectChanges();
      }
    });
  }

  getBarWidth(tiempoMs: number): number {
    if (this.resultados.length === 0) return 0;
    const maxTiempo = Math.max(...this.resultados.map(r => r.tiempoPromedioMs), 0.001);
    return Math.min((tiempoMs / maxTiempo) * 100, 100);
  }

  esElMejor(tiempoMs: number): boolean {
    if (this.resultados.length === 0) return false;
    const minTiempo = Math.min(...this.resultados.map(r => r.tiempoPromedioMs));
    return tiempoMs === minTiempo;
  }
}