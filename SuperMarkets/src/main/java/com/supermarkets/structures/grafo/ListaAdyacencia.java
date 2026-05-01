/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.supermarkets.structures.grafo;

import com.supermarkets.pojo.Sucursal;

/**
 *
 * @author antho
 */
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
    
    public void nuevaAdyacencia(Sucursal destino) {
        if (!adyacente(destino)) {
            Arco nodo = new Arco(destino);
            inserta(nodo, destino);
        }
    }
    
    public void nuevaAdyacencia(Sucursal destino, float peso) {
        if (!adyacente(destino)) {
            Arco nodo = new Arco(destino, peso);
            inserta(nodo, destino);
        }
    }
    
    private void inserta(Arco nodo, Sucursal destino) {
        if(listaVacia()) {
            this.primero = nodo;
            this.ultimo = nodo;
        } else {
            if (destino.toString().compareTo(this.primero.getDestino().toString()) <= 0) {
                nodo.setSiguiente(this.primero);
                this.primero = nodo;
            } else {
                if (destino.toString().compareTo(this.ultimo.getDestino().toString()) >= 0) {
                    this.ultimo.setSiguiente(nodo);
                    this.ultimo = nodo;
                }
                Arco posicion = this.primero;
                while (destino.toString().compareTo(posicion.getDestino().toString()) <= 0) {
                    posicion = posicion.getSiguiente();
                }
                nodo.setSiguiente(posicion.getSiguiente());
                posicion.setSiguiente(nodo);
            }
        }
    }
    
    public boolean adyacente(Sucursal dato) {
        Arco actual;
        boolean encontrado = false;
        actual = this.primero;
        while (actual != null && !dato.toString().equals(actual.getDestino().toString())) {
            actual = actual.getSiguiente();
        }
        if (actual != null) {
            encontrado = true;
        }
        return encontrado;
    }

    public Arco getPrimero() {
        return primero;
    }

    public void setPrimero(Arco primero) {
        this.primero = primero;
    }

    public Arco getUltimo() {
        return ultimo;
    }

    public void setUltimo(Arco ultimo) {
        this.ultimo = ultimo;
    }
}
