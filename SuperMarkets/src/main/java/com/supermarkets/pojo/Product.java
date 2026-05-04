package com.supermarkets.pojo;

public class Product {
    public enum Estado {
        DISPONIBLE("disponible"),
        EN_TRANSITO("en transito"),
        AGOTADO("agotado");

        private final String valor;

        Estado(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }

    private String sucursalId;
    private String name;
    private String barcode;
    private String category;
    private String expiryDate;
    private String brand;
    private double price;
    private int stock;
    private Estado estado;

    public Product() {
        this.price = 0.0;
        this.stock = 0;
        this.estado = Estado.DISPONIBLE;
    }

    public String getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(String sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
