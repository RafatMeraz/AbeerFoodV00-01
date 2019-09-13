package com.example.abeerfoodv0_0.model;

public class Slider {
    private int shop_id;
    private String image;

    public Slider() {
    }

    public Slider(int shop_id, String image) {
        this.shop_id = shop_id;
        this.image = image;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
