package com.supermarkets.structures.b;

import com.supermarkets.pojo.Product;

import java.util.ArrayList;
import java.util.List;

public class NodoB {
    private final int orden;
    private final List<Product> claves;
    private final List<NodoB> ramas;
    private boolean hoja;
    private int dotId;

    public NodoB(int orden) {
        this(orden, true);
    }

    public NodoB(int orden, boolean hoja) {
        this.orden = orden;
        this.hoja = hoja;
        this.claves = new ArrayList<>();
        this.ramas = new ArrayList<>();
        this.dotId = 0;
    }

    public boolean nodoLLeno() {
        return claves.size() >= orden - 1;
    }

    public boolean nodoSemiVacio() {
        return hoja;
    }

    public Product Oclave(int i) {
        return claves.get(i - 1);
    }

    public void Pclave(int i, Product producto) {
        int index = i - 1;
        while (claves.size() <= index) {
            claves.add(null);
        }
        claves.set(index, producto);
    }

    public NodoB Orama(int i) {
        if (i < 0 || i >= ramas.size()) {
            return null;
        }
        return ramas.get(i);
    }

    public void Prama(int i, NodoB p) {
        while (ramas.size() <= i) {
            ramas.add(null);
        }
        ramas.set(i, p);
    }

    public int Ocuenta() {
        return claves.size();
    }

    public void Pcuenta(int valor) {
        while (claves.size() > valor) {
            claves.remove(claves.size() - 1);
        }
        while (claves.size() < valor) {
            claves.add(null);
        }
    }

    public int OdotId() {
        return dotId;
    }

    public void PdotId(int id) {
        this.dotId = id;
    }

    public int getOrden() {
        return orden;
    }

    public List<Product> getClaves() {
        return claves;
    }

    public List<NodoB> getRamas() {
        return ramas;
    }

    public boolean esHoja() {
        return hoja;
    }

    public void setHoja(boolean hoja) {
        this.hoja = hoja;
    }
}
