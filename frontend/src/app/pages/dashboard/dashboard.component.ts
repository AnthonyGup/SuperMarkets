import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SucursalService } from '../../services/sucursal.service';
import { ProductoService } from '../../services/producto.service';
import { TransferenciaService } from '../../services/transferencia.service';
import { Sucursal } from '../../models/sucursal.model';
import { ApiResponse } from '../../models/medicion.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard">
      <h1>Dashboard</h1>
      
      <div class="loading" *ngIf="loading">
        <p>Cargando datos...</p>
      </div>

      <div class="stats-grid" *ngIf="!loading">
        <div class="stat-card">
          <div class="stat-icon blue">
            <span>S</span>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalSucursales }}</span>
            <span class="stat-label">Sucursales</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon green">
            <span>P</span>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ totalProductos }}</span>
            <span class="stat-label">Productos</span>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon orange">
            <span>T</span>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ transferenciasActivas }}</span>
            <span class="stat-label">Transferencias Activas</span>
          </div>
        </div>
      </div>

      <div class="quick-actions">
        <h2>Acciones Rápidas</h2>
        <div class="actions-grid">
          <a routerLink="/sucursales" class="action-card">
            <div class="action-icon">+</div>
            <span>Nueva Sucursal</span>
          </a>
          <a routerLink="/productos" class="action-card">
            <div class="action-icon">P</div>
            <span>Gestionar Productos</span>
          </a>
          <a routerLink="/transferencia" class="action-card">
            <div class="action-icon">T</div>
            <span>Transferir Producto</span>
          </a>
          <a routerLink="/medicion" class="action-card">
            <div class="action-icon">M</div>
            <span>Medir Rendimiento</span>
          </a>
        </div>
      </div>

      <div class="sucursales-preview" *ngIf="sucursales.length > 0">
        <h2>Sucursales Recientes</h2>
        <div class="sucursales-list">
          <div class="sucursal-item" *ngFor="let s of sucursales.slice(0, 5)">
            <div class="sucursal-info">
              <strong>{{ s.nombre }}</strong>
              <span>{{ s.ubicacion }}</span>
            </div>
            <span class="sucursal-id">ID: {{ s.id }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard { max-width: 1200px; margin: 0 auto; }
    h1 { margin-bottom: 24px; color: #2c3e50; }
    h2 { margin: 24px 0 16px; color: #34495e; font-size: 18px; }
    
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }
    .stat-card { background: white; padding: 20px; border-radius: 12px; display: flex; align-items: center; gap: 16px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .stat-icon { width: 50px; height: 50px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 20px; }
    .stat-icon.blue { background: #3498db; }
    .stat-icon.green { background: #27ae60; }
    .stat-icon.orange { background: #e67e22; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 28px; font-weight: bold; color: #2c3e50; }
    .stat-label { color: #7f8c8d; font-size: 14px; }

    .actions-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 16px; }
    .action-card { background: white; padding: 24px; border-radius: 12px; display: flex; flex-direction: column; align-items: center; gap: 12px; text-decoration: none; color: #2c3e50; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: transform 0.2s, box-shadow 0.2s; }
    .action-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.12); }
    .action-icon { width: 48px; height: 48px; background: #3498db; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; font-size: 20px; }
    .action-card span { font-weight: 500; }

    .sucursales-list { display: flex; flex-direction: column; gap: 8px; }
    .sucursal-item { background: white; padding: 16px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
    .sucursal-info { display: flex; flex-direction: column; }
    .sucursal-info strong { color: #2c3e50; }
    .sucursal-info span { color: #7f8c8d; font-size: 13px; }
    .sucursal-id { color: #95a5a6; font-size: 12px; }
  `]
})
export class DashboardComponent implements OnInit {
  sucursales: Sucursal[] = [];
  totalSucursales = 0;
  totalProductos = 0;
  transferenciasActivas = 0;
  loading = true;

  constructor(
    private readonly sucursalService: SucursalService,
    private readonly productoService: ProductoService,
    private readonly transferenciaService: TransferenciaService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    this.sucursalService.getAll().subscribe({
      next: (res: ApiResponse<Sucursal[]>) => {
        this.sucursales = res.data || [];
        this.totalSucursales = this.sucursales.length;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.totalSucursales = 0;
        this.cdr.detectChanges();
      }
    });
  }
}