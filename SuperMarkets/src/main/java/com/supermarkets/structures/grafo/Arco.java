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
public class Arco {
    
    private Sucursal destino;
    private float peso;
    private Arco siguiente;

    public Arco(Sucursal destino) {
        this.destino = destino;
        this.siguiente = null;
    }

    public Arco(Sucursal destino, float peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Sucursal getDestino() {
        return destino;
    }

    public void setDestino(Sucursal destino) {
        this.destino = destino;
    }
    
    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public Arco getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Arco siguiente) {
        this.siguiente = siguiente;
    }
    
    
}
