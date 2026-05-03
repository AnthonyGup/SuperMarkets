export interface TransferenciaRequest {
  origenId: string;
  destinoId: string;
  productoBarcode: string;
  criterio: 'tiempo' | 'costo';
}

export interface RutaResponse {
  origen: string;
  destino: string;
  criterio: string;
  ruta: string[];
  saltos: number;
}

export interface ColaEstado {
  ingreso: number;
  preparacion: number;
  salida: number;
}