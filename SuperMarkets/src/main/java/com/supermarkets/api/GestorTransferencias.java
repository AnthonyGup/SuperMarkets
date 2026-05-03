package com.supermarkets.api;

import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;
import com.supermarkets.structures.grafo.Grafo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GestorTransferencias {
    public enum EtapaTransferencia {
        COLA_PREPARACION_ORIGEN,
        COLA_SALIDA_ORIGEN,
        VIAJE,
        COLA_INGRESO_INTERMEDIA,
        COLA_PREPARACION_INTERMEDIA,
        COLA_SALIDA_INTERMEDIA,
        VIAJE_SALIDA,
        COLA_INGRESO_DESTINO,
        ENTREGADO
    }

    public static class TramoViaje {
        private final String desde;
        private final String hacia;
        private final double tiempoMinutos;
        private double progreso;

        public TramoViaje(String desde, String hacia, double tiempoMinutos) {
            this.desde = desde;
            this.hacia = hacia;
            this.tiempoMinutos = tiempoMinutos;
            this.progreso = 0;
        }

        public String getDesde() { return desde; }
        public String getHacia() { return hacia; }
        public double getTiempoMinutos() { return tiempoMinutos; }
        public double getProgreso() { return progreso; }
        public void setProgreso(double p) { this.progreso = p; }
    }

    public static class TransferenciaActiva {
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
            this.etapa = EtapaTransferencia.COLA_PREPARACION_ORIGEN;
            this.progresoGeneral = 0;
            this.tiempoInicioTransferencia = System.currentTimeMillis();
            this.etapaInicioMs = tiempoInicioTransferencia;
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
        public void nuevaEtapa() { this.etapaInicioMs = System.currentTimeMillis(); this.etapaIniciada = false; }
        public boolean isEtapaIniciada() { return etapaIniciada; }
        public void iniciarEtapa() { this.etapaIniciada = true; }

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
                case COLA_PREPARACION_ORIGEN: return "Preparación en Origen";
                case COLA_SALIDA_ORIGEN: return "En Cola de Salida Origen";
                case VIAJE: return "En Viaje";
                case COLA_INGRESO_INTERMEDIA: return "Cola de Ingreso Intermedia";
                case COLA_PREPARACION_INTERMEDIA: return "Preparación Intermedia";
                case COLA_SALIDA_INTERMEDIA: return "Cola de Salida Intermedia";
                case VIAJE_SALIDA: return "En Viaje (después intermediaria)";
                case COLA_INGRESO_DESTINO: return "Cola de Ingreso Destino";
                case ENTREGADO: return "Entregado";
                default: return etapa.name();
            }
        }
    }

    public static class TransferenciaHistorial {
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

    private static GestorTransferencias instancia;
    private final Map<String, TransferenciaActiva> transferenciasActivas;
    private final List<TransferenciaHistorial> historial;
    private final GestorCentral gestor;

    private GestorTransferencias() {
        this.transferenciasActivas = new ConcurrentHashMap<>();
        this.historial = Collections.synchronizedList(new ArrayList<>());
        this.gestor = GestorCentral.getInstancia();
        iniciarHiloProcesamiento();
    }

    public static synchronized GestorTransferencias getInstancia() {
        if (instancia == null) {
            instancia = new GestorTransferencias();
        }
        return instancia;
    }

    public String iniciarTransferencia(String origenId, String destinoId, Product producto) {
        if (producto == null || origenId == null || destinoId == null) {
            return null;
        }

        List<String> ruta = gestor.calcularRuta(origenId, destinoId);
        if (ruta == null || ruta.isEmpty()) {
            return null;
        }

        Sucursal origen = gestor.getSucursal(origenId);
        if (origen == null) {
            return null;
        }

        origen.eliminarProducto(producto.getName());
        producto.setEstado(Product.Estado.EN_TRANSITO);
        origen.agregarAPreparacion(producto);

        List<TramoViaje> tramos = calcularTramosViaje(ruta);

        String transferId = UUID.randomUUID().toString().substring(0, 8);
        TransferenciaActiva transferencia = new TransferenciaActiva(
            transferId, producto, origenId, destinoId, ruta, tramos
        );

        transferenciasActivas.put(transferId, transferencia);

        return transferId;
    }

    private List<TramoViaje> calcularTramosViaje(List<String> ruta) {
        List<TramoViaje> tramos = new ArrayList<>();
        Grafo grafo = gestor.getRedSucursales();

        for (int i = 0; i < ruta.size() - 1; i++) {
            String desde = ruta.get(i);
            String hacia = ruta.get(i + 1);
            float tiempo = grafo.obtenerPeso(desde, hacia);
            if (tiempo <= 0) {
                tiempo = 5;
            }
            tramos.add(new TramoViaje(desde, hacia, tiempo));
        }

        return tramos;
    }

    public List<TransferenciaActiva> getTransferenciasActivas() {
        return new ArrayList<>(transferenciasActivas.values());
    }

    public List<TransferenciaHistorial> getHistorial() {
        return new ArrayList<>(historial);
    }

    private void iniciarHiloProcesamiento() {
        Thread hilo = new Thread(() -> {
            while (true) {
                try {
                    procesarTransferencias();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        hilo.setDaemon(true);
        hilo.setName("GestorTransferencias-Processor");
        hilo.start();
    }

    private void procesarTransferencias() {
        List<String> idsCompletadas = new ArrayList<>();

        for (Map.Entry<String, TransferenciaActiva> entry : transferenciasActivas.entrySet()) {
            TransferenciaActiva t = entry.getValue();
            boolean completada = procesarEtapa(t);
            if (completada) {
                idsCompletadas.add(entry.getKey());
            }
        }

        for (String id : idsCompletadas) {
            TransferenciaActiva t = transferenciasActivas.remove(id);
            if (t != null) {
                long duracion = System.currentTimeMillis() - t.tiempoInicioTransferencia;
                historial.add(new TransferenciaHistorial(
                    t.getId(),
                    t.getProducto().getName(),
                    t.getProducto().getBarcode(),
                    t.getOrigenId(),
                    t.getDestinoId(),
                    duracion
                ));
            }
        }
    }

    private boolean procesarEtapa(TransferenciaActiva t) {
        Sucursal sucursal;
        Product producto = t.getProducto();
        long ahora = System.currentTimeMillis();
        long elapsedEnEtapa = ahora - t.getEtapaInicioMs();

        switch (t.getEtapa()) {
            case COLA_PREPARACION_ORIGEN:
                sucursal = gestor.getSucursal(t.getOrigenId());
                if (sucursal != null && !sucursal.getColaPreparacion().isEmpty()) {
                    producto = sucursal.getColaPreparacion().peek();
                    if (producto != null && producto.getBarcode().equals(t.getProducto().getBarcode())) {
                        if (!t.isEtapaIniciada()) {
                            t.iniciarEtapa();
                        }
                        long esperaMs = (long) (sucursal.gettTraspaso() * 60 * 1000);
                        double progreso = Math.min(elapsedEnEtapa / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.02 + progreso * 0.08);

                        if (progreso >= 1.0) {
                            sucursal.getColaPreparacion().pop();
                            sucursal.agregarASalida(producto);
                            t.setEtapa(EtapaTransferencia.COLA_SALIDA_ORIGEN);
                            t.nuevaEtapa();
                            t.setProgresoGeneral(0.10);
                        }
                    }
                }
                break;

            case COLA_SALIDA_ORIGEN:
                sucursal = gestor.getSucursal(t.getOrigenId());
                if (sucursal != null && !sucursal.getColaSalida().isEmpty()) {
                    producto = sucursal.getColaSalida().peek();
                    if (producto != null && producto.getBarcode().equals(t.getProducto().getBarcode())) {
                        if (!t.isEtapaIniciada()) {
                            t.iniciarEtapa();
                        }
                        long esperaMs = (long) (sucursal.gettDespacho() * 60 * 1000);
                        double progreso = Math.min(elapsedEnEtapa / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.10 + progreso * 0.10);

                        if (progreso >= 1.0) {
                            sucursal.getColaSalida().pop();
                            t.setEtapa(EtapaTransferencia.VIAJE);
                            t.nuevaEtapa();
                            t.setTramoActualIndex(0);
                            t.setProgresoGeneral(0.20);
                        }
                    }
                }
                break;

            case VIAJE:
            case VIAJE_SALIDA:
                if (t.getTramoActualIndex() >= t.getTramosViaje().size()) {
                    t.setEtapa(EtapaTransferencia.COLA_INGRESO_DESTINO);
                    t.nuevaEtapa();
                    return false;
                }

                TramoViaje tramo = t.getTramoActual();
                if (tramo != null) {
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                    }

                    long tiempoTramoMs = (long) (tramo.getTiempoMinutos() * 60 * 1000);
                    double progresoTramo = Math.min(elapsedEnEtapa / (double) tiempoTramoMs, 1.0);
                    tramo.setProgreso(progresoTramo);

                    int numTramos = t.getTramosViaje().size();
                    double progresoBase = 0.20 + (0.45 * t.getTramoActualIndex() / numTramos);
                    double progresoActual = progresoBase + (0.45 / numTramos) * progresoTramo;
                    t.setProgresoGeneral(Math.min(progresoActual, 0.95));

                    if (progresoTramo >= 1.0) {
                        String siguienteId = tramo.getHacia();
                        t.setSucursalActualId(siguienteId);

                        int siguienteTramoIdx = t.getTramoActualIndex() + 1;

                        if (siguienteTramoIdx < numTramos) {
                            t.setEtapa(EtapaTransferencia.COLA_INGRESO_INTERMEDIA);
                            t.setProgresoGeneral(0.65);
                        } else {
                            t.setEtapa(EtapaTransferencia.COLA_INGRESO_DESTINO);
                            t.setProgresoGeneral(0.65);
                        }
                        t.nuevaEtapa();
                    }
                }
                break;

            case COLA_INGRESO_INTERMEDIA:
                sucursal = gestor.getSucursal(t.getSucursalActualId());
                if (sucursal != null) {
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarAIngreso(producto);
                    }
                    long esperaMs = (long) (sucursal.gettIngreso() * 60 * 1000);
                    double progreso = Math.min(elapsedEnEtapa / (double) esperaMs, 1.0);
                    t.setProgresoGeneral(0.65 + progreso * 0.05);

                    if (progreso >= 1.0) {
                        if (!sucursal.getColaIngreso().isEmpty()) {
                            sucursal.getColaIngreso().pop();
                        }
                        t.setEtapa(EtapaTransferencia.COLA_PREPARACION_INTERMEDIA);
                        t.nuevaEtapa();
                        t.setProgresoGeneral(0.70);
                    }
                }
                break;

            case COLA_PREPARACION_INTERMEDIA:
                sucursal = gestor.getSucursal(t.getSucursalActualId());
                if (sucursal != null) {
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarAPreparacion(producto);
                    }
                    long esperaMs = (long) (sucursal.gettTraspaso() * 60 * 1000);
                    double progreso = Math.min(elapsedEnEtapa / (double) esperaMs, 1.0);
                    t.setProgresoGeneral(0.70 + progreso * 0.05);

                    if (progreso >= 1.0) {
                        if (!sucursal.getColaPreparacion().isEmpty()) {
                            sucursal.getColaPreparacion().pop();
                        }
                        t.setEtapa(EtapaTransferencia.COLA_SALIDA_INTERMEDIA);
                        t.nuevaEtapa();
                        t.setProgresoGeneral(0.75);
                    }
                }
                break;

            case COLA_SALIDA_INTERMEDIA:
                sucursal = gestor.getSucursal(t.getSucursalActualId());
                if (sucursal != null) {
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarASalida(producto);
                    }
                    long esperaMs = (long) (sucursal.gettDespacho() * 60 * 1000);
                    double progreso = Math.min(elapsedEnEtapa / (double) esperaMs, 1.0);
                    t.setProgresoGeneral(0.75 + progreso * 0.05);

                    if (progreso >= 1.0) {
                        if (!sucursal.getColaSalida().isEmpty()) {
                            sucursal.getColaSalida().pop();
                        }
                        t.setTramoActualIndex(t.getTramoActualIndex() + 1);
                        t.setEtapa(EtapaTransferencia.VIAJE_SALIDA);
                        t.nuevaEtapa();
                        t.setProgresoGeneral(0.80);
                    }
                }
                break;

            case COLA_INGRESO_DESTINO:
                sucursal = gestor.getSucursal(t.getDestinoId());
                if (sucursal != null) {
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarAIngreso(producto);
                    }
                    long esperaMs = (long) (sucursal.gettIngreso() * 60 * 1000);
                    double progreso = Math.min(elapsedEnEtapa / (double) esperaMs, 1.0);
                    t.setProgresoGeneral(0.80 + progreso * 0.15);

                    if (progreso >= 1.0) {
                        if (!sucursal.getColaIngreso().isEmpty()) {
                            sucursal.getColaIngreso().pop();
                        }
                        producto.setEstado(Product.Estado.EN_TRANSITO);
                        t.setEtapa(EtapaTransferencia.ENTREGADO);
                        t.nuevaEtapa();
                        t.setProgresoGeneral(0.95);
                    }
                }
                break;

            case ENTREGADO:
                sucursal = gestor.getSucursal(t.getDestinoId());
                if (sucursal != null) {
                    producto.setEstado(Product.Estado.DISPONIBLE);
                    producto.setSucursalId(t.getDestinoId());
                    sucursal.agregarProducto(producto);
                    t.setProgresoGeneral(1.0);
                }
                return true;

            default:
                return false;
        }

        return false;
    }

    public void limpiarHistorial() {
        historial.clear();
    }
}
