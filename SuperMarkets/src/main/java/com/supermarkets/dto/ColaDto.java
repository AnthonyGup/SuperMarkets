package com.supermarkets.dto;

import java.util.List;

public class ColaDto {
    private String nombre;
    private List<Object> productos;
    private int size;

    public ColaDto() {}

    public ColaDto(String nombre, int size) {
        this.nombre = nombre;
        this.size = size;
        this.productos = new java.util.ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Object> getProductos() {
        return productos;
    }

    public void setProductos(List<Object> productos) {
        this.productos = productos;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}