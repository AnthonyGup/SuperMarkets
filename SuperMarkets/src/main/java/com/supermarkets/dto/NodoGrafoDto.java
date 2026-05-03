package com.supermarkets.dto;

import java.util.List;

public class NodoGrafoDto {
    private String id;
    private String nombre;
    private String ubicacion;
    private List<ArcoDto> conexiones;

    public NodoGrafoDto() {
        this.conexiones = new java.util.ArrayList<>();
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

    public List<ArcoDto> getConexiones() {
        return conexiones;
    }

    public void setConexiones(List<ArcoDto> conexiones) {
        this.conexiones = conexiones;
    }
}