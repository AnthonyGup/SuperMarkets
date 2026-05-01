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
public class Grafo {
    private NodoGrafo primero;
    private NodoGrafo ultimo;

    public Grafo() {
        this.primero = null;
        this.ultimo = null;
    }
    
    public boolean grafoVacio() {
        return this.primero == null;
    }
    
    public boolean existeVertice(Sucursal dato) {
        boolean existe = false;
        if (!grafoVacio()) {
            NodoGrafo temp = primero;
            while (temp != null && !existe) {
                if (temp.getDato().toString().equals(dato.toString())) {
                    existe = true;
                }
                temp = temp.getSiguientee();
            }
        }
        return existe;
    }
    
    public void NuevaArista(Sucursal origen, Sucursal destino) {
        if (existeVertice(origen) && existeVertice(destino)) {
            NodoGrafo posicion = this.primero;
            while (!posicion.getDato().equals(origen.toString())) {
                posicion = posicion.getSiguientee();
            }
            posicion.getLista().nuevaAdyacencia(destino);
        }
    }
    
    public void NuevaArista(Sucursal origen, Sucursal destino, float peso) {
        if (existeVertice(origen) && existeVertice(destino)) {
            NodoGrafo posicion = this.primero;
            while (!posicion.getDato().equals(origen.toString())) {
                posicion = posicion.getSiguientee();
            }
            posicion.getLista().nuevaAdyacencia(destino, peso);
        }
    }
    
    public void nuevoNodo(Sucursal dato) {
        if (!existeVertice(dato)) {
            NodoGrafo nodo = new NodoGrafo(dato);
            if (grafoVacio()) {
                this.primero = nodo;
                this.ultimo = nodo;
            } else {
                if (dato.toString().compareTo(this.primero.getDato().toString()) <= 0) {
                    nodo.setSiguientee(this.primero);
                    this.primero = nodo;
                } else {
                    if (dato.toString().compareTo(this.ultimo.getDato().toString()) >= 0) {
                        this.ultimo.setSiguientee(nodo);
                        this.ultimo = nodo;
                    } else {
                        NodoGrafo temp = this.primero;
                        while (dato.toString().compareTo(temp.getDato().toString()) >= 0) {
                            temp = temp.getSiguientee();
                        }
                        nodo.setSiguientee(temp.getSiguientee());
                        temp.setSiguientee(nodo);
                    }
                }
            }
        }
    }
}
