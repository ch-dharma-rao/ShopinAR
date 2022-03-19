package com.dharma.shopinar.models;

public class ListItem {
    private  String name;
    private  String id;
    private  String path;
    private  Item item;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public Item getItem() {
        return item;
    }

    public ListItem(String name, String id, String path, Item item) {
        this.name = name;
        this.id = id;
        this.path = path;
        this.item = item;
    }
}
