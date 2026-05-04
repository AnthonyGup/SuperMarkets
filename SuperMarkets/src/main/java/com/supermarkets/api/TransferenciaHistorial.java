package com.supermarkets.api;

public class TransferenciaHistorial {
    private final String id;
    private final String productoNombre;
    private final String barcode;
    private final String origen;
    private final String destino;
    private final long tiempoCompletado;
    private final long duracionMs;

    public TransferenciaHistorial(String id, String productoNombre, String barcode, 
                                  String origen, String destino, long duracionMs) {
        this.id = id;
        this.productoNombre = productoNombre;
        this.barcode = barcode;
        this.origen = origen;
        this.destino = destino;
        this.tiempoCompletado = System.currentTimeMillis();
        this.duracionMs = duracionMs;
    }

    public String getId() { return id; }
    public String getProductoNombre() { return productoNombre; }
    public String getBarcode() { return barcode; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public long getTiempoCompletado() { return tiempoCompletado; }
    public long getDuracionMs() { return duracionMs; }
}