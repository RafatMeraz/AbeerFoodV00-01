package com.example.abeerfoodv0_0.model;

public class Slider {
    private int id;
    private String image;

    public Slider() {
    }

    public Slider(int id, String image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
