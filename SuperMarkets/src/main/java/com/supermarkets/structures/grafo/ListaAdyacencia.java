package com.supermarkets.structures.grafo;

public class ListaAdyacencia {
    private Arco primero;
    private Arco ultimo;

    public ListaAdyacencia() {
        this.primero = null;
        this.ultimo = null;
    }

    public boolean listaVacia() {
        return this.primero == null;
    }

    public void insertar(String destinoId, float peso) {
        Arco nodo = new Arco(destinoId, peso);
        if (listaVacia()) {
            this.primero = nodo;
            this.ultimo = nodo;
        } else {
            this.ultimo.setSiguiente(nodo);
            this.ultimo = nodo;
        }
    }

    public boolean buscar(String destinoId) {
        Arco actual = this.primero;
        while (actual != null) {
            if (actual.getDestinoId().equals(destinoId)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public float getPeso(String destinoId) {
        Arco actual = this.primero;
        while (actual != null) {
            if (actual.getDestinoId().equals(destinoId)) {
                return actual.getPeso();
            }
            actual = actual.getSiguiente();
        }
        return -1;
    }

    public Arco getPrimero() {
        return primero;
    }
}