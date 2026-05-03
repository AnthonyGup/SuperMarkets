package com.supermarkets.dto;

public class NodoArbolDto {
    private String clave;
    private Object dato;
    private int altura;
    private int fe;
    private java.util.List<NodoArbolDto> hijos;

    public NodoArbolDto() {
        this.hijos = new java.util.ArrayList<>();
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getFe() {
        return fe;
    }

    public void setFe(int fe) {
        this.fe = fe;
    }

    public java.util.List<NodoArbolDto> getHijos() {
        return hijos;
    }

    public void setHijos(java.util.List<NodoArbolDto> hijos) {
        this.hijos = hijos;
    }
}