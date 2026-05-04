package com.supermarkets.pojo;

import com.supermarkets.structures.avl.ArbolAvl;
import com.supermarkets.structures.b.ArbolB;
import com.supermarkets.structures.bplus.ArbolBPlus;
import com.supermarkets.structures.cola.Cola;
import com.supermarkets.structures.hash.TablaHash;
import com.supermarkets.structures.listas.ListaEnlazada;
import com.supermarkets.structures.pila.Pila;

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

    private ArbolAvl inventarioAvl;
    private ArbolB inventarioB;
    private ArbolBPlus inventarioBPlus;
    private TablaHash inventarioHash;
    private ListaEnlazada inventarioLista;

    private Pila pilaCambios;
    private Pila pilaDevoluciones;

    private volatile boolean ejecutando;
    private Thread hilo;

    public Sucursal() {
        this.colaIngreso = new Cola();
        this.colaPreparacion = new Cola();
        this.colaSalida = new Cola();

        this.inventarioAvl = new ArbolAvl();
        this.inventarioB = new ArbolB();
        this.inventarioBPlus = new ArbolBPlus();
        this.inventarioHash = new TablaHash(16);
        this.inventarioLista = new ListaEnlazada();

        this.pilaCambios = new Pila();
        this.pilaDevoluciones = new Pila();

        this.ejecutando = false;
    }

    public boolean agregarProducto(Product producto) {
        if (producto == null) {
            return false;
        }

        try {
            inventarioLista.insertar(producto);
            inventarioHash.insertar(producto.getBarcode(), producto);
            inventarioAvl.insertar(producto);
            inventarioB.insertar(producto);
            inventarioBPlus.insertar(producto);
            return true;
        } catch (Exception e) {
            rollbackProducto(producto);
            return false;
        }
    }

    public boolean eliminarProducto(String nombre) {
        try {
            Product producto = inventarioAvl.buscarProducto(nombre);
            if (producto != null) {
                inventarioLista.eliminar(nombre);
                inventarioHash.eliminar(producto.getBarcode());
                inventarioB.eliminar(producto.getExpiryDate());
                inventarioBPlus.eliminar(producto.getCategory());
                pilaCambios.push(producto);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deshacer() {
        if (pilaCambios.isEmpty()) {
            return false;
        }
        Product producto = pilaCambios.pop();
        return agregarProducto(producto);
    }

    public boolean devolver(Product producto) {
        if (producto != null) {
            pilaDevoluciones.push(producto);
            return agregarProducto(producto);
        }
        return false;
    }

    private void rollbackProducto(Product producto) {
        try {
            inventarioLista.eliminar(producto.getName());
        } catch (Exception ignored) {}
        try {
            inventarioHash.eliminar(producto.getBarcode());
        } catch (Exception ignored) {}
    }

    public Product buscarPorNombre(String nombre) {
        return inventarioAvl.busquedaBinaria(nombre);
    }

    public Product buscarPorBarcode(String barcode) {
        return inventarioHash.buscar(barcode);
    }

    public Product[] buscarPorCategoria(String categoria) {
        return inventarioBPlus.buscarPorCategoria(categoria);
    }

    public Product[] buscarPorRangoFechas(String fechaInicio, String fechaFin) {
        return inventarioB.buscarPorRango(fechaInicio, fechaFin);
    }

    public boolean existeProducto(String nombre) {
        return inventarioAvl.buscar(nombre);
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
                Product producto = colaIngreso.peek();
                if (producto != null) {
                    Thread.sleep((long) (tIngreso * 1000));
                    colaIngreso.pop();
                    producto.setEstado(Product.Estado.EN_TRANSITO);
                    colaPreparacion.put(producto);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (!colaPreparacion.isEmpty()) {
            try {
                
                Product producto = colaPreparacion.peek();
                if (producto != null) {
                    Thread.sleep((long) (tTraspaso * 1000));
                    colaPreparacion.pop();
                    colaSalida.put(producto);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (!colaSalida.isEmpty()) {
            try {
                Product producto = colaSalida.peek();
                if (producto != null) {
                    Thread.sleep((long) (tDespacho * 1000));
                    colaSalida.pop();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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

    public ArbolAvl getInventarioAvl() {
        return inventarioAvl;
    }

    public ArbolB getInventarioB() {
        return inventarioB;
    }

    public ArbolBPlus getInventarioBPlus() {
        return inventarioBPlus;
    }

    public TablaHash getInventarioHash() {
        return inventarioHash;
    }

    public ListaEnlazada getInventarioLista() {
        return inventarioLista;
    }

    public Pila getPilaCambios() {
        return pilaCambios;
    }

    public Pila getPilaDevoluciones() {
        return pilaDevoluciones;
    }

    public boolean isEjecutando() {
        return ejecutando;
    }

    public int getTotalProductos() {
        return inventarioLista.getSize();
    }
}