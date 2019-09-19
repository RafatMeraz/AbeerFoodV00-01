package com.example.abeerfoodv0_0.model;

public class Request {
    private int id, shopId, quantity, status;
    private double price;
    private String itemList, shopName;

    public Request(int id, int shopId, int quantity, int status, double price, String itemList, String shopName) {
        this.id = id;
        this.shopId = shopId;
        this.quantity = quantity;
        this.price = price;
        this.itemList = itemList;
        this.status = status;
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
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

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getItemList() {
        return itemList;
    }

    public void setItemList(String itemList) {
        this.itemList = itemList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
