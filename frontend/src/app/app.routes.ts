import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SucursalesComponent } from './pages/sucursales/sucursales.component';
import { ProductosComponent } from './pages/productos/productos.component';
import { TransferenciaComponent } from './pages/transferencia/transferencia.component';
import { MedicionComponent } from './pages/medicion/medicion.component';
import { CargaCsvComponent } from './pages/carga-csv/carga-csv.component';
import { VisualizacionComponent } from './pages/visualizacion/visualizacion.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'sucursales', component: SucursalesComponent },
  { path: 'productos', component: ProductosComponent },
  { path: 'transferencia', component: TransferenciaComponent },
  { path: 'medicion', component: MedicionComponent },
  { path: 'carga-csv', component: CargaCsvComponent },
  { path: 'visualizacion', component: VisualizacionComponent },
  { path: '**', redirectTo: '' }
];
