package com.supermarkets.structures.listas;

import com.supermarkets.pojo.Product;

public class NodoList {
    private NodoList siguiente;
    private Product dato;

    public NodoList(Product dato) {
        this.dato = dato;
    }

    public NodoList getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoList siguiente) {
        this.siguiente = siguiente;
    }

    public Product getDato() {
        return dato;
    }

    public void setDato(Product dato) {
        this.dato = dato;
    }
}
