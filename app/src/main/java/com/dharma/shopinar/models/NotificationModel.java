package com.dharma.shopinar.models;

import java.util.List;

public class NotificationModel {

    private String imageURL;
    private  String name;
    private String description;

    public NotificationModel() {
    }

    public NotificationModel(String imageURL, String name, String description) {
        this.imageURL = imageURL;
        this.name = name;
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
