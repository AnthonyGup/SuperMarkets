export interface VisualizacionState {
  sucursales: VisualizacionSucursal[];
  conexiones: VisualizacionConexion[];
  productosEnTransito: ProductoTransito[];
}

export interface VisualizacionSucursal {
  id: string;
  nombre: string;
  x: number;
  y: number;
  colaIngreso: number;
  colaPreparacion: number;
  colaSalida: number;
  estado: 'normal' | 'procesando';
}

export interface VisualizacionConexion {
  origen: string;
  destino: string;
  tiempo: number;
}

export interface ProductoTransito {
  id: string;
  nombre: string;
  origen: string;
  destino: string;
  ruta: string[];
  progreso: number;
  estado: 'ingreso' | 'preparacion' | 'salida' | 'transito';
}

export interface ColaInfo {
  tipo: 'ingreso' | 'preparacion' | 'salida';
  productos: string[];
  tiempoProceso: number;
  posicion: number;
}
