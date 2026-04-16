package com.supermarkets.structures.bplus;

import com.supermarkets.pojo.Product;
import com.supermarkets.structures.listas.ListaEnlazada;

import java.util.ArrayList;
import java.util.List;

public class NodoBPlus {
    private final int m;
    private boolean hoja;
    private final List<String> claves;
    private final List<NodoBPlus> ramas;
    private final List<ListaEnlazada> valores;
    private NodoBPlus siguiente;
    private int dotId;

    public NodoBPlus(int orden) {
        this(orden, false);
    }

    public NodoBPlus(int orden, boolean hoja) {
        this.m = orden;
        this.hoja = hoja;
        this.claves = new ArrayList<>();
        this.ramas = new ArrayList<>();
        this.valores = new ArrayList<>();
        this.siguiente = null;
        this.dotId = 0;
    }

    public String Oclave(int i) {
        return claves.get(i);
    }

    public NodoBPlus Orama(int i) {
        if (i < 0 || i >= ramas.size()) {
            return null;
        }
        return ramas.get(i);
    }

    public ListaEnlazada Ovalor(int i) {
        if (!hoja || i < 0 || i >= valores.size()) {
            return null;
        }
        return valores.get(i);
    }

    public NodoBPlus OramaSiguiente() {
        return siguiente;
    }

    public boolean esHoja() {
        return hoja;
    }

    public int Ocuenta() {
        return claves.size();
    }

    public int Oorden() {
        return m;
    }

    public void Pclave(int i, String clave) {
        while (claves.size() <= i) {
            claves.add("");
        }
        claves.set(i, clave);
    }

    public void Prama(int i, NodoBPlus p) {
        while (ramas.size() <= i) {
            ramas.add(null);
        }
        ramas.set(i, p);
    }

    public void Pvalor(int i, ListaEnlazada lista) {
        while (valores.size() <= i) {
            valores.add(null);
        }
        valores.set(i, lista);
    }

    public void PramaSiguiente(NodoBPlus p) {
        this.siguiente = p;
    }

    public void Pcuenta(int valor) {
        while (claves.size() > valor) {
            claves.remove(claves.size() - 1);
            if (hoja && valores.size() > claves.size()) {
                valores.remove(valores.size() - 1);
            }
        }
    }

    public boolean nodoLLeno() {
        return claves.size() >= m - 1;
    }

    public boolean nodoSemiVacio() {
        return claves.size() <= (m / 2);
    }

    public int buscarPosicion(String clave) {
        int pos = 0;
        while (pos < claves.size() && claves.get(pos).compareTo(clave) < 0) {
            pos++;
        }
        return pos;
    }

    public void agregarProductoEnHoja(ListaEnlazada lista, Product producto) {
        if (lista != null) {
            lista.insertar(producto);
        }
    }

    public int OdotId() {
        return dotId;
    }

    public void PdotId(int id) {
        this.dotId = id;
    }

    public List<String> getClaves() {
        return claves;
    }

    public List<NodoBPlus> getRamas() {
        return ramas;
    }

    public List<ListaEnlazada> getValores() {
        return valores;
    }

    public void setHoja(boolean hoja) {
        this.hoja = hoja;
    }
}
