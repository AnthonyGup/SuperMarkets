export interface Producto {
  sucursalId: string;
  name: string;
  barcode: string;
  category: string;
  expiryDate: string;
  brand: string;
  price: number;
  stock: number;
  estado?: 'disponible' | 'en transito' | 'agotado';
}