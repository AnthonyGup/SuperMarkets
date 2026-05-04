package com.supermarkets.dto;

public class DotResponse {
    private String tipo;
    private String dot;
    private int totalNodos;
    private int altura;

    public DotResponse() {}

    public DotResponse(String tipo, String dot, int totalNodos, int altura) {
        this.tipo = tipo;
        this.dot = dot;
        this.totalNodos = totalNodos;
        this.altura = altura;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDot() { return dot; }
    public void setDot(String dot) { this.dot = dot; }
    public int getTotalNodos() { return totalNodos; }
    public void setTotalNodos(int totalNodos) { this.totalNodos = totalNodos; }
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
}
