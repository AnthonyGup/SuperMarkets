package com.supermarkets.structures.pila;

import com.supermarkets.pojo.Product;

public class NodoPila {
    private Product dato;
    private NodoPila siguiente;

    public NodoPila(Product dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Product getDato() {
        return dato;
    }

    public void setDato(Product dato) {
        this.dato = dato;
    }

    public NodoPila getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
}