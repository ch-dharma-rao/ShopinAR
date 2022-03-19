package com.dharma.shopinar.models;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private String name;
    private Double price;
    private Double cuttedPrice;
    private List<String> images;
    private boolean isAR;
    private int quantity;
    private int rating;
    private String description;
    private String modelURL;

    public Item() {
    }

    public Item(String name, Double price, Double cuttedPrice, List<String> images, boolean isAR, int quantity, int rating, String description, String modelURL) {
        this.name = name;
        this.price = price;
        this.cuttedPrice = cuttedPrice;
        this.images = images;
        this.isAR = isAR;
        this.quantity = quantity;
        this.rating = rating;
        this.description = description;
        this.modelURL = modelURL;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Double getCuttedPrice() {
        return cuttedPrice;
    }

    public List<String> getImages() {
        return images;
    }

    public boolean isAR() {
        return isAR;
    }

    public int getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getModelURL() {
        return modelURL;
    }

    public boolean getIsAR() {
        return isAR;
    }

    public int getQuantity() {
        return quantity;
    }
}