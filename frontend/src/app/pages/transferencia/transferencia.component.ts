import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransferenciaService } from '../../services/transferencia.service';
import { SucursalService } from '../../services/sucursal.service';
import { Sucursal } from '../../models/sucursal.model';
import { RutaResponse } from '../../models/transferencia.model';
import { ApiResponse } from '../../models/medicion.model';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="transferencia-page">
      <h1>Transferencia de Productos</h1>

      <div class="transfer-form">
        <div class="form-section">
          <h3>Sucursales</h3>
          <div class="form-row">
            <div class="form-group">
              <label>Origen</label>
              <select [(ngModel)]="origenId" (change)="cargarColas()">
                <option value="">Seleccionar...</option>
                <option *ngFor="let s of sucursales" [value]="s.id">{{ s.nombre }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>Destino</label>
              <select [(ngModel)]="destinoId">
                <option value="">Seleccionar...</option>
                <option *ngFor="let s of sucursales" [value]="s.id">{{ s.nombre }}</option>
              </select>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3>Criterio de Ruta</h3>
          <div class="radio-group">
            <label class="radio-option">
              <input type="radio" [(ngModel)]="criterio" value="tiempo">
              <span>Mínimo Tiempo</span>
            </label>
            <label class="radio-option">
              <input type="radio" [(ngModel)]="criterio" value="costo">
              <span>Mínimo Costo</span>
            </label>
          </div>
        </div>

        <div class="form-section">
          <h3>Datos del Producto</h3>
          <div class="form-group">
            <label>Código de Barras</label>
            <input type="text" [(ngModel)]="barcode" placeholder="Ingrese el código de barras del producto a transferir">
          </div>
        </div>

        <button class="btn-primary btn-lg" (click)="calcularRuta()" [disabled]="!origenId || !destinoId">
          Calcular Ruta
        </button>
      </div>

      <div class="ruta-result" *ngIf="ruta">
        <h3>Ruta Calculada</h3>
        <div class="ruta-info">
          <p><strong>Origen:</strong> {{ ruta.origen }}</p>
          <p><strong>Destino:</strong> {{ ruta.destino }}</p>
          <p><strong>Criterio:</strong> {{ ruta.criterio }}</p>
          <p><strong>Saltos:</strong> {{ ruta.saltos }}</p>
        </div>
        <div class="ruta-path">
          <span class="ruta-step" *ngFor="let step of ruta.ruta; let last = last">
            {{ step }}<span *ngIf="!last" class="arrow">→</span>
          </span>
        </div>
        <div class="error-message" *ngIf="errorMensaje">
          <span>{{ errorMensaje }}</span>
        </div>
        <div class="success-message" *ngIf="successMensaje">
          <span>{{ successMensaje }}</span>
        </div>
        <button class="btn-transfer" (click)="transferir()">Iniciar Transferencia</button>
      </div>

      <div class="colas-section" *ngIf="origenId">
        <h3>Estado de Colas - {{ getSucursalNombre(origenId) }}</h3>
        <div class="colas-grid">
          <div class="cola-card">
            <div class="cola-header ingreso">Cola de Ingreso</div>
            <div class="cola-body">
              <span class="cola-count">{{ colas.ingreso }}</span>
              <span class="cola-label">elementos</span>
            </div>
          </div>
          <div class="cola-card">
            <div class="cola-header preparacion">Cola de Preparación</div>
            <div class="cola-body">
              <span class="cola-count">{{ colas.preparacion }}</span>
              <span class="cola-label">elementos</span>
            </div>
          </div>
          <div class="cola-card">
            <div class="cola-header salida">Cola de Salida</div>
            <div class="cola-body">
              <span class="cola-count">{{ colas.salida }}</span>
              <span class="cola-label">elementos</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .transferencia-page { max-width: 900px; margin: 0 auto; }
    h1 { color: #2c3e50; margin-bottom: 24px; }
    h3 { color: #34495e; margin: 0 0 16px; font-size: 16px; }

    .transfer-form { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); margin-bottom: 24px; }
    .form-section { margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px solid #eee; }
    .form-section:last-of-type { border-bottom: none; margin-bottom: 0; padding-bottom: 0; }
    .form-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
    .form-group { display: flex; flex-direction: column; }
    .form-group label { margin-bottom: 8px; font-weight: 500; color: #2c3e50; font-size: 14px; }
    .form-group select, .form-group input { padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; background: #fafafa; }
    .form-group input::placeholder { color: #aaa; font-style: italic; }
    .form-group select:hover, .form-group input:hover { border-color: #3498db; }
    .form-group select:focus, .form-group input:focus { outline: none; border-color: #3498db; box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1); background: white; }

    .radio-group { display: flex; gap: 24px; }
    .radio-option { display: flex; align-items: center; gap: 8px; cursor: pointer; }
    .radio-option input { width: 18px; height: 18px; }
    .radio-option span { font-size: 14px; color: #555; }

    .btn-primary { background: #3498db; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-weight: 500; }
    .btn-primary:disabled { background: #bdc3c7; cursor: not-allowed; }
    .btn-lg { width: 100%; font-size: 16px; padding: 14px; }

    .ruta-result { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); margin-bottom: 24px; border-left: 4px solid #3498db; }
    .ruta-info { display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; margin-bottom: 16px; }
    .ruta-info p { margin: 0; font-size: 14px; }
    .ruta-path { background: #f8f9fa; padding: 16px; border-radius: 8px; display: flex; flex-wrap: wrap; align-items: center; gap: 8px; margin-bottom: 16px; }
    .ruta-step { font-weight: 500; color: #2c3e50; }
    .arrow { color: #3498db; font-size: 18px; }
    .btn-transfer { background: #27ae60; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-weight: 500; }

    .colas-section { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .colas-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
    .cola-card { border: 1px solid #eee; border-radius: 8px; overflow: hidden; }
    .cola-header { padding: 12px; text-align: center; font-weight: 600; font-size: 14px; color: white; }
    .cola-header.ingreso { background: #3498db; }
    .cola-header.preparacion { background: #f39c12; }
    .cola-header.salida { background: #27ae60; }
    .cola-body { padding: 20px; text-align: center; }
    .cola-count { display: block; font-size: 32px; font-weight: bold; color: #2c3e50; }
    .cola-label { font-size: 12px; color: #7f8c8d; }

    .error-message { background: #fee; border: 1px solid #e74c3c; color: #c0392b; padding: 12px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; }
    .success-message { background: #efe; border: 1px solid #27ae60; color: #27ae60; padding: 12px; border-radius: 6px; margin-bottom: 16px; font-size: 14px; }
  `]
})
export class TransferenciaComponent implements OnInit {
  sucursales: Sucursal[] = [];
  origenId = '';
  destinoId = '';
  criterio: 'tiempo' | 'costo' = 'tiempo';
  barcode = '';
  ruta: RutaResponse | null = null;
  colas: any = { ingreso: 0, preparacion: 0, salida: 0 };
  errorMensaje = '';
  successMensaje = '';

  constructor(
    private readonly transferenciaService: TransferenciaService,
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

  cargarColas(): void {
    if (!this.origenId) return;
    this.transferenciaService.getColas(this.origenId).subscribe({
      next: (res) => {
        this.colas = res.data || this.colas;
        this.cdr.detectChanges();
      }
    });
  }

  calcularRuta(): void {
    this.ruta = null;
    this.errorMensaje = '';
    this.transferenciaService.calcularRuta(this.origenId, this.destinoId, this.criterio).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.ruta = res.data;
        } else {
          this.errorMensaje = res.message || 'Error calculando ruta';
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMensaje = err.error?.message || 'Error calculando ruta';
        this.cdr.detectChanges();
      }
    });
  }

  transferir(): void {
    this.errorMensaje = '';
    this.successMensaje = '';
    this.transferenciaService.transferir({
      origenId: this.origenId,
      destinoId: this.destinoId,
      productoBarcode: this.barcode,
      criterio: this.criterio
    }).subscribe({
      next: (res) => {
        if (res.success) {
          this.successMensaje = `Producto transferido exitosamente a ${this.destinoId}`;
          this.ruta = null;
          this.barcode = '';
          this.cargarColas();
        } else {
          this.errorMensaje = res.message;
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMensaje = err.error?.message || 'Error en la transferencia';
        this.cdr.detectChanges();
      }
    });
  }

  getSucursalNombre(id: string): string {
    const s = this.sucursales.find(x => x.id === id);
    return s ? s.nombre : id;
  }
}