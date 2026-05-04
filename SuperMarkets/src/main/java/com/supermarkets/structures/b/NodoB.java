package com.supermarkets.structures.b;

import com.supermarkets.pojo.Product;

public class NodoB {
    protected Product[] claves;
    protected NodoB[] ramas;
    protected int cuenta;

    private int m;

    public NodoB(int grado) {
        this.m = grado;
        this.cuenta = 0;
        claves = new Product[grado * 2];
        ramas = new NodoB[(grado * 2) + 1];

        for(int k = 0; k < grado*2; k++) {
            claves[k] = null;
        }

        for(int k = 0; k <= grado*2; k++) {
            ramas[k] = null;
        }
    }

    public boolean nodoLleno() {
        return cuenta == m - 1;
    }

    public boolean nodoSemiVacio() {
        return cuenta < (m + 1) / 2;
    }

    public boolean esHoja() {
        return ramas[0] == null;
    }
    
    public Product Oclave(int i) {
        return claves[i];
    }

    public void Pclave(int i, Product clave) {
        claves[i] = clave;
    }

    public NodoB Orama(int i) {
        return ramas[i];
    }

    public void Prama(int i, NodoB p) {
        ramas[i] = p;
    }

    public int Ocuenta() {
        return cuenta;
    }

    public void Pcuenta(int valor) {
        cuenta = valor;
    }
}
