import { Component, OnInit, OnDestroy, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VisualizacionService } from '../../services/visualizacion.service';
import { ApiResponse } from '../../models/medicion.model';

interface NodoVisual {
  id: string;
  nombre: string;
  x: number;
  y: number;
  colaIngreso: number;
  colaPreparacion: number;
  colaSalida: number;
  estado: string;
}

interface ConexionVisual {
  origen: string;
  destino: string;
  tiempo: number;
}

interface TransferenciaActiva {
  id: string;
  producto: string;
  origen: string;
  destino: string;
  ruta: string[];
  etapa: string;
  etapaNombre: string;
  progreso: number;
  sucursalActual: string;
  tramoProgreso: number;
  tramoDesde?: string;
  tramoHacia?: string;
}

@Component({
  selector: 'app-visualizacion',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="visualizacion-page">
      <div class="header">
        <h1>Visualización</h1>
        <button class="btn-primary" (click)="actualizarEstado()" [disabled]="cargando">
          {{ cargando ? '...' : 'Actualizar' }}
        </button>
      </div>

      <div class="canvas-container">
        <canvas #canvas width="900" height="500"></canvas>
      </div>

      <div class="transferencias-panel" *ngIf="transferenciasActivas.length > 0">
        <h3>Transferencias</h3>
        <div class="transferencias-list">
          <div class="transferencia-item" *ngFor="let t of transferenciasActivas">
            <div class="transferencia-info">
              <strong>{{ t.producto }}</strong>
              <span>{{ t.origen }} → {{ t.destino }}</span>
            </div>
            <div class="progress-container">
              <div class="progress-bar">
                <div class="progress-fill" [style.width.%]="t.progreso * 100"></div>
              </div>
              <span class="progress-text">{{ (t.progreso * 100).toFixed(0) }}%</span>
            </div>
            <span class="etapa-badge">{{ t.etapaNombre }}</span>
          </div>
        </div>
      </div>

      <div class="stats-panel" *ngIf="sucursales.length > 0">
        <div class="sucursal-card" *ngFor="let s of sucursales">
          <span class="sucursal-id">{{ s.id }}</span>
          <div class="cola-bar">
            <div class="cola-segment" title="Ingreso: {{s.colaIngreso}}">
              <span class="cola-count" [class.active]="s.colaIngreso > 0">{{ s.colaIngreso }}</span>
              <span class="cola-label">In</span>
            </div>
            <div class="cola-segment" title="Despacho: {{s.colaPreparacion}}">
              <span class="cola-count" [class.active]="s.colaPreparacion > 0">{{ s.colaPreparacion }}</span>
              <span class="cola-label">Des</span>
            </div>
            <div class="cola-segment" title="Salida: {{s.colaSalida}}">
              <span class="cola-count" [class.active]="s.colaSalida > 0">{{ s.colaSalida }}</span>
              <span class="cola-label">Sal</span>
            </div>
          </div>
        </div>
      </div>

      <div class="error-message" *ngIf="error">
        <p>{{ error }}</p>
      </div>
    </div>
  `,
  styles: [`
    .visualizacion-page { padding: 20px; max-width: 1000px; margin: 0 auto; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
    h1 { color: #2c3e50; margin: 0; font-size: 20px; }

    .btn-primary { background: #3498db; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; font-size: 12px; }
    .btn-primary:hover { background: #2980b9; }
    .btn-primary:disabled { background: #bdc3c7; cursor: not-allowed; }

    .canvas-container { background: #fff; border-radius: 8px; padding: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    canvas { display: block; }

    .stats-panel { display: flex; gap: 12px; margin-top: 15px; flex-wrap: wrap; }

    .transferencias-panel { margin-top: 15px; background: white; border-radius: 8px; padding: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .transferencias-panel h3 { margin: 0 0 10px; color: #2c3e50; font-size: 14px; }
    .transferencias-list { display: flex; flex-direction: column; gap: 8px; }
    .transferencia-item { display: flex; align-items: center; gap: 12px; padding: 8px; background: #f8f9fa; border-radius: 6px; }
    .transferencia-info strong { color: #2c3e50; font-size: 12px; }
    .transferencia-info span { font-size: 10px; color: #7f8c8d; }
    .progress-container { display: flex; align-items: center; gap: 8px; flex: 1; }
    .progress-bar { flex: 1; height: 10px; background: #ecf0f1; border-radius: 5px; overflow: hidden; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #3498db, #2ecc71); }
    .progress-text { font-size: 11px; font-weight: 600; color: #2c3e50; min-width: 35px; }
    .etapa-badge { padding: 4px 8px; background: #3498db; color: white; border-radius: 4px; font-size: 10px; }
    .sucursal-card { display: flex; align-items: center; gap: 10px; background: white; border-radius: 6px; padding: 8px 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .sucursal-id { font-weight: bold; color: #2c3e50; min-width: 20px; }

    .cola-bar { display: flex; gap: 6px; }
    .cola-segment { display: flex; flex-direction: column; align-items: center; min-width: 28px; }
    .cola-count { font-size: 12px; font-weight: 600; color: #95a5a6; }
    .cola-count.active { color: #e74c3c; }
    .cola-label { font-size: 9px; color: #7f8c8d; }

    .error-message { background: #fee; border: 1px solid #e74c3c; color: #c0392b; padding: 10px; border-radius: 4px; margin-top: 10px; }
  `]
})
export class VisualizacionComponent implements OnInit, OnDestroy {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  private ctx!: CanvasRenderingContext2D;
  private animacionFrame: number = 0;
  private pollingInterval: any;
  cargando = false;
  error = '';
  sucursales: NodoVisual[] = [];
  conexiones: ConexionVisual[] = [];
  transferenciasActivas: TransferenciaActiva[] = [];
  private nodosPosiciones: Map<string, { x: number; y: number }> = new Map();

  constructor(
    private readonly visualizacionService: VisualizacionService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const canvas = this.canvasRef.nativeElement;
    this.ctx = canvas.getContext('2d')!;
    this.actualizarEstado();
    this.iniciarLoop();
    this.iniciarPolling();
  }

  ngOnDestroy(): void {
    if (this.animacionFrame) {
      cancelAnimationFrame(this.animacionFrame);
    }
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  actualizarEstado(): void {
    this.cargando = true;
    this.visualizacionService.getEstado().subscribe({
      next: (res: ApiResponse<any>) => {
        this.cargando = false;
        if (res.success && res.data) {
          this.sucursales = res.data.sucursales || [];
          this.conexiones = res.data.conexiones || [];
          this.transferenciasActivas = res.data.transferenciasActivas || [];
          this.calcularPosiciones();
          this.error = '';
        } else {
          this.error = res.message || 'Error cargando datos';
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.cargando = false;
        this.error = err.error?.message || 'Error de conexion';
        this.cdr.detectChanges();
      }
    });
  }

  iniciarPolling(): void {
    this.pollingInterval = setInterval(() => {
      this.actualizarEstado();
    }, 1000);
  }

  calcularPosiciones(): void {
    const canvas = this.canvasRef.nativeElement;
    const ancho = canvas.width;
    const alto = canvas.height;
    const padding = 80;
    const n = this.sucursales.length;

    if (n === 0) return;

    const cols = Math.min(n, 4);
    const filas = Math.ceil(n / cols);
    const espacioX = (ancho - 2 * padding) / (cols + 1);
    const espacioY = (alto - 2 * padding) / (filas + 1);

    this.sucursales.forEach((s, i) => {
      const fila = Math.floor(i / cols);
      const col = i % cols;
      s.x = padding + espacioX * (col + 1);
      s.y = padding + espacioY * (fila + 1);
      this.nodosPosiciones.set(s.id, { x: s.x, y: s.y });
    });
  }

  iniciarLoop(): void {
    const loop = () => {
      this.dibujar();
      this.animacionFrame = requestAnimationFrame(loop);
    };
    loop();
  }

  dibujar(): void {
    const canvas = this.canvasRef.nativeElement;
    this.ctx.clearRect(0, 0, canvas.width, canvas.height);
    this.ctx.fillStyle = '#f8f9fa';
    this.ctx.fillRect(0, 0, canvas.width, canvas.height);
    this.dibujarConexiones();
    this.dibujarNodos();
    this.dibujarTransferencias();
  }

  dibujarConexiones(): void {
    this.ctx.strokeStyle = '#bdc3c7';
    this.ctx.lineWidth = 2;

    this.conexiones.forEach((c) => {
      const nodoA = this.nodosPosiciones.get(c.origen);
      const nodoB = this.nodosPosiciones.get(c.destino);
      if (nodoA && nodoB) {
        this.ctx.beginPath();
        this.ctx.moveTo(nodoA.x, nodoA.y);
        this.ctx.lineTo(nodoB.x, nodoB.y);
        this.ctx.stroke();

        const midX = (nodoA.x + nodoB.x) / 2;
        const midY = (nodoA.y + nodoB.y) / 2;

        this.ctx.fillStyle = '#2ecc71';
        this.ctx.beginPath();
        this.ctx.arc(midX, midY, 12, 0, Math.PI * 2);
        this.ctx.fill();

        this.ctx.fillStyle = 'white';
        this.ctx.font = 'bold 9px Arial';
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillText(c.tiempo + 'm', midX, midY);
      }
    });
  }

  dibujarNodos(): void {
    this.sucursales.forEach(s => {
      const tieneCola = s.colaIngreso > 0 || s.colaPreparacion > 0 || s.colaSalida > 0;
      const enTransito = this.transferenciasActivas.some(t => t.origen === s.id || t.destino === s.id);

      let color = '#27ae60';
      if (enTransito) {
        color = '#3498db';
      } else if (tieneCola) {
        color = '#e74c3c';
      }

      this.ctx.fillStyle = color;
      this.ctx.beginPath();
      this.ctx.arc(s.x, s.y, 30, 0, Math.PI * 2);
      this.ctx.fill();

      this.ctx.strokeStyle = 'white';
      this.ctx.lineWidth = 3;
      this.ctx.stroke();

      this.ctx.fillStyle = 'white';
      this.ctx.font = 'bold 14px Arial';
      this.ctx.textAlign = 'center';
      this.ctx.textBaseline = 'middle';
      this.ctx.fillText(s.id, s.x, s.y - 5);

      this.ctx.font = '9px Arial';
      this.ctx.fillText(s.nombre.substring(0, 10), s.x, s.y + 10);

      if (s.colaIngreso > 0 || s.colaPreparacion > 0 || s.colaSalida > 0) {
        this.ctx.fillStyle = '#fff';
        this.ctx.font = 'bold 8px Arial';
        this.ctx.fillText('I:' + s.colaIngreso + ' D:' + s.colaPreparacion + ' S:' + s.colaSalida, s.x, s.y + 50);
      }
    });
  }

  dibujarTransferencias(): void {
    this.transferenciasActivas.forEach(t => {
      if (t.etapa === 'ENTREGADO') return;

      let posicionX = 0;
      let posicionY = 0;

      let color = '#3498db';
      if (t.etapa.includes('ORIGEN')) {
        color = '#e67e22';
      } else if (t.etapa.includes('INTERMEDIA') || t.etapa.includes('SALIDA_INTERMEDIA')) {
        color = '#9b59b6';
      } else if (t.etapa.includes('DESTINO')) {
        color = '#27ae60';
      }

      if (t.etapa.includes('VIAJE') && t.tramoDesde && t.tramoHacia) {
        const posDesde = this.nodosPosiciones.get(t.tramoDesde);
        const posHacia = this.nodosPosiciones.get(t.tramoHacia);

        if (posDesde && posHacia) {
          const progreso = t.tramoProgreso || 0;
          posicionX = posDesde.x + (posHacia.x - posDesde.x) * progreso;
          posicionY = posDesde.y + (posHacia.y - posDesde.y) * progreso;
        }
      } else {
        const posSucursal = this.nodosPosiciones.get(t.sucursalActual);
        if (posSucursal) {
          posicionX = posSucursal.x + 40;
          posicionY = posSucursal.y - 20;
        }
      }

      if (posicionX === 0 && posicionY === 0) return;

      this.ctx.fillStyle = color;
      this.ctx.beginPath();
      this.ctx.arc(posicionX, posicionY, 10, 0, Math.PI * 2);
      this.ctx.fill();

      this.ctx.fillStyle = 'white';
      this.ctx.font = 'bold 7px Arial';
      this.ctx.textAlign = 'center';
      this.ctx.textBaseline = 'middle';
      this.ctx.fillText(t.producto.substring(0, 5), posicionX, posicionY);
    });
  }
}