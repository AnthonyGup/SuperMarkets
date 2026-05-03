export interface ResultadoMedicion {
  estructura: string;
  operacion: string;
  tiempoMs: number;
  tiempoPromedioMs: number;
  cantidadElementos: number;
  iteraciones: number;
  complejidad: string;
  claveBusqueda: string;
  detalles: string[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  tiempoMs: number;
}