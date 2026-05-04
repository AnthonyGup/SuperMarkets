import { Component, OnInit, OnDestroy, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VisualizacionService } from '../../services/visualizacion.service';
import { ApiResponse } from '../../models/medicion.model';
import { instance as vizInstance } from '@viz-js/viz';

interface NodoVisual {
  id: string;
  nombre: string;
  x: number;
  y: number;
  colaIngreso: number;
  colaPreparacion: number;
  colaSalida: number;
}

interface ConexionVisual {
  origen: string;
  destino: string;
  tiempo: number;
  costo: number;
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

type VistaEstructura = 'B' | 'B+' | 'AVL' | 'Hash' | 'Grafo';
type CriterioGrafo = 'tiempo' | 'costo';

interface HistorialTransferencia {
  id: string;
  producto: string;
  origen: string;
  destino: string;
  duracionMs: number;
}

interface DotResponse {
  tipo: string;
  dot: string;
  totalNodos: number;
  altura: number;
}

@Component({
  selector: 'app-visualizacion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="visualizacion-page">
      <div class="header">
        <h1>Visualización</h1>
        <div class="header-actions">
          <select class="structure-select" [(ngModel)]="vistaSeleccionada" (ngModelChange)="onVistaChange()">
            <option value="Grafo">Grafo (Transferencias)</option>
            <option value="B">Árbol B</option>
            <option value="B+">Árbol B+</option>
            <option value="AVL">Árbol AVL</option>
            <option value="Hash">Tabla Hash</option>
          </select>
          <button class="btn-primary" (click)="actualizarGrafo()" [disabled]="cargando || vistaSeleccionada !== 'Grafo'">
            {{ cargando ? '...' : 'Actualizar' }}
          </button>
        </div>
      </div>

      <!-- GRAFO -->
      <ng-container *ngIf="vistaSeleccionada === 'Grafo'">
        <div class="grafo-controls">
          <div class="control-group">
            <label>Mostrar pesos por:</label>
            <select [(ngModel)]="criterioGrafo">
              <option value="tiempo">Tiempo</option>
              <option value="costo">Costo</option>
            </select>
          </div>
        </div>

        <div class="canvas-container">
          <canvas #canvas width="900" height="500"></canvas>
        </div>

        <div class="transferencias-panel" *ngIf="transferenciasActivas.length > 0">
          <h3>Transferencias Activas ({{ transferenciasActivas.length }})</h3>
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

        <div class="historial-panel" *ngIf="historialTransferencias.length > 0">
          <h3>Historial ({{ historialTransferencias.length }})</h3>
          <div class="historial-list">
            <div class="historial-item" *ngFor="let h of historialTransferencias">
              <div class="historial-info">
                <strong>{{ h.producto }}</strong>
                <span>{{ h.origen }} → {{ h.destino }}</span>
                <span class="duracion">{{ formatearDuracion(h.duracionMs) }}</span>
              </div>
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
      </ng-container>

      <!-- HASH -->
      <ng-container *ngIf="vistaSeleccionada === 'Hash'">
        <div class="estructura-selector">
          <label>Sucursal:</label>
          <select [(ngModel)]="sucursalSeleccionada" (ngModelChange)="cargarHash()">
            <option value="">Seleccionar sucursal...</option>
            <option *ngFor="let s of sucursalesBase" [value]="s.id">{{ s.id }} - {{ s.nombre }}</option>
          </select>
        </div>

        <div class="dot-container" *ngIf="dotSvg">
          <h2>Tabla Hash - {{ getNombreSucursal() }}</h2>
          <p class="estructura-stats">Capacidad: {{ datosArbol?.totalNodos }} | Elementos: {{ datosArbol?.altura }}</p>
          <div #dotGraph class="dot-graph"></div>
        </div>
      </ng-container>

      <!-- ARBOLES -->
      <ng-container *ngIf="vistaSeleccionada === 'AVL' || vistaSeleccionada === 'B' || vistaSeleccionada === 'B+'">
        <div class="estructura-selector">
          <label>Sucursal:</label>
          <select [(ngModel)]="sucursalSeleccionada" (ngModelChange)="cargarArbol()">
            <option value="">Seleccionar sucursal...</option>
            <option *ngFor="let s of sucursalesBase" [value]="s.id">{{ s.id }} - {{ s.nombre }}</option>
          </select>
        </div>

        <div class="dot-container" *ngIf="dotSvg">
          <h2>{{ getNombreVista(vistaSeleccionada) }} - {{ getNombreSucursal() }}</h2>
          <p class="estructura-stats">Nodos: {{ datosArbol?.totalNodos }} | Altura: {{ datosArbol?.altura }}</p>
          <div #dotGraph class="dot-graph"></div>
        </div>

        <div class="empty-state" *ngIf="!dotSvg && !cargando && sucursalSeleccionada">
          <p>Selecciona una sucursal para ver el árbol</p>
        </div>
      </ng-container>

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

    .grafo-controls { background: white; border-radius: 8px; padding: 12px; margin-bottom: 15px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .control-group { display: flex; align-items: center; gap: 8px; }
    .control-group label { font-size: 12px; color: #2c3e50; font-weight: 500; }
    .control-group select { padding: 6px 10px; border: 1px solid #bdc3c7; border-radius: 4px; font-size: 12px; }

    .canvas-container { background: white; border-radius: 8px; padding: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .canvas-container canvas { display: block; }

    .transferencias-panel { margin-top: 15px; background: white; border-radius: 8px; padding: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .transferencias-panel h3 { margin: 0 0 10px; color: #2c3e50; font-size: 14px; }
    .transferencias-list { display: flex; flex-direction: column; gap: 8px; }
    .transferencia-item { display: flex; align-items: center; gap: 12px; padding: 8px; background: #f8f9fa; border-radius: 6px; }
    .transferencia-info strong { color: #2c3e50; font-size: 12px; display: block; }
    .transferencia-info span { font-size: 10px; color: #7f8c8d; }
    .progress-container { display: flex; align-items: center; gap: 8px; flex: 1; }
    .progress-bar { flex: 1; height: 10px; background: #ecf0f1; border-radius: 5px; overflow: hidden; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #3498db, #2ecc71); }
    .progress-text { font-size: 11px; font-weight: 600; color: #2c3e50; min-width: 35px; }
    .etapa-badge { padding: 4px 8px; background: #3498db; color: white; border-radius: 4px; font-size: 10px; }

    .historial-panel { margin-top: 15px; background: white; border-radius: 8px; padding: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); max-height: 200px; overflow-y: auto; }
    .historial-panel h3 { margin: 0 0 10px; color: #2c3e50; font-size: 14px; }
    .historial-list { display: flex; flex-direction: column; gap: 6px; }
    .historial-item { padding: 6px 8px; background: #f8f9fa; border-radius: 4px; }
    .historial-info { display: flex; align-items: center; gap: 12px; }
    .historial-info strong { color: #2c3e50; font-size: 11px; }
    .historial-info span { font-size: 10px; color: #7f8c8d; }
    .duracion { color: #27ae60 !important; font-weight: 500; }

    .stats-panel { display: flex; gap: 12px; margin-top: 15px; flex-wrap: wrap; }
    .sucursal-card { display: flex; align-items: center; gap: 10px; background: white; border-radius: 6px; padding: 8px 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .sucursal-id { font-weight: bold; color: #2c3e50; min-width: 20px; }
    .cola-bar { display: flex; gap: 6px; }
    .cola-segment { display: flex; flex-direction: column; align-items: center; min-width: 28px; }
    .cola-count { font-size: 12px; font-weight: 600; color: #95a5a6; }
    .cola-count.active { color: #e74c3c; }
    .cola-label { font-size: 9px; color: #7f8c8d; }

    .estructura-selector { margin: 20px 0; display: flex; align-items: center; gap: 10px; }
    .estructura-selector label { font-size: 12px; color: #2c3e50; font-weight: 500; }
    .estructura-selector select { padding: 6px 10px; border: 1px solid #bdc3c7; border-radius: 4px; font-size: 12px; min-width: 200px; }

    .dot-container { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .dot-container h2 { margin: 0 0 5px; color: #2c3e50; }
    .estructura-stats { margin: 0 0 15px; color: #7f8c8d; font-size: 12px; }
    .dot-graph { display: flex; justify-content: center; align-items: center; min-height: 400px; overflow-x: auto; }
    .dot-graph svg { max-width: 100%; height: auto; }

    .empty-state { background: white; border-radius: 8px; padding: 60px 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; margin-top: 20px; color: #7f8c8d; }
    .empty-state p { font-size: 14px; }

    .header-actions { display: flex; gap: 10px; align-items: center; }
    .structure-select { padding: 8px 12px; border: 1px solid #bdc3c7; border-radius: 4px; background: white; font-size: 12px; cursor: pointer; min-width: 150px; }
    .structure-select:hover { border-color: #3498db; }

    .error-message { background: #fee; border: 1px solid #e74c3c; color: #c0392b; padding: 10px; border-radius: 4px; margin-top: 10px; }
  `]
})
export class VisualizacionComponent implements OnInit, OnDestroy {
  @ViewChild('canvas', { static: false }) canvasRef?: ElementRef<HTMLCanvasElement>;
  @ViewChild('dotGraph', { static: false }) dotGraphRef?: ElementRef<HTMLDivElement>;

  private ctx: CanvasRenderingContext2D | null = null;
  private animacionFrame: number = 0;
  private viz: any = null;
  private esPrimero: boolean = true;

  cargando = false;
  error = '';
  vistaSeleccionada: VistaEstructura = 'Grafo';
  criterioGrafo: CriterioGrafo = 'tiempo';
  sucursalSeleccionada = '';

  sucursales: NodoVisual[] = [];
  sucursalesBase: NodoVisual[] = [];
  conexiones: ConexionVisual[] = [];
  transferenciasActivas: TransferenciaActiva[] = [];
  historialTransferencias: HistorialTransferencia[] = [];
  private nodosPosiciones = new Map<string, { x: number; y: number }>();

  dotSvg: string | null = null;
  datosArbol: DotResponse | null = null;

  constructor(
    private readonly visualizacionService: VisualizacionService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.inicializarViz();
    this.cargarSucursalesBase();
  }

  ngOnDestroy(): void {
    this.detenerLoop();
  }

  private inicializarViz(): void {
    vizInstance().then((viz: any) => {
      this.viz = viz;
    }).catch((err: any) => {
      console.error('Error initializing Viz:', err);
    });
  }

  private cargarSucursalesBase(): void {
    this.visualizacionService.getEstado().subscribe({
      next: (res: ApiResponse<any>) => {
        if (res.success && res.data) {
          this.sucursalesBase = res.data.sucursales || [];
          if (this.vistaSeleccionada === 'Grafo') {
            this.iniciarGrafo(res.data);
          }
        }
      },
      error: () => {}
    });
  }

  private iniciarGrafo(data: any): void {
    this.sucursales = data.sucursales || [];
    this.conexiones = data.conexiones || [];
    this.transferenciasActivas = data.transferenciasActivas || [];
    this.calcularPosiciones();

    if (this.canvasRef) {
      const canvas = this.canvasRef.nativeElement;
      this.ctx = canvas.getContext('2d');
      this.iniciarLoop();
    }

    this.visualizacionService.getHistorialTransferencias().subscribe({
      next: (res: ApiResponse<HistorialTransferencia[]>) => {
        if (res.success && res.data) {
          this.historialTransferencias = res.data;
        }
      },
      error: () => {}
    });
  }

  private calcularPosiciones(): void {
    if (!this.canvasRef || this.sucursales.length === 0) return;

    const canvas = this.canvasRef.nativeElement;
    const ancho = canvas.width;
    const alto = canvas.height;
    const padding = 80;
    const n = this.sucursales.length;

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

  private iniciarLoop(): void {
    if (this.animacionFrame) return;
    const loop = () => {
      this.dibujarGrafo();
      this.animacionFrame = requestAnimationFrame(loop);
    };
    loop();
  }

  private detenerLoop(): void {
    if (this.animacionFrame) {
      cancelAnimationFrame(this.animacionFrame);
      this.animacionFrame = 0;
    }
  }

  private dibujarGrafo(): void {
    if (!this.ctx || !this.canvasRef) return;

    const canvas = this.canvasRef.nativeElement;
    this.ctx.clearRect(0, 0, canvas.width, canvas.height);
    this.ctx.fillStyle = '#f8f9fa';
    this.ctx.fillRect(0, 0, canvas.width, canvas.height);

    this.ctx.strokeStyle = '#bdc3c7';
    this.ctx.lineWidth = 2;
    this.conexiones.forEach((c) => {
      const nodoA = this.nodosPosiciones.get(c.origen);
      const nodoB = this.nodosPosiciones.get(c.destino);
      if (nodoA && nodoB) {
        this.ctx!.beginPath();
        this.ctx!.moveTo(nodoA.x, nodoA.y);
        this.ctx!.lineTo(nodoB.x, nodoB.y);
        this.ctx!.stroke();

        const midX = (nodoA.x + nodoB.x) / 2;
        const midY = (nodoA.y + nodoB.y) / 2;
        this.ctx!.fillStyle = '#2ecc71';
        this.ctx!.beginPath();
        this.ctx!.arc(midX, midY, 16, 0, Math.PI * 2);
        this.ctx!.fill();

        const pesoMostrar = this.criterioGrafo === 'tiempo' ? c.tiempo + 'm' : c.costo + '$';
        this.ctx!.fillStyle = 'white';
        this.ctx!.font = 'bold 8px Arial';
        this.ctx!.textAlign = 'center';
        this.ctx!.textBaseline = 'middle';
        this.ctx!.fillText(pesoMostrar, midX, midY);
      }
    });

    this.sucursales.forEach(s => {
      const tieneCola = s.colaIngreso > 0 || s.colaPreparacion > 0 || s.colaSalida > 0;
      const enTransito = this.transferenciasActivas.some(t => t.origen === s.id || t.destino === s.id);
      let color = '#27ae60';
      if (enTransito) color = '#3498db';
      else if (tieneCola) color = '#e74c3c';

      this.ctx!.fillStyle = color;
      this.ctx!.beginPath();
      this.ctx!.arc(s.x, s.y, 30, 0, Math.PI * 2);
      this.ctx!.fill();
      this.ctx!.strokeStyle = 'white';
      this.ctx!.lineWidth = 3;
      this.ctx!.stroke();

      this.ctx!.fillStyle = 'white';
      this.ctx!.font = 'bold 14px Arial';
      this.ctx!.textAlign = 'center';
      this.ctx!.textBaseline = 'middle';
      this.ctx!.fillText(s.id, s.x, s.y - 5);
      this.ctx!.font = '9px Arial';
      this.ctx!.fillText(s.nombre.substring(0, 10), s.x, s.y + 10);
    });

    this.transferenciasActivas.forEach(t => {
      if (t.etapa === 'ENTREGADO') return;
      let posicionX = 0, posicionY = 0;
      let color = '#3498db';

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

      this.ctx!.fillStyle = color;
      this.ctx!.beginPath();
      this.ctx!.arc(posicionX, posicionY, 10, 0, Math.PI * 2);
      this.ctx!.fill();
      this.ctx!.fillStyle = 'white';
      this.ctx!.font = 'bold 7px Arial';
      this.ctx!.fillText(t.producto.substring(0, 5), posicionX, posicionY);
    });
  }

  onVistaChange(): void {
    this.dotSvg = null;
    this.datosArbol = null;
    this.detenerLoop();

    if (this.vistaSeleccionada === 'Grafo') {
      if (this.sucursalesBase.length > 0) {
        this.iniciarGrafo({
          sucursales: this.sucursalesBase,
          conexiones: this.conexiones,
          transferenciasActivas: this.transferenciasActivas
        });
      }
    } else if (this.sucursalSeleccionada) {
      if (this.vistaSeleccionada === 'Hash') {
        this.cargarHash();
      } else {
        this.cargarArbol();
      }
    }
  }

  actualizarGrafo(): void {
    this.cargando = true;
    this.visualizacionService.getEstado().subscribe({
      next: (res: ApiResponse<any>) => {
        this.cargando = false;
        if (res.success && res.data) {
          this.iniciarGrafo(res.data);
          this.error = '';
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

  cargarHash(): void {
    if (!this.sucursalSeleccionada) {
      this.dotSvg = null;
      return;
    }
    this.cargando = true;
    this.visualizacionService.getDotEstructura('hash', this.sucursalSeleccionada).subscribe({
      next: (res: any) => {
        this.cargando = false;
        if (res.success && res.data && res.data.dot) {
          this.datosArbol = res.data;
          this.renderizarDot(res.data.dot);
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.dotSvg = null;
        this.datosArbol = null;
        this.cdr.detectChanges();
      }
    });
  }

  private mapTipoEstructura(tipo: string): string {
    const mapa: Record<string, string> = {
      'B': 'b', 'B+': 'bplus', 'AVL': 'avl', 'Hash': 'hash'
    };
    return mapa[tipo] || tipo.toLowerCase();
  }

  cargarArbol(): void {
    if (!this.sucursalSeleccionada) {
      this.dotSvg = null;
      this.datosArbol = null;
      return;
    }
    this.cargando = true;
    const tipoBackend = this.mapTipoEstructura(this.vistaSeleccionada);
    this.visualizacionService.getDotEstructura(tipoBackend, this.sucursalSeleccionada).subscribe({
      next: (res: any) => {
        this.cargando = false;
        if (res.success && res.data && res.data.dot) {
          this.datosArbol = res.data;
          this.renderizarDot(res.data.dot);
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.dotSvg = null;
        this.datosArbol = null;
        this.cdr.detectChanges();
      }
    });
  }

  private renderizarDot(dotContent: string): void {
    if (!this.viz) {
      vizInstance().then((viz: any) => {
        this.viz = viz;
        this.doRender(dotContent);
      });
      return;
    }
    this.doRender(dotContent);
  }

  private doRender(dotContent: string): void {
    if (!this.viz) return;
    try {
      const result = this.viz.render(dotContent, { format: 'svg' });
      if (result.status === 'success' && result.output) {
        this.dotSvg = result.output;
        this.cdr.detectChanges();
        if (this.dotGraphRef && this.dotGraphRef.nativeElement && this.dotSvg) {
          this.dotGraphRef.nativeElement.innerHTML = this.dotSvg;
        }
      } else {
        this.dotSvg = null;
      }
    } catch (err) {
      this.dotSvg = null;
    }
  }

  getNombreVista(vista: VistaEstructura): string {
    const nombres: Record<VistaEstructura, string> = {
      'B': 'Árbol B',
      'B+': 'Árbol B+',
      'AVL': 'Árbol AVL',
      'Hash': 'Tabla Hash',
      'Grafo': 'Grafo'
    };
    return nombres[vista];
  }

  getNombreSucursal(): string {
    if (!this.sucursalSeleccionada) return '';
    const s = this.sucursalesBase.find(x => x.id === this.sucursalSeleccionada);
    return s ? s.nombre : '';
  }

  formatearDuracion(ms: number): string {
    const segundos = Math.floor(ms / 1000);
    const minutos = Math.floor(segundos / 60);
    const horas = Math.floor(minutos / 60);
    if (horas > 0) return `${horas}h ${minutos % 60}m`;
    if (minutos > 0) return `${minutos}m ${segundos % 60}s`;
    return `${segundos}s`;
  }
}
