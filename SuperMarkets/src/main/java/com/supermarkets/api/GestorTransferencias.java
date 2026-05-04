package com.supermarkets.api;

import com.supermarkets.pojo.Product;
import com.supermarkets.pojo.Sucursal;
import com.supermarkets.structures.grafo.Grafo;
import com.supermarkets.structures.cola.Cola;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GestorTransferencias {
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
        origen.agregarASalida(producto);

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
                long duracion = System.currentTimeMillis() - t.getTiempoInicioTransferencia();
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

        switch (t.getEtapa()) {
            case COLA_SALIDA_ORIGEN:
                sucursal = gestor.getSucursal(t.getOrigenId());
                if (sucursal != null && !sucursal.getColaSalida().isEmpty()) {
                    Cola cola = sucursal.getColaSalida();
                    
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                    }
                    
                    if (t.esPrimeroEnCola(cola)) {
                        if (t.getTiempoEsperaInicioMs() == 0) {
                            t.setTiempoEsperaInicioMs(ahora);
                        }
                        
                        long esperaMs = (long) (sucursal.gettDespacho() * 60 * 1000);
                        long elapsedEspera = ahora - t.getTiempoEsperaInicioMs();
                        double progreso = Math.min(elapsedEspera / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.10 + progreso * 0.10);

                        if (progreso >= 1.0) {
                            cola.pop();
                            t.setEtapa(EtapaTransferencia.VIAJE);
                            t.nuevaEtapa();
                            t.setTiempoEsperaInicioMs(0);
                            t.setTramoActualIndex(0);
                            t.setProgresoGeneral(0.20);
                        }
                    }
                }
                break;

            case VIAJE:
            case VIAJE_SALIDA:
                long elapsedEnEtapa = ahora - t.getEtapaInicioMs();
                
                if (t.getTramoActualIndex() >= t.getTramosViaje().size()) {
                    t.setEtapa(EtapaTransferencia.COLA_INGRESO_DESTINO);
                    t.nuevaEtapa();
                    t.setTiempoEsperaInicioMs(0);
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
                        t.setTiempoEsperaInicioMs(0);
                    }
                }
                break;

            case COLA_INGRESO_INTERMEDIA:
                sucursal = gestor.getSucursal(t.getSucursalActualId());
                if (sucursal != null) {
                    Cola cola = sucursal.getColaIngreso();
                    
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarAIngreso(producto);
                    }
                    
                    if (t.esPrimeroEnCola(cola)) {
                        if (t.getTiempoEsperaInicioMs() == 0) {
                            t.setTiempoEsperaInicioMs(ahora);
                        }
                        
                        long esperaMs = (long) (sucursal.gettIngreso() * 60 * 1000);
                        long elapsedEspera = ahora - t.getTiempoEsperaInicioMs();
                        double progreso = Math.min(elapsedEspera / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.65 + progreso * 0.05);

                        if (progreso >= 1.0) {
                            cola.pop();
                            t.setEtapa(EtapaTransferencia.COLA_PREPARACION_INTERMEDIA);
                            t.nuevaEtapa();
                            t.setTiempoEsperaInicioMs(0);
                            t.setProgresoGeneral(0.70);
                        }
                    }
                }
                break;

            case COLA_PREPARACION_INTERMEDIA:
                sucursal = gestor.getSucursal(t.getSucursalActualId());
                if (sucursal != null) {
                    Cola cola = sucursal.getColaPreparacion();
                    
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarAPreparacion(producto);
                    }
                    
                    if (t.esPrimeroEnCola(cola)) {
                        if (t.getTiempoEsperaInicioMs() == 0) {
                            t.setTiempoEsperaInicioMs(ahora);
                        }
                        
                        long esperaMs = (long) (sucursal.gettTraspaso() * 60 * 1000);
                        long elapsedEspera = ahora - t.getTiempoEsperaInicioMs();
                        double progreso = Math.min(elapsedEspera / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.70 + progreso * 0.05);

                        if (progreso >= 1.0) {
                            cola.pop();
                            t.setEtapa(EtapaTransferencia.COLA_SALIDA_INTERMEDIA);
                            t.nuevaEtapa();
                            t.setTiempoEsperaInicioMs(0);
                            t.setProgresoGeneral(0.75);
                        }
                    }
                }
                break;

            case COLA_SALIDA_INTERMEDIA:
                sucursal = gestor.getSucursal(t.getSucursalActualId());
                if (sucursal != null) {
                    Cola cola = sucursal.getColaSalida();
                    
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarASalida(producto);
                    }
                    
                    if (t.esPrimeroEnCola(cola)) {
                        if (t.getTiempoEsperaInicioMs() == 0) {
                            t.setTiempoEsperaInicioMs(ahora);
                        }
                        
                        long esperaMs = (long) (sucursal.gettDespacho() * 60 * 1000);
                        long elapsedEspera = ahora - t.getTiempoEsperaInicioMs();
                        double progreso = Math.min(elapsedEspera / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.75 + progreso * 0.05);

                        if (progreso >= 1.0) {
                            cola.pop();
                            t.setTramoActualIndex(t.getTramoActualIndex() + 1);
                            t.setEtapa(EtapaTransferencia.VIAJE_SALIDA);
                            t.nuevaEtapa();
                            t.setTiempoEsperaInicioMs(0);
                            t.setProgresoGeneral(0.80);
                        }
                    }
                }
                break;

            case COLA_INGRESO_DESTINO:
                sucursal = gestor.getSucursal(t.getDestinoId());
                if (sucursal != null) {
                    Cola cola = sucursal.getColaIngreso();
                    
                    if (!t.isEtapaIniciada()) {
                        t.iniciarEtapa();
                        sucursal.agregarAIngreso(producto);
                    }
                    
                    if (t.esPrimeroEnCola(cola)) {
                        if (t.getTiempoEsperaInicioMs() == 0) {
                            t.setTiempoEsperaInicioMs(ahora);
                        }
                        
                        long esperaMs = (long) (sucursal.gettIngreso() * 60 * 1000);
                        long elapsedEspera = ahora - t.getTiempoEsperaInicioMs();
                        double progreso = Math.min(elapsedEspera / (double) esperaMs, 1.0);
                        t.setProgresoGeneral(0.80 + progreso * 0.15);

                        if (progreso >= 1.0) {
                            cola.pop();
                            producto.setEstado(Product.Estado.EN_TRANSITO);
                            t.setEtapa(EtapaTransferencia.ENTREGADO);
                            t.nuevaEtapa();
                            t.setTiempoEsperaInicioMs(0);
                            t.setProgresoGeneral(0.95);
                        }
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