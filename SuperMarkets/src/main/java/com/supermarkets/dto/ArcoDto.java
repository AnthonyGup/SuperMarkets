package com.supermarkets.dto;

public class ArcoDto {
    private String destinoId;
    private float peso;

    public ArcoDto() {}

    public ArcoDto(String destinoId, float peso) {
        this.destinoId = destinoId;
        this.peso = peso;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(String destinoId) {
        this.destinoId = destinoId;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }
}