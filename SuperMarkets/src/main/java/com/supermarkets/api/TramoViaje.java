package com.supermarkets.api;

public class TramoViaje {
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