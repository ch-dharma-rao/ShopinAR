package com.dharma.shopinar.models;

public class CartListItem {
    private  String name;
    private  String id;
    private  String path;
    private  CartModel cartItem;
    private Item item;

    public CartListItem(String name, String id, String path, CartModel cartItem, Item item) {
        this.name = name;
        this.id = id;
        this.path = path;
        this.cartItem = cartItem;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public CartModel getCartItem() {
        return cartItem;
    }

    public Item getItem() {
        return item;
    }
}
