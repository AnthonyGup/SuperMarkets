package com.supermarkets.api;

import com.supermarkets.pojo.Product;
import com.supermarkets.structures.cola.Cola;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferenciaActiva {
    private final String id;
    private final Product producto;
    private final String origenId;
    private final String destinoId;
    private final List<String> ruta;
    private final List<TramoViaje> tramosViaje;
    private EtapaTransferencia etapa;
    private double progresoGeneral;
    private final long tiempoInicioTransferencia;
    private long etapaInicioMs;
    private long tiempoEsperaInicioMs;
    private String sucursalActualId;
    private int tramoActualIndex;
    private boolean etapaIniciada;

    public TransferenciaActiva(String id, Product producto, String origenId, String destinoId, 
                               List<String> ruta, List<TramoViaje> tramos) {
        this.id = id;
        this.producto = producto;
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.ruta = ruta;
        this.tramosViaje = tramos;
        this.etapa = EtapaTransferencia.COLA_SALIDA_ORIGEN;
        this.progresoGeneral = 0;
        this.tiempoInicioTransferencia = System.currentTimeMillis();
        this.etapaInicioMs = tiempoInicioTransferencia;
        this.tiempoEsperaInicioMs = 0;
        this.sucursalActualId = origenId;
        this.tramoActualIndex = 0;
        this.etapaIniciada = false;
    }

    public String getId() { return id; }
    public Product getProducto() { return producto; }
    public String getOrigenId() { return origenId; }
    public String getDestinoId() { return destinoId; }
    public List<String> getRuta() { return ruta; }
    public EtapaTransferencia getEtapa() { return etapa; }
    public void setEtapa(EtapaTransferencia e) { this.etapa = e; }
    public double getProgresoGeneral() { return progresoGeneral; }
    public void setProgresoGeneral(double p) { this.progresoGeneral = p; }
    public String getSucursalActualId() { return sucursalActualId; }
    public void setSucursalActualId(String id) { this.sucursalActualId = id; }
    public int getTramoActualIndex() { return tramoActualIndex; }
    public void setTramoActualIndex(int i) { this.tramoActualIndex = i; }
    public List<TramoViaje> getTramosViaje() { return tramosViaje; }
    public TramoViaje getTramoActual() {
        return (tramoActualIndex >= 0 && tramoActualIndex < tramosViaje.size()) 
            ? tramosViaje.get(tramoActualIndex) : null;
    }
    public long getEtapaInicioMs() { return etapaInicioMs; }
    public long getTiempoEsperaInicioMs() { return tiempoEsperaInicioMs; }
    public void setTiempoEsperaInicioMs(long ms) { this.tiempoEsperaInicioMs = ms; }
    public void nuevaEtapa() { this.etapaInicioMs = System.currentTimeMillis(); this.etapaIniciada = false; }
    public boolean isEtapaIniciada() { return etapaIniciada; }
    public void iniciarEtapa() { this.etapaIniciada = true; }
    public long getTiempoInicioTransferencia() { return tiempoInicioTransferencia; }

    public boolean esPrimeroEnCola(Cola cola) {
        if (cola.isEmpty()) return false;
        Product primero = cola.peek();
        return primero != null && primero.getBarcode().equals(this.producto.getBarcode());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("producto", producto.getName());
        map.put("barcode", producto.getBarcode());
        map.put("origen", origenId);
        map.put("destino", destinoId);
        map.put("ruta", ruta);
        map.put("etapa", etapa.name());
        map.put("etapaNombre", getNombreEtapa());
        map.put("progreso", progresoGeneral);
        map.put("sucursalActual", sucursalActualId);
        map.put("tramoActualIndex", tramoActualIndex);
        map.put("tramoProgreso", getTramoActual() != null ? getTramoActual().getProgreso() : 0);
        if (getTramoActual() != null) {
            map.put("tramoDesde", getTramoActual().getDesde());
            map.put("tramoHacia", getTramoActual().getHacia());
        }
        return map;
    }

    private String getNombreEtapa() {
        switch (etapa) {
            case COLA_SALIDA_ORIGEN: return "Cola de Salida Origen";
            case VIAJE: return "En Viaje";
            case COLA_INGRESO_INTERMEDIA: return "Cola de Ingreso Intermedia";
            case COLA_PREPARACION_INTERMEDIA: return "Preparacion Intermedia";
            case COLA_SALIDA_INTERMEDIA: return "Cola de Salida Intermedia";
            case VIAJE_SALIDA: return "En Viaje (despues intermediaria)";
            case COLA_INGRESO_DESTINO: return "Cola de Ingreso Destino";
            case ENTREGADO: return "Entregado";
            default: return etapa.name();
        }
    }
}