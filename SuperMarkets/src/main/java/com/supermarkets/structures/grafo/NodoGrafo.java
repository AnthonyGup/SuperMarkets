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
public class NodoGrafo {
    
    private Sucursal dato;
    private ListaAdyacencia lista;
    private NodoGrafo siguientee;
    
    
    public NodoGrafo(Sucursal dato) {
        this.dato = dato;
        this.lista = new ListaAdyacencia();
        this.siguientee = null;
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

    public NodoGrafo getSiguientee() {
        return siguientee;
    }

    public void setSiguientee(NodoGrafo siguientee) {
        this.siguientee = siguientee;
    }
    
    
}
