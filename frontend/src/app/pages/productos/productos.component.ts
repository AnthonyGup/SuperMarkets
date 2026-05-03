import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../../services/producto.service';
import { SucursalService } from '../../services/sucursal.service';
import { Producto } from '../../models/producto.model';
import { Sucursal } from '../../models/sucursal.model';
import { ApiResponse } from '../../models/medicion.model';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="productos-page">
      <div class="header">
        <h1>Gestión de Productos</h1>
        <button class="btn-primary" (click)="abrirModal()">+ Nuevo Producto</button>
      </div>

      <div class="filters-bar">
        <select [(ngModel)]="sucursalSeleccionada" (change)="cargarProductos()" class="select-sucursal">
          <option value="">Seleccionar sucursal</option>
          <option *ngFor="let s of sucursales" [value]="s.id">{{ s.nombre }}</option>
        </select>

        <div class="search-box">
          <input type="text" [(ngModel)]="busquedaNombre" placeholder="Buscar producto por nombre...">
          <button class="btn-search" (click)="buscarPorNombre()">Buscar</button>
        </div>
      </div>

      <div class="search-filters">
        <div class="filter-group">
          <label>Código de barras:</label>
          <input type="text" [(ngModel)]="busquedaBarcode" placeholder="Buscar por código de barras...">
          <button class="btn-sm" (click)="buscarPorBarcode()">Buscar</button>
        </div>
        <div class="filter-group">
          <label>Categoría:</label>
          <input type="text" [(ngModel)]="busquedaCategoria" placeholder="Ej: Lácteos, Bebidas...">
          <button class="btn-sm" (click)="buscarPorCategoria()">Buscar</button>
        </div>
        <div class="filter-group">
          <label>Rango de fechas:</label>
          <input type="date" [(ngModel)]="fechaInicio" placeholder="Fecha inicio"> a <input type="date" [(ngModel)]="fechaFin" placeholder="Fecha fin">
          <button class="btn-sm" (click)="buscarPorFecha()">Buscar</button>
        </div>
      </div>

      <div class="productos-table" *ngIf="sucursalSeleccionada">
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Barcode</th>
              <th>Categoría</th>
              <th>Marca</th>
              <th>Precio</th>
              <th>Stock</th>
              <th>Caducidad</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let p of productos">
              <td>{{ p.name }}</td>
              <td>{{ p.barcode }}</td>
              <td>{{ p.category }}</td>
              <td>{{ p.brand }}</td>
              <td>Q{{ p.price | number:'1.2-2' }}</td>
              <td>{{ p.stock }}</td>
              <td>{{ p.expiryDate }}</td>
              <td>
                <button class="btn-edit-sm" (click)="abrirModal(p)">Editar</button>
                <button class="btn-delete-sm" (click)="confirmarEliminar(p)">Eliminar</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="empty-table" *ngIf="productos.length === 0">
          <p>No hay productos en esta sucursal</p>
        </div>
      </div>

      <div class="empty-state" *ngIf="!sucursalSeleccionada">
        <p>Seleccione una sucursal para ver sus productos</p>
      </div>

      <div class="modal" *ngIf="mostrarModal">
        <div class="modal-content">
          <h2>{{ editando ? 'Editar' : 'Nuevo' }} Producto</h2>
          <form (ngSubmit)="guardar()">
<div class="form-row">
            <div class="form-group">
              <label>Nombre</label>
              <input type="text" [(ngModel)]="formulario.name" name="name" required placeholder="Nombre del producto">
            </div>
            <div class="form-group">
              <label>Código de barras</label>
              <input type="text" [(ngModel)]="formulario.barcode" name="barcode" required placeholder="Ej: 7501234567890">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Categoría</label>
              <input type="text" [(ngModel)]="formulario.category" name="category" required placeholder="Ej: Lácteos, Bebidas, etc.">
            </div>
            <div class="form-group">
              <label>Marca</label>
              <input type="text" [(ngModel)]="formulario.brand" name="brand" required placeholder="Ej: PIL, Nestlé, etc.">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Precio (Q)</label>
              <input type="number" step="0.01" [(ngModel)]="formulario.price" name="price" required placeholder="0.00">
            </div>
            <div class="form-group">
              <label>Stock (unidades)</label>
              <input type="number" [(ngModel)]="formulario.stock" name="stock" required placeholder="0">
            </div>
            <div class="form-group">
              <label>Fecha de caducidad</label>
              <input type="date" [(ngModel)]="formulario.expiryDate" name="expiryDate" required placeholder="YYYY-MM-DD">
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
          <p>¿Está seguro de eliminar "{{ productoAEliminar?.name }}"?</p>
          <div class="modal-actions">
            <button class="btn-cancel" (click)="mostrarConfirm = false">Cancelar</button>
            <button class="btn-delete" (click)="eliminar()">Eliminar</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .productos-page { max-width: 1200px; margin: 0 auto; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    h1 { color: #2c3e50; margin: 0; }

    .btn-primary { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; font-weight: 500; }
    .btn-cancel { background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
    .btn-delete { background: #e74c3c; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
    .btn-sm { background: #27ae60; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 13px; }
    .btn-edit-sm { background: #f39c12; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 12px; margin-right: 4px; }
    .btn-delete-sm { background: #e74c3c; color: white; border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 12px; }

    .filters-bar { display: flex; gap: 16px; margin-bottom: 16px; }
    .select-sucursal { padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; min-width: 200px; }
    .search-box { display: flex; gap: 8px; flex: 1; }
    .search-box input { flex: 1; padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; }
    .btn-search { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }

    .search-filters { display: flex; gap: 16px; margin-bottom: 24px; flex-wrap: wrap; }
    .filter-group { display: flex; align-items: center; gap: 8px; }
    .filter-group label { font-size: 13px; color: #555; }
    .filter-group input { padding: 6px 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 13px; }

    .productos-table { background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    table { width: 100%; border-collapse: collapse; }
    th { background: #34495e; color: white; padding: 12px 16px; text-align: left; font-size: 13px; }
    td { padding: 12px 16px; border-bottom: 1px solid #eee; font-size: 14px; }
    tr:hover { background: #f8f9fa; }
    .empty-table { padding: 48px; text-align: center; color: #7f8c8d; }

    .empty-state { text-align: center; padding: 48px; background: white; border-radius: 12px; color: #7f8c8d; }

    .modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
    .modal-content { background: white; padding: 24px; border-radius: 12px; width: 100%; max-width: 600px; }
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
    .form-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
    .form-row:has(.form-group:only-child) { grid-template-columns: 1fr; }
    .modal-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 20px; }

    .search-box input::placeholder { color: #aaa; font-style: italic; }
    .filter-group input::placeholder { color: #aaa; font-style: italic; }
    .filter-group input:hover, .filter-group select:hover { border-color: #3498db; }
  `]
})
export class ProductosComponent implements OnInit {
  sucursales: Sucursal[] = [];
  productos: Producto[] = [];
  sucursalSeleccionada = '';
  loading = false;
  
  mostrarModal = false;
  mostrarConfirm = false;
  editando = false;
  productoAEliminar: Producto | null = null;

  busquedaNombre = '';
  busquedaBarcode = '';
  busquedaCategoria = '';
  fechaInicio = '';
  fechaFin = '';

  formulario: any = {
    name: '', barcode: '', category: '', brand: '',
    price: 0, stock: 0, expiryDate: ''
  };

  constructor(
    private readonly productoService: ProductoService,
    private readonly sucursalService: SucursalService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarSucursales();
  }

  cargarSucursales(): void {
    this.sucursalService.getAll().subscribe({
      next: (res: ApiResponse<Sucursal[]>) => {
        this.sucursales = res.data || [];
        this.cdr.detectChanges();
      }
    });
  }

  cargarProductos(): void {
    if (!this.sucursalSeleccionada) return;
    this.loading = true;
    this.productoService.getBySucursal(this.sucursalSeleccionada).subscribe({
      next: (res: ApiResponse<Producto[]>) => {
        this.productos = res.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.productos = [];
        this.cdr.detectChanges();
      }
    });
  }

  buscarPorNombre(): void {
    if (!this.sucursalSeleccionada || !this.busquedaNombre) return;
    this.loading = true;
    this.productoService.buscar({ sucursalId: this.sucursalSeleccionada, nombre: this.busquedaNombre }).subscribe({
      next: (res: ApiResponse<Producto[]>) => {
        this.productos = res.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscarPorBarcode(): void {
    if (!this.sucursalSeleccionada || !this.busquedaBarcode) return;
    this.loading = true;
    this.productoService.buscar({ sucursalId: this.sucursalSeleccionada, barcode: this.busquedaBarcode }).subscribe({
      next: (res: ApiResponse<Producto[]>) => {
        this.productos = res.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscarPorCategoria(): void {
    if (!this.sucursalSeleccionada || !this.busquedaCategoria) return;
    this.loading = true;
    this.productoService.buscar({ sucursalId: this.sucursalSeleccionada, categoria: this.busquedaCategoria }).subscribe({
      next: (res: ApiResponse<Producto[]>) => {
        this.productos = res.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscarPorFecha(): void {
    if (!this.sucursalSeleccionada || !this.fechaInicio || !this.fechaFin) return;
    this.loading = true;
    this.productoService.buscar({ sucursalId: this.sucursalSeleccionada, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin }).subscribe({
      next: (res: ApiResponse<Producto[]>) => {
        this.productos = res.data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  abrirModal(producto?: Producto): void {
    if (producto) {
      this.editando = true;
      this.formulario = { ...producto };
    } else {
      this.editando = false;
      this.formulario = { name: '', barcode: '', category: '', brand: '', price: 0, stock: 0, expiryDate: '' };
    }
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  guardar(): void {
    const producto: Producto = { ...this.formulario, sucursalId: this.sucursalSeleccionada };
    if (this.editando) {
      this.productoService.create(producto).subscribe({
        next: () => { this.cerrarModal(); this.cargarProductos(); }
      });
    } else {
      this.productoService.create(producto).subscribe({
        next: () => { this.cerrarModal(); this.cargarProductos(); }
      });
    }
  }

  confirmarEliminar(producto: Producto): void {
    this.productoAEliminar = producto;
    this.mostrarConfirm = true;
  }

  eliminar(): void {
    if (this.productoAEliminar) {
      this.productoService.delete(this.productoAEliminar.name, this.sucursalSeleccionada).subscribe({
        next: () => {
          this.mostrarConfirm = false;
          this.productoAEliminar = null;
          this.cargarProductos();
        }
      });
    }
  }
}