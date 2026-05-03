package com.supermarkets.dto;

import java.util.List;

public class ResultadoMedicion {
    private String estructura;
    private String operacion;
    private double tiempoMs;
    private double tiempoPromedioMs;
    private int cantidadElementos;
    private int iteraciones;
    private String complejidad;
    private String claveBusqueda;
    private List<String> detalles;

    public ResultadoMedicion() {
        this.detalles = new java.util.ArrayList<>();
    }

    public ResultadoMedicion(String estructura, String operacion, double tiempoMs, int cantidadElementos) {
        this.estructura = estructura;
        this.operacion = operacion;
        this.tiempoMs = tiempoMs;
        this.cantidadElementos = cantidadElementos;
        this.iteraciones = 1;
        this.tiempoPromedioMs = tiempoMs;
        this.detalles = new java.util.ArrayList<>();
    }

    public String getEstructura() { return estructura; }
    public void setEstructura(String estructura) { this.estructura = estructura; }

    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    public double getTiempoMs() { return tiempoMs; }
    public void setTiempoMs(double tiempoMs) { this.tiempoMs = tiempoMs; }

    public double getTiempoPromedioMs() { return tiempoPromedioMs; }
    public void setTiempoPromedioMs(double tiempoPromedioMs) { this.tiempoPromedioMs = tiempoPromedioMs; }

    public int getCantidadElementos() { return cantidadElementos; }
    public void setCantidadElementos(int cantidadElementos) { this.cantidadElementos = cantidadElementos; }

    public int getIteraciones() { return iteraciones; }
    public void setIteraciones(int iteraciones) { this.iteraciones = iteraciones; }

    public String getComplejidad() { return complejidad; }
    public void setComplejidad(String complejidad) { this.complejidad = complejidad; }

    public String getClaveBusqueda() { return claveBusqueda; }
    public void setClaveBusqueda(String claveBusqueda) { this.claveBusqueda = claveBusqueda; }

    public List<String> getDetalles() { return detalles; }
    public void setDetalles(List<String> detalles) { this.detalles = detalles; }
}