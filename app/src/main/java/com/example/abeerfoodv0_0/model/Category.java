package com.example.abeerfoodv0_0.model;

public class Category {
    private int categoryId;
    private String categoryName, image;

    public Category(int categoryId, String categoryName, String image) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.image = image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
