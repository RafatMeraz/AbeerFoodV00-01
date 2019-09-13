package com.example.abeerfoodv0_0.model;

import java.io.Serializable;

public class Food implements Serializable {
    private int id, available;
    private String title, about, image;
    private double price;

    public Food() {
    }

    public Food(int id, int available, String title, String about, String image, double price) {
        this.id = id;
        this.available = available;
        this.title = title;
        this.about = about;
        this.image = image;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
