export interface ResultadoMedicion {
  estructura: string;
  operacion: string;
  tiempoMs: number;
  cantidadElementos: number;
  detalles?: string[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  tiempoMs: number;
}