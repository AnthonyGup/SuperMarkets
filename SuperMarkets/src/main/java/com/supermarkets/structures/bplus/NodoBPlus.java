package com.supermarkets.structures.bplus;

import com.supermarkets.pojo.Product;
import com.supermarkets.structures.listas.ListaEnlazada;

public class NodoBPlus {
    protected String[] claves;
    protected NodoBPlus[] ramas;
    protected ListaEnlazada[] valores;
    protected NodoBPlus siguiente;
    protected boolean isHoja;
    protected int cuenta;
    protected int m;

    public NodoBPlus(int orden) {
        this(orden, false);
    }

    public NodoBPlus(int orden, boolean hoja) {
        this.isHoja = hoja;
        this.cuenta = 0;
        this.m = orden;
        this.claves = new String[m - 1];
        this.ramas = new NodoBPlus[m];
        this.valores = new ListaEnlazada[m - 1];
        this.siguiente = null;

        for (int i = 0; i < m - 1; i++) {
            claves[i] = "";
            valores[i] = null;
        }

        for (int i = 0; i < m; i++) {
            ramas[i] = null;
        }
    }

    public String Oclave(int i) {
        if (i >= 0 && i < cuenta) {
            return claves[i];
        }
        return "";
    }

    public NodoBPlus Orama(int i) {
        if (i < 0 || i > cuenta) {
            return null;
        }
        return ramas[i];
    }

    public ListaEnlazada Ovalor(int i) {
        if (!isHoja || i < 0 || i >= cuenta) {
            return null;
        }
        return valores[i];
    }

    public NodoBPlus OramaSiguiente() {
        return siguiente;
    }

    public boolean esHoja() {
        return isHoja;
    }

    public int Ocuenta() {
        return cuenta;
    }

    public int Oorden() {
        return m;
    }

    public void Pclave(int i, String clave) {
        if (i >= 0 && i < m - 1) {
            claves[i] = clave;
        }
    }

    public void Prama(int i, NodoBPlus p) {
        if (i >= 0 && i < m) {
            ramas[i] = p;
        }
    }

    public void Pvalor(int i, ListaEnlazada lista) {
        if (i >= 0 && i < m - 1 && isHoja) {
            valores[i] = lista;
        }
    }

    public void PramaSiguiente(NodoBPlus p) {
        this.siguiente = p;
    }

    public void Pcuenta(int valor) {
        this.cuenta = valor;
    }

    public boolean nodoLLeno() {
        return cuenta == m - 1;
    }

    public boolean nodoSemiVacio() {
        return cuenta <= (m / 2);
    }

    public int buscarPosicion(String clave) {
        int pos = 0;
        while (pos < cuenta && claves[pos].compareTo(clave) < 0) {
            pos++;
        }
        return pos;
    }

    public void agregarProductoEnHoja(ListaEnlazada lista, Product producto) {
        if (lista != null) {
            lista.insertar(producto);
        }
    }
}
