package com.dharma.shopinar.models;

public class WishlistModel {

    boolean izInWishlist;
    String path;
    String productType;
    String productId;

    public WishlistModel() {
    }

    public WishlistModel(boolean izInWishlist, String path, String productType, String productId) {
        this.izInWishlist = izInWishlist;
        this.path = path;
        this.productType = productType;
        this.productId = productId;
    }

    public boolean isIzInWishlist() {
        return izInWishlist;
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

    public void setIzInWishlist(boolean izInWishlist) {
        this.izInWishlist = izInWishlist;
    }
}
