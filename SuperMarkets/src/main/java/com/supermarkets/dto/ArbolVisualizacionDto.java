package com.supermarkets.dto;

import java.util.List;

public class ArbolVisualizacionDto {
    private String tipo;
    private int totalNodos;
    private int altura;
    private List<NodoArbolVisual> nodos;
    private List<EnlaceVisual> enlaces;

    public ArbolVisualizacionDto() {}

    public ArbolVisualizacionDto(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getTotalNodos() { return totalNodos; }
    public void setTotalNodos(int totalNodos) { this.totalNodos = totalNodos; }
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
    public List<NodoArbolVisual> getNodos() { return nodos; }
    public void setNodos(List<NodoArbolVisual> nodos) { this.nodos = nodos; }
    public List<EnlaceVisual> getEnlaces() { return enlaces; }
    public void setEnlaces(List<EnlaceVisual> enlaces) { this.enlaces = enlaces; }

    public static class NodoArbolVisual {
        private String id;
        private String clave;
        private String valor;
        private int nivel;
        private int x;
        private int y;
        private boolean esHoja;

        public NodoArbolVisual() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getClave() { return clave; }
        public void setClave(String clave) { this.clave = clave; }
        public String getValor() { return valor; }
        public void setValor(String valor) { this.valor = valor; }
        public int getNivel() { return nivel; }
        public void setNivel(int nivel) { this.nivel = nivel; }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        public boolean isEsHoja() { return esHoja; }
        public void setEsHoja(boolean esHoja) { this.esHoja = esHoja; }
    }

    public static class EnlaceVisual {
        private String desde;
        private String hacia;

        public EnlaceVisual() {}

        public EnlaceVisual(String desde, String hacia) {
            this.desde = desde;
            this.hacia = hacia;
        }

        public String getDesde() { return desde; }
        public void setDesde(String desde) { this.desde = desde; }
        public String getHacia() { return hacia; }
        public void setHacia(String hacia) { this.hacia = hacia; }
    }
}