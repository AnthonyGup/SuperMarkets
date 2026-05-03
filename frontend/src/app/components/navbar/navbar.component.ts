import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <div class="nav-brand">
        <span class="brand-icon">S</span>
        <span class="brand-text">SuperMarkets</span>
      </div>
      <ul class="nav-links">
        <li><a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Dashboard</a></li>
        <li><a routerLink="/carga-csv" routerLinkActive="active">Cargar CSV</a></li>
        <li><a routerLink="/sucursales" routerLinkActive="active">Sucursales</a></li>
        <li><a routerLink="/productos" routerLinkActive="active">Productos</a></li>
        <li><a routerLink="/transferencia" routerLinkActive="active">Transferencia</a></li>
        <li><a routerLink="/medicion" routerLinkActive="active">Medición</a></li>
        <li><a routerLink="/visualizacion" routerLinkActive="active">Visualización</a></li>
      </ul>
    </nav>
  `,
  styles: [`
    .navbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: #2c3e50;
      padding: 0 24px;
      height: 60px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    }
    .nav-brand {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .brand-icon {
      width: 36px;
      height: 36px;
      background: #3498db;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: bold;
      font-size: 18px;
    }
    .brand-text {
      color: white;
      font-weight: 600;
      font-size: 18px;
    }
    .nav-links {
      display: flex;
      list-style: none;
      margin: 0;
      padding: 0;
      gap: 8px;
    }
    .nav-links a {
      color: rgba(255,255,255,0.8);
      text-decoration: none;
      padding: 8px 16px;
      border-radius: 6px;
      transition: all 0.2s;
      font-size: 14px;
    }
    .nav-links a:hover {
      background: rgba(255,255,255,0.1);
      color: white;
    }
    .nav-links a.active {
      background: #3498db;
      color: white;
    }
  `]
})
export class NavbarComponent {}