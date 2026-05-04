import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
    <footer class="app-footer">
      <div class="footer-content">
        <span class="footer-text">SuperMarkets - Sistema de Gestión</span>
        <div class="theme-toggle">
          <span class="toggle-label">{{ themeService.darkMode() ? '🌙' : '☀️' }}</span>
          <label class="switch">
            <input type="checkbox" [checked]="themeService.darkMode()" (change)="themeService.toggle()">
            <span class="slider"></span>
          </label>
        </div>
      </div>
    </footer>
  `,
  styles: [`
    .main-content {
      padding: 24px;
      min-height: calc(100vh - 120px);
      background: #f5f6fa;
      transition: background-color 0.3s ease;
    }

    .app-footer {
      background: #2c3e50;
      color: white;
      padding: 12px 24px;
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      z-index: 1000;
    }

    .footer-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      max-width: 1200px;
      margin: 0 auto;
    }

    .footer-text {
      font-size: 12px;
      opacity: 0.8;
    }

    .theme-toggle {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .toggle-label {
      font-size: 16px;
    }

    .switch {
      position: relative;
      display: inline-block;
      width: 40px;
      height: 22px;
    }

    .switch input {
      opacity: 0;
      width: 0;
      height: 0;
    }

    .slider {
      position: absolute;
      cursor: pointer;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: #bdc3c7;
      transition: 0.3s;
      border-radius: 22px;
    }

    .slider:before {
      position: absolute;
      content: "";
      height: 16px;
      width: 16px;
      left: 3px;
      bottom: 3px;
      background-color: white;
      transition: 0.3s;
      border-radius: 50%;
    }

    input:checked + .slider {
      background-color: #3498db;
    }

    input:checked + .slider:before {
      transform: translateX(18px);
    }

    :host-context(.dark-theme) .main-content {
      background: #1a1a2e;
      color: #e0e0e0;
    }

    :host-context(.dark-theme) .app-footer {
      background: #0f0f1a;
      border-top: 1px solid #333;
    }

    :host-context(.dark-theme) .footer-text {
      color: #e0e0e0;
    }

    :host-context(.dark-theme) .slider {
      background-color: #444;
    }
  `]
})
export class AppComponent {
  constructor(public themeService: ThemeService) {}
}