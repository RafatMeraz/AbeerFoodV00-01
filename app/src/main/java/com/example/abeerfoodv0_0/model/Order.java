package com.example.abeerfoodv0_0.model;

public class Order {
    private int id, userId, shopId, quantity, driverId, status, paymentMethod, isDelivered;
    private double price, latitude, longitude;
    private String itemList, address, note, transactionID;

    public Order() {
    }

    public Order(int userId, int shopId, int quantity, double price, double latitude, double longitude, String itemList, String address, String note, int paymentMethod, String transactionID) {
        this.userId = userId;
        this.shopId = shopId;
        this.quantity = quantity;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemList = itemList;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.transactionID = transactionID;
        this.note = note;
    }

    public Order(int id, int userId, int shopId, int quantity, int driverId, int status, double price, double latitude, double longitude, String itemList, String address, String note, int paymentMethod, String transactionID) {
        this.id = id;
        this.userId = userId;
        this.shopId = shopId;
        this.quantity = quantity;
        this.driverId = driverId;
        this.status = status;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemList = itemList;
        this.address = address;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.transactionID = transactionID;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getItemList() {
        return itemList;
    }

    public void setItemList(String itemList) {
        this.itemList = itemList;
    }
}
