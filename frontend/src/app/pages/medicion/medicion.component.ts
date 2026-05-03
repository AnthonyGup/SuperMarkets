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
      <h1>Medición de Rendimiento</h1>

      <div class="medicion-form">
        <div class="form-group">
          <label>Sucursal</label>
          <select [(ngModel)]="sucursalId" (change)="cargarElementos()">
            <option value="">Seleccionar...</option>
            <option *ngFor="let s of sucursales" [value]="s.id">{{ s.nombre }}</option>
          </select>
        </div>

        <div class="form-group">
          <label>Operación</label>
          <select [(ngModel)]="operacion">
            <option value="buscar">Búsqueda</option>
            <option value="insertar">Inserción</option>
          </select>
        </div>

        <div class="form-group" *ngIf="operacion === 'buscar'">
          <label>Nombre del producto a buscar</label>
          <input type="text" [(ngModel)]="nombreBusqueda" placeholder="Ingrese el nombre exacto del producto que desea buscar">
        </div>

        <button class="btn-primary" (click)="ejecutarMedicion()" [disabled]="!sucursalId">
          Ejecutar Benchmark
        </button>
      </div>

      <div class="resultados-section" *ngIf="resultados.length > 0">
        <h2>Resultados</h2>
        <div class="resultados-table">
          <table>
            <thead>
              <tr>
                <th>Estructura</th>
                <th>Tiempo (ms)</th>
                <th>Elementos</th>
                <th>Detalles</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let r of resultados">
                <td><strong>{{ r.estructura }}</strong></td>
                <td>{{ r.tiempoMs | number:'1.4-4' }}</td>
                <td>{{ r.cantidadElementos }}</td>
                <td>{{ r.detalles?.join(', ') || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="comparacion-chart">
          <h3>Comparación Visual</h3>
          <div class="bar-chart">
            <div class="bar-item" *ngFor="let r of resultados">
              <div class="bar-label">{{ r.estructura }}</div>
              <div class="bar-container">
                <div class="bar" [style.width.%]="getBarWidth(r.tiempoMs)" [class.fast]="r.tiempoMs < 10" [class.medium]="r.tiempoMs >= 10 && r.tiempoMs < 100" [class.slow]="r.tiempoMs >= 100">
                  {{ r.tiempoMs | number:'1.2-2' }} ms
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" *ngIf="!ejecutando && resultados.length === 0">
        <p>Seleccione una sucursal y ejecute el benchmark para ver resultados</p>
      </div>
    </div>
  `,
  styles: [`
    .medicion-page { max-width: 900px; margin: 0 auto; }
    h1 { color: #2c3e50; margin-bottom: 24px; }
    h2 { color: #34495e; margin: 24px 0 16px; }
    h3 { color: #34495e; margin: 24px 0 16px; font-size: 16px; }

    .medicion-form { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); display: flex; flex-direction: column; gap: 16px; }
    .form-group { display: flex; flex-direction: column; }
    .form-group label { margin-bottom: 8px; font-weight: 500; color: #2c3e50; font-size: 14px; }
    .form-group select, .form-group input { padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; background: #fafafa; }
    .form-group input::placeholder { color: #aaa; font-style: italic; }
    .form-group select:hover, .form-group input:hover { border-color: #3498db; }
    .form-group select:focus, .form-group input:focus { outline: none; border-color: #3498db; box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1); background: white; }
    .btn-primary { background: #3498db; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-weight: 500; }
    .btn-primary:hover { background: #2980b9; }
    .btn-primary:disabled { background: #bdc3c7; cursor: not-allowed; }

    .resultados-section { margin-top: 24px; }
    .resultados-table { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    table { width: 100%; border-collapse: collapse; }
    th { background: #34495e; color: white; padding: 12px 16px; text-align: left; font-size: 13px; }
    td { padding: 12px 16px; border-bottom: 1px solid #eee; font-size: 14px; }
    tr:hover { background: #f8f9fa; }

    .comparacion-chart { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); margin-top: 24px; }
    .bar-chart { display: flex; flex-direction: column; gap: 16px; }
    .bar-item { display: flex; align-items: center; gap: 16px; }
    .bar-label { width: 80px; font-weight: 500; color: #2c3e50; font-size: 14px; }
    .bar-container { flex: 1; background: #ecf0f1; border-radius: 4px; height: 30px; }
    .bar { height: 100%; border-radius: 4px; display: flex; align-items: center; padding-left: 12px; color: white; font-size: 12px; font-weight: 500; min-width: 80px; }
    .bar.fast { background: #27ae60; }
    .bar.medium { background: #f39c12; }
    .bar.slow { background: #e74c3c; }

    .empty-state { text-align: center; padding: 48px; background: white; border-radius: 12px; color: #7f8c8d; margin-top: 24px; }
  `]
})
export class MedicionComponent implements OnInit {
  sucursales: Sucursal[] = [];
  sucursalId = '';
  operacion = 'buscar';
  nombreBusqueda = '';
  resultados: ResultadoMedicion[] = [];
  ejecutando = false;

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

  cargarElementos(): void {
    this.resultados = [];
  }

  ejecutarMedicion(): void {
    if (!this.sucursalId) return;
    this.ejecutando = true;

    if (this.operacion === 'buscar') {
      this.medicionService.comparar(this.sucursalId, 'buscar', this.nombreBusqueda).subscribe({
        next: (res: ApiResponse<ResultadoMedicion[]>) => {
          this.resultados = res.data || [];
          this.ejecutando = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.ejecutando = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  getBarWidth(tiempoMs: number): number {
    if (this.resultados.length === 0) return 0;
    const maxTiempo = Math.max(...this.resultados.map(r => r.tiempoMs), 1);
    return Math.min((tiempoMs / maxTiempo) * 100, 100);
  }
}