package com.supermarkets.structures.pila;

import com.supermarkets.pojo.Product;

public class Pila {
    private NodoPila top;
    private int size;

    public Pila() {
        this.top = null;
        this.size = 0;
    }

    public void push(Product dato) {
        if (dato == null) {
            throw new IllegalArgumentException("dato nulo");
        }

        NodoPila nuevo = new NodoPila(dato);
        nuevo.setSiguiente(top);
        top = nuevo;
        size++;
    }

    public Product pop() {
        if (isEmpty()) {
            throw new IllegalStateException("pila vacia");
        }

        Product dato = top.getDato();
        top = top.getSiguiente();
        size--;
        return dato;
    }

    public Product peek() {
        if (isEmpty()) {
            return null;
        }
        return top.getDato();
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }
}