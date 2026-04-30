package com.supermarkets.structures.cola;

import com.supermarkets.pojo.Product;

public class NodoCola {
    private Product dato;
    private NodoCola siguiente;

    public NodoCola(Product dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Product getDato() {
        return dato;
    }

    public void setDato(Product dato) {
        this.dato = dato;
    }

    public NodoCola getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCola siguiente) {
        this.siguiente = siguiente;
    }
}