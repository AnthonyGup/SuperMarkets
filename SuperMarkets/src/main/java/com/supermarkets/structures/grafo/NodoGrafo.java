package com.supermarkets.structures.grafo;

import com.supermarkets.pojo.Sucursal;

public class NodoGrafo {
    private String id;
    private Sucursal dato;
    private ListaAdyacencia lista;
    private NodoGrafo siguiente;

    public NodoGrafo(String id, Sucursal dato) {
        this.id = id;
        this.dato = dato;
        this.lista = new ListaAdyacencia();
        this.siguiente = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Sucursal getDato() {
        return dato;
    }

    public void setDato(Sucursal dato) {
        this.dato = dato;
    }

    public ListaAdyacencia getLista() {
        return lista;
    }

    public void setLista(ListaAdyacencia lista) {
        this.lista = lista;
    }

    public NodoGrafo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoGrafo siguiente) {
        this.siguiente = siguiente;
    }
}