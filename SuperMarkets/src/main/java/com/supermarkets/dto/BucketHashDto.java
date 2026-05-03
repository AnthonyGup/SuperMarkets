package com.supermarkets.dto;

public class BucketHashDto {
    private int indice;
    private int elementos;
    private java.util.List<String> claves;

    public BucketHashDto() {
        this.claves = new java.util.ArrayList<>();
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getElementos() {
        return elementos;
    }

    public void setElementos(int elementos) {
        this.elementos = elementos;
    }

    public java.util.List<String> getClaves() {
        return claves;
    }

    public void setClaves(java.util.List<String> claves) {
        this.claves = claves;
    }
}