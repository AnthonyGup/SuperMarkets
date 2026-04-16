package com.supermarkets.structures.avl;

import com.supermarkets.pojo.Product;

public class NodoAvl {
    private Product dato;
    private NodoAvl derecho;
    private NodoAvl izquierdo;
    private int fe;
    private int altura;

    public NodoAvl(Product valor) {
        this.dato = valor;
        this.izquierdo = null;
        this.derecho = null;
        this.fe = 0;
        this.altura = 1;
    }

    public Product getDato() {
        return dato;
    }

    public void setDato(Product dato) {
        this.dato = dato;
    }

    public NodoAvl getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoAvl derecho) {
        this.derecho = derecho;
    }

    public NodoAvl getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoAvl izquierdo) {
        this.izquierdo = izquierdo;
    }

    public int getFe() {
        return fe;
    }

    public void setFe(int fe) {
        this.fe = fe;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }
}
