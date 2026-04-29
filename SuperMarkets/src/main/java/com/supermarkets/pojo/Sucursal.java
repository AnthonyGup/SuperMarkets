package com.supermarkets.pojo;

public class Sucursal {
    private String id;
    private String nombre;
    private String ubicacion;
    private double tIngreso;
    private double tTraspaso;
    private double tDespacho;

    public Sucursal() {}

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
}