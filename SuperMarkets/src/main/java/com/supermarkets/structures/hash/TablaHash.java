package com.supermarkets.structures.hash;

import com.supermarkets.pojo.Product;

import java.util.Locale;

public class TablaHash {
    private NodoHash[] buckets;
    private int capacidad;
    private int size;

    public TablaHash(int capacidadInicial) {
        this.capacidad = capacidadInicial;
        this.buckets = new NodoHash[capacidad];
        this.size = 0;
    }

    public boolean insertar(String clave, Product valor) {
        if (clave == null || clave.isBlank() || valor == null) {
            return false;
        }

        String claveNorm = normalizar(clave);
        int indice = hash(claveNorm);

        NodoHash actual = buckets[indice];
        while (actual != null) {
            if (actual.getClave().equals(claveNorm)) {
                return false;
            }
            actual = actual.getSiguiente();
        }

        NodoHash nuevo = new NodoHash(claveNorm, valor);
        nuevo.setSiguiente(buckets[indice]);
        buckets[indice] = nuevo;
        size++;

        if ((double) size / capacidad > 0.75) {
            redimensionar();
        }

        return true;
    }

    public Product buscar(String clave) {
        if (clave == null || clave.isBlank()) {
            return null;
        }

        String claveNorm = normalizar(clave);
        int indice = hash(claveNorm);
        NodoHash actual = buckets[indice];

        while (actual != null) {
            if (actual.getClave().equals(claveNorm)) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }

        return null;
    }

    public boolean eliminar(String clave) {
        if (clave == null || clave.isBlank()) {
            return false;
        }

        String claveNorm = normalizar(clave);
        int indice = hash(claveNorm);
        NodoHash actual = buckets[indice];
        NodoHash anterior = null;

        while (actual != null) {
            if (actual.getClave().equals(claveNorm)) {
                if (anterior != null) {
                    anterior.setSiguiente(actual.getSiguiente());
                } else {
                    buckets[indice] = actual.getSiguiente();
                }
                size--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }

        return false;
    }

    public boolean estaVacia() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    private int hash(String clave) {
        int hash = 0;
        for (int i = 0; i < clave.length(); i++) {
            hash = (hash * 31 + clave.charAt(i)) % capacidad;
        }
        return Math.abs(hash);
    }

    private void redimensionar() {
        NodoHash[] antiguos = buckets;
        capacidad = capacidad * 2;
        buckets = new NodoHash[capacidad];
        size = 0;

        for (int i = 0; i < antiguos.length; i++) {
            NodoHash actual = antiguos[i];
            while (actual != null) {
                NodoHash siguiente = actual.getSiguiente();
                int indice = hash(actual.getClave());
                actual.setSiguiente(buckets[indice]);
                buckets[indice] = actual;
                size++;
                actual = siguiente;
            }
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.toLowerCase(Locale.ROOT);
    }
}