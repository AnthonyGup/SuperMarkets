package com.supermarkets.dto;

public class SucursalDto {
    private String id;
    private String nombre;
    private String ubicacion;
    private double tIngreso;
    private double tTraspaso;
    private double tDespacho;
    private int totalProductos;
    private int colaIngresoSize;
    private int colaPreparacionSize;
    private int colaSalidaSize;

    public SucursalDto() {}

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

    public int getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(int totalProductos) {
        this.totalProductos = totalProductos;
    }

    public int getColaIngresoSize() {
        return colaIngresoSize;
    }

    public void setColaIngresoSize(int colaIngresoSize) {
        this.colaIngresoSize = colaIngresoSize;
    }

    public int getColaPreparacionSize() {
        return colaPreparacionSize;
    }

    public void setColaPreparacionSize(int colaPreparacionSize) {
        this.colaPreparacionSize = colaPreparacionSize;
    }

    public int getColaSalidaSize() {
        return colaSalidaSize;
    }

    public void setColaSalidaSize(int colaSalidaSize) {
        this.colaSalidaSize = colaSalidaSize;
    }
}