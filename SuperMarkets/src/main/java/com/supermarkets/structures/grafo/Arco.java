package com.supermarkets.structures.grafo;

public class Arco {
    private String destinoId;
    private float peso;
    private Arco siguiente;

    public Arco(String destinoId) {
        this.destinoId = destinoId;
        this.siguiente = null;
    }

    public Arco(String destinoId, float peso) {
        this.destinoId = destinoId;
        this.peso = peso;
        this.siguiente = null;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(String destinoId) {
        this.destinoId = destinoId;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public Arco getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Arco siguiente) {
        this.siguiente = siguiente;
    }
}