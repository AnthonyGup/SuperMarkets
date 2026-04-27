import { CommonModule } from '@angular/common';
import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CsvUploadResponse, CsvUploadService } from './services/csv-upload.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  title = 'SuperMarkets';
  selectedFileName = 'Sin archivo seleccionado';
  selectedFile: File | null = null;
  hasHeader = false;
  isUploading = false;
  alertType: 'success' | 'danger' | 'info' | '' = 'info';
  alertMessage = 'Selecciona un archivo CSV';
  uploadErrors: string[] = [];
  uploadStats: CsvUploadResponse['stats'] | null = null;

  private readonly csvUploadService = inject(CsvUploadService);
  private readonly cd = inject(ChangeDetectorRef);

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length > 0 ? input.files[0] : null;

    this.selectedFile = file;
    this.selectedFileName = file ? file.name : 'Sin archivo seleccionado';
    this.alertType = 'info';
    this.alertMessage = file ? `Listo: ${file.name}` : 'Selecciona un archivo CSV';
    this.uploadErrors = [];
    this.uploadStats = null;

    this.cd.detectChanges();
  }

  onHeaderChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.hasHeader = input.checked;
    this.uploadStats = null;

    this.cd.detectChanges();
  }

  uploadCsv(): void {
    if (!this.selectedFile) {
      this.alertType = 'danger';
      this.alertMessage = 'Selecciona un archivo CSV.';
      this.cd.detectChanges();
      return;
    }

    this.isUploading = true;
    this.alertType = 'info';
    this.alertMessage = 'Procesando...';
    this.uploadErrors = [];
    this.uploadStats = null;

    this.cd.detectChanges();

    this.csvUploadService.upload(this.selectedFile, this.hasHeader).subscribe({
      next: (response) => {
        this.isUploading = false;
        this.alertType = response.success ? 'success' : 'danger';
        this.alertMessage = response.message;
        this.uploadErrors = response.errors ?? [];
        this.uploadStats = response.stats ?? null;

        this.cd.detectChanges();
      },
      error: () => {
        this.isUploading = false;
        this.alertType = 'danger';
        this.alertMessage = 'Error de conexión con el servidor.';
        this.uploadErrors = [];
        this.uploadStats = null;

        this.cd.detectChanges();
      }
    });
  }
}