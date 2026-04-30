package com.supermarkets.pojo;

import com.supermarkets.structures.cola.Cola;
import com.supermarkets.structures.hash.TablaHash;
import com.supermarkets.structures.listas.ListaEnlazada;

public class Sucursal implements Runnable {
    private String id;
    private String nombre;
    private String ubicacion;
    private double tIngreso;
    private double tTraspaso;
    private double tDespacho;

    private Cola colaIngreso;
    private Cola colaPreparacion;
    private Cola colaSalida;

    private TablaHash inventarioHash;
    private ListaEnlazada inventarioLista;

    private volatile boolean ejecutando;
    private Thread hilo;

    public Sucursal() {
        this.colaIngreso = new Cola();
        this.colaPreparacion = new Cola();
        this.colaSalida = new Cola();
        this.inventarioHash = new TablaHash(16);
        this.inventarioLista = new ListaEnlazada();
        this.ejecutando = false;
    }

    public void iniciar() {
        if (!ejecutando) {
            ejecutando = true;
            hilo = new Thread(this);
            hilo.start();
        }
    }

    public void detener() {
        ejecutando = false;
        if (hilo != null) {
            hilo.interrupt();
        }
    }

    @Override
    public void run() {
        while (ejecutando) {
            try {
                procesarColas();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void procesarColas() {
        if (!colaIngreso.isEmpty()) {
            try {
                Thread.sleep((long) (tIngreso * 1000));
                colaIngreso.pop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (!colaPreparacion.isEmpty()) {
            try {
                Thread.sleep((long) (tTraspaso * 1000));
                colaPreparacion.pop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (!colaSalida.isEmpty()) {
            try {
                Thread.sleep((long) (tDespacho * 1000));
                colaSalida.pop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void agregarAIngreso(Product producto) {
        colaIngreso.put(producto);
    }

    public void agregarAPreparacion(Product producto) {
        colaPreparacion.put(producto);
    }

    public void agregarASalida(Product producto) {
        colaSalida.put(producto);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double gettIngreso() {
        return tIngreso;
    }

    public void settIngreso(double tIngreso) {
        this.tIngreso = tIngreso;
    }

    public double gettTraspaso() {
        return tTraspaso;
    }

    public void settTraspaso(double tTraspaso) {
        this.tTraspaso = tTraspaso;
    }

    public double gettDespacho() {
        return tDespacho;
    }

    public void settDespacho(double tDespacho) {
        this.tDespacho = tDespacho;
    }

    public Cola getColaIngreso() {
        return colaIngreso;
    }

    public Cola getColaPreparacion() {
        return colaPreparacion;
    }

    public Cola getColaSalida() {
        return colaSalida;
    }

    public TablaHash getInventarioHash() {
        return inventarioHash;
    }

    public ListaEnlazada getInventarioLista() {
        return inventarioLista;
    }

    public boolean isEjecutando() {
        return ejecutando;
    }
}