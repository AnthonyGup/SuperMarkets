package com.supermarkets.structures.hash;

import com.supermarkets.pojo.Product;

public class NodoHash {
    private String clave;
    private Product dato;
    private NodoHash siguiente;

    public NodoHash(String clave, Product dato) {
        this.clave = clave;
        this.dato = dato;
        this.siguiente = null;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Product getDato() {
        return dato;
    }

    public void setDato(Product dato) {
        this.dato = dato;
    }

    public NodoHash getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoHash siguiente) {
        this.siguiente = siguiente;
    }
}