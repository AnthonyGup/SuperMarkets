package com.supermarkets.dto;

import com.supermarkets.pojo.Product;

public class ProductoDto {
    private String sucursalId;
    private String name;
    private String barcode;
    private String category;
    private String expiryDate;
    private String brand;
    private double price;
    private int stock;
    private String estado;

    public ProductoDto() {}

    public static ProductoDto fromProduct(Product p) {
        if (p == null) return null;
        ProductoDto dto = new ProductoDto();
        dto.setSucursalId(p.getSucursalId());
        dto.setName(p.getName());
        dto.setBarcode(p.getBarcode());
        dto.setCategory(p.getCategory());
        dto.setExpiryDate(p.getExpiryDate());
        dto.setBrand(p.getBrand());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setEstado(p.getEstado() != null ? p.getEstado().getValor() : "disponible");
        return dto;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}