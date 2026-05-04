export interface TransferenciaActiva {
  id: string;
  producto: string;
  barcode: string;
  origen: string;
  destino: string;
  ruta: string[];
  etapa: string;
  etapaNombre: string;
  progreso: number;
  sucursalActual: string;
  tramoActualIndex: number;
  tramoProgreso: number;
  tramoDesde?: string;
  tramoHacia?: string;
}

export interface TransferenciaHistorial {
  id: string;
  producto: string;
  barcode: string;
  origen: string;
  destino: string;
  tiempoCompletado: number;
  duracionMs: number;
}

export type EtapaTransferencia = 
  | 'COLA_SALIDA_ORIGEN'
  | 'VIAJE'
  | 'COLA_INGRESO_INTERMEDIA'
  | 'COLA_PREPARACION_INTERMEDIA'
  | 'COLA_SALIDA_INTERMEDIA'
  | 'VIAJE_SALIDA'
  | 'COLA_INGRESO_DESTINO'
  | 'ENTREGADO';

export interface TramoViaje {
  desde: string;
  hacia: string;
  tiempoMinutos: number;
  progreso: number;
}

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