package com.dharma.shopinar.models;

public class CartModel {

    boolean izInCart;
    String path;
    String productType;
    String productId;
    int no_of_items;

    public CartModel() {
    }

    public CartModel(boolean izInCart, String path, String productType, String productId, int no_of_items) {
        this.izInCart = izInCart;
        this.path = path;
        this.productType = productType;
        this.productId = productId;
        this.no_of_items = no_of_items;
    }

    public boolean isIzInCart() {
        return izInCart;
    }

    public String getPath() {
        return path;
    }

    public String getProductType() {
        return productType;
    }

    public String getProductId() {
        return productId;
    }

    public int getNo_of_items() {
        return no_of_items;
    }
}
