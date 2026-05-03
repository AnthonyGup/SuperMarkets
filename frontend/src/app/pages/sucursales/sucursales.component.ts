import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SucursalService } from '../../services/sucursal.service';
import { Sucursal } from '../../models/sucursal.model';
import { ApiResponse } from '../../models/medicion.model';

@Component({
  selector: 'app-sucursales',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="sucursales-page">
      <div class="header">
        <h1>Sucursales</h1>
        <button class="btn-primary" (click)="cargarSucursales(); abrirModal()">+ Nueva Sucursal</button>
      </div>

      <div class="loading" *ngIf="loading">
        <p>Cargando sucursales...</p>
      </div>

      <div class="sucursales-grid" *ngIf="!loading && sucursales.length > 0">
        <div class="sucursal-card" *ngFor="let s of sucursales">
          <div class="card-header">
            <div class="card-icon">S</div>
            <div class="card-title">
              <h3>{{ s.nombre }}</h3>
              <span class="card-id">ID: {{ s.id }}</span>
            </div>
          </div>
          <div class="card-body">
            <p><strong>Ubicación:</strong> {{ s.ubicacion }}</p>
            <p><strong>T. Ingreso:</strong> {{ s.tIngreso }} min</p>
            <p><strong>T. Traslado:</strong> {{ s.tTraspaso }} min</p>
            <p><strong>T. Despacho:</strong> {{ s.tDespacho }} min</p>
          </div>
          <div class="card-actions">
            <button class="btn-edit" (click)="abrirModal(s)">Editar</button>
            <button class="btn-delete" (click)="confirmarEliminar(s)">Eliminar</button>
          </div>
        </div>
      </div>

      <div class="empty-state" *ngIf="!loading && sucursales.length === 0">
        <p>No hay sucursales registradas</p>
        <p class="hint">Carga un archivo CSV con sucursales o crea una nueva.</p>
        <button class="btn-primary" (click)="abrirModal()">Crear primera sucursal</button>
      </div>

      <div class="modal" *ngIf="mostrarModal">
        <div class="modal-content">
          <h2>{{ editando ? 'Editar' : 'Nueva' }} Sucursal</h2>
          <form (ngSubmit)="guardar()">
            <div class="form-group">
              <label>ID</label>
              <input type="text" [(ngModel)]="formulario.id" name="id" [disabled]="editando" required placeholder="Ej: SUC001">
            </div>
            <div class="form-group">
              <label>Nombre</label>
              <input type="text" [(ngModel)]="formulario.nombre" name="nombre" required placeholder="Nombre de la sucursal">
            </div>
            <div class="form-group">
              <label>Ubicación</label>
              <input type="text" [(ngModel)]="formulario.ubicacion" name="ubicacion" required placeholder="Dirección o ubicación">
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>T. Ingreso (min)</label>
                <input type="number" [(ngModel)]="formulario.tIngreso" name="tIngreso" required placeholder="Ej: 10">
              </div>
              <div class="form-group">
                <label>T. Traslado (min)</label>
                <input type="number" [(ngModel)]="formulario.tTraspaso" name="tTraspaso" required placeholder="Ej: 5">
              </div>
              <div class="form-group">
                <label>T. Despacho (min)</label>
                <input type="number" [(ngModel)]="formulario.tDespacho" name="tDespacho" required placeholder="Ej: 15">
              </div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-cancel" (click)="cerrarModal()">Cancelar</button>
              <button type="submit" class="btn-primary">Guardar</button>
            </div>
          </form>
        </div>
      </div>

      <div class="modal" *ngIf="mostrarConfirm">
        <div class="modal-content modal-sm">
          <h3>Confirmar eliminación</h3>
          <p>¿Está seguro de eliminar la sucursal "{{ sucursalAEliminar?.nombre }}"?</p>
          <div class="modal-actions">
            <button class="btn-cancel" (click)="mostrarConfirm = false">Cancelar</button>
            <button class="btn-delete" (click)="eliminar()">Eliminar</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .sucursales-page { max-width: 1200px; margin: 0 auto; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    h1 { color: #2c3e50; margin: 0; }
    
    .btn-primary { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500; }
    .btn-primary:hover { background: #2980b9; }
    .btn-cancel { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
    .btn-edit { background: #f39c12; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; }
    .btn-delete { background: #e74c3c; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; }

    .sucursales-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
    .sucursal-card { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .card-header { display: flex; align-items: center; gap: 12px; padding: 16px; background: #f8f9fa; border-bottom: 1px solid #eee; }
    .card-icon { width: 40px; height: 40px; background: #3498db; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold; }
    .card-title h3 { margin: 0; color: #2c3e50; font-size: 16px; }
    .card-id { font-size: 12px; color: #95a5a6; }
    .card-body { padding: 16px; }
    .card-body p { margin: 8px 0; font-size: 14px; color: #555; }
    .card-body strong { color: #2c3e50; }
    .card-actions { padding: 12px 16px; background: #f8f9fa; display: flex; gap: 8px; justify-content: flex-end; }

    .empty-state { text-align: center; padding: 48px; background: white; border-radius: 12px; }
    .empty-state p { color: #7f8c8d; margin-bottom: 16px; }

    .modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; padding: 24px; border-radius: 12px; width: 100%; max-width: 500px; }
    .modal-content h2 { margin: 0 0 20px; color: #2c3e50; }
    .modal-sm { max-width: 400px; }
    .modal-sm h3 { margin: 0 0 12px; color: #2c3e50; }
    .modal-sm p { color: #555; margin-bottom: 20px; }

    .form-group { margin-bottom: 16px; }
    .form-group label { display: block; margin-bottom: 6px; font-weight: 500; color: #2c3e50; font-size: 14px; }
    .form-group input, .form-group select { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; box-sizing: border-box; background: #fafafa; }
    .form-group input::placeholder { color: #aaa; font-style: italic; }
    .form-group input:hover, .form-group select:hover { border-color: #3498db; }
    .form-group input:focus, .form-group select:focus { outline: none; border-color: #3498db; box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1); background: white; }
    .form-group input:disabled { background: #f5f5f5; color: #999; }
    .form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }

    .modal-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 20px; }
    .loading { text-align: center; padding: 40px; color: #7f8c8d; }
    .hint { color: #95a5a6; font-size: 14px; margin-bottom: 16px; }
  `]
})
export class SucursalesComponent implements OnInit {
  sucursales: Sucursal[] = [];
  loading = false;
  mostrarModal = false;
  mostrarConfirm = false;
  editando = false;
  sucursalAEliminar: Sucursal | null = null;

  formulario: any = {
    id: '',
    nombre: '',
    ubicacion: '',
    tIngreso: 10,
    tTraspaso: 5,
    tDespacho: 15
  };

  constructor(
    private readonly sucursalService: SucursalService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarSucursales();
  }

  cargarSucursales(): void {
    this.loading = true;
    this.sucursalService.getAll().subscribe({
      next: (res: ApiResponse<Sucursal[]>) => {
        this.sucursales = res.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.sucursales = [];
        this.cdr.detectChanges();
      }
    });
  }

  abrirModal(sucursal?: Sucursal): void {
    if (sucursal) {
      this.editando = true;
      this.formulario = { ...sucursal };
    } else {
      this.editando = false;
      this.formulario = { id: '', nombre: '', ubicacion: '', tIngreso: 10, tTraspaso: 5, tDespacho: 15 };
    }
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.formulario = { id: '', nombre: '', ubicacion: '', tIngreso: 10, tTraspaso: 5, tDespacho: 15 };
  }

  guardar(): void {
    if (this.editando) {
      this.sucursalService.update(this.formulario.id, this.formulario).subscribe({
        next: () => {
          this.cerrarModal();
          this.cargarSucursales();
        }
      });
    } else {
      this.sucursalService.create(this.formulario).subscribe({
        next: () => {
          this.cerrarModal();
          this.cargarSucursales();
        }
      });
    }
  }

  confirmarEliminar(sucursal: Sucursal): void {
    this.sucursalAEliminar = sucursal;
    this.mostrarConfirm = true;
  }

  eliminar(): void {
    if (this.sucursalAEliminar) {
      this.sucursalService.delete(this.sucursalAEliminar.id).subscribe({
        next: () => {
          this.mostrarConfirm = false;
          this.sucursalAEliminar = null;
          this.cargarSucursales();
        }
      });
    }
  }
}