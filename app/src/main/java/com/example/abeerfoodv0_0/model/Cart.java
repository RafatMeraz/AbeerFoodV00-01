package com.example.abeerfoodv0_0.model;

public class Cart {
    private String productName;
    private int quantity, id;
    private double price;

    public Cart() {
    }

    public Cart(String productName, int quantity, int id, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.id = id;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
