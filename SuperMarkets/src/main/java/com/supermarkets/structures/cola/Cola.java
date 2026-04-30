package com.supermarkets.structures.cola;

import com.supermarkets.pojo.Product;

public class Cola {
    private NodoCola front;
    private NodoCola rear;
    private int size;

    public Cola() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public void put(Product dato) {
        if (dato == null) {
            throw new IllegalArgumentException("dato nulo");
        }

        NodoCola nuevo = new NodoCola(dato);
        if (isEmpty()) {
            front = nuevo;
            rear = nuevo;
        } else {
            rear.setSiguiente(nuevo);
            rear = nuevo;
        }
        size++;
    }

    public Product pop() {
        if (isEmpty()) {
            throw new IllegalStateException("cola vacia");
        }

        Product dato = front.getDato();
        front = front.getSiguiente();

        if (front == null) {
            rear = null;
        }

        size--;
        return dato;
    }

    public Product peek() {
        if (isEmpty()) {
            return null;
        }
        return front.getDato();
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }
}