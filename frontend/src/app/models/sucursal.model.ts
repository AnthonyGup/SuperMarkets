export interface Sucursal {
  id: string;
  nombre: string;
  ubicacion: string;
  tIngreso: number;
  tTraspaso: number;
  tDespacho: number;
  totalProductos?: number;
  colaIngresoSize?: number;
  colaPreparacionSize?: number;
  colaSalidaSize?: number;
}