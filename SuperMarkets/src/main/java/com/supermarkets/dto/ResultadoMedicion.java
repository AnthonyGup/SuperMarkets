package com.supermarkets.dto;

import java.util.List;

public class ResultadoMedicion {
    private String estructura;
    private String operacion;
    private double tiempoMs;
    private int cantidadElementos;
    private List<String> detalles;

    public ResultadoMedicion() {
        this.detalles = new java.util.ArrayList<>();
    }

    public ResultadoMedicion(String estructura, String operacion, double tiempoMs, int cantidadElementos) {
        this.estructura = estructura;
        this.operacion = operacion;
        this.tiempoMs = tiempoMs;
        this.cantidadElementos = cantidadElementos;
        this.detalles = new java.util.ArrayList<>();
    }

    public String getEstructura() {
        return estructura;
    }

    public void setEstructura(String estructura) {
        this.estructura = estructura;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public double getTiempoMs() {
        return tiempoMs;
    }

    public void setTiempoMs(double tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    public int getCantidadElementos() {
        return cantidadElementos;
    }

    public void setCantidadElementos(int cantidadElementos) {
        this.cantidadElementos = cantidadElementos;
    }

    public List<String> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<String> detalles) {
        this.detalles = detalles;
    }
}