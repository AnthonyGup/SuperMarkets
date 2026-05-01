package com.supermarkets.structures.listas;

import com.supermarkets.pojo.Product;

import java.util.Locale;

public class ListaEnlazada {
    private NodoList cabeza;
    private NodoList cola;
    private int size;

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int getSize() {
        return size;
    }

    public NodoList getCabeza() {
        return cabeza;
    }

    public NodoList buscarNodo(String nombre) {
        String nombreBuscado = normalizar(nombre);
        NodoList actual = cabeza;

        while (actual != null) {
            Product producto = actual.getDato();
            if (producto != null && normalizar(producto.getName()).equals(nombreBuscado)) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public void insertar(Product dato) {
        if (dato == null) {
            throw new IllegalArgumentException("dato nulo");
        }
        if (dato.getName() == null || dato.getName().isBlank()) {
            throw new IllegalArgumentException("nombre vacio");
        }
        if (buscarNodo(dato.getName()) != null) {
            throw new IllegalArgumentException("ya existe un producto con nombre: " + dato.getName());
        }

        NodoList nuevoNodo = new NodoList(dato);
        if (estaVacia()) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.setSiguiente(nuevoNodo);
            cola = nuevoNodo;
        }
        size++;
    }

    public void eliminar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("nombre vacio");
        }

        String nombreBuscado = normalizar(nombre);
        NodoList anterior = null;
        NodoList actual = cabeza;

        while (actual != null) {
            Product producto = actual.getDato();
            if (producto != null && normalizar(producto.getName()).equals(nombreBuscado)) {
                desvincularNodo(anterior, actual);
                size--;
                return;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }

        throw new IllegalArgumentException("no existe un producto con nombre: " + nombre);
    }

    public void limpiar() {
        cabeza = null;
        cola = null;
        size = 0;
    }

    private void desvincularNodo(NodoList anterior, NodoList actual) {
        NodoList siguiente = actual.getSiguiente();

        if (anterior != null) {
            anterior.setSiguiente(siguiente);
        } else {
            cabeza = siguiente;
        }

        if (siguiente == null) {
            cola = anterior;
        }
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.toLowerCase(Locale.ROOT);
    }
}
