package com.example.abeerfoodv0_0.model;

import java.io.Serializable;

public class Shop implements Serializable {
    private int id, isOpen;
    private String shopName, slug, phoneNumber, tradeLicence, openAt, closeAT, location, image;

    public Shop() {
    }

    public Shop(int id, int isOpen, String shopName, String slug, String phoneNumber, String openAt, String closeAT, String location, String image) {
        this.id = id;
        this.isOpen = isOpen;
        this.shopName = shopName;
        this.slug = slug;
        this.phoneNumber = phoneNumber;
        this.openAt = openAt;
        this.closeAT = closeAT;
        this.location = location;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTradeLicence() {
        return tradeLicence;
    }

    public void setTradeLicence(String tradeLicence) {
        this.tradeLicence = tradeLicence;
    }

    public String getOpenAt() {
        return openAt;
    }

    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    public String getCloseAT() {
        return closeAT;
    }

    public void setCloseAT(String closeAT) {
        this.closeAT = closeAT;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
