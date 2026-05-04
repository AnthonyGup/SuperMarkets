import { Injectable, signal, effect } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly STORAGE_KEY = 'supermarkets-dark-mode';
  
  darkMode = signal<boolean>(this.loadFromStorage());

  constructor() {
    effect(() => {
      this.applyTheme(this.darkMode());
    });
  }

  toggle(): void {
    this.darkMode.update(value => !value);
    this.saveToStorage(this.darkMode());
  }

  private loadFromStorage(): boolean {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      return stored === 'true';
    } catch {
      return false;
    }
  }

  private saveToStorage(value: boolean): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, String(value));
    } catch {}
  }

  private applyTheme(isDark: boolean): void {
    document.body.classList.toggle('dark-theme', isDark);
  }
}