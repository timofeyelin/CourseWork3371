package com.hotel.model;

public class Room {
    private int id;
    private String type;
    private double price;
    private boolean isAvailable;
    private String description;

    public Room (int id, String type, double price, boolean isAvailable, String description) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.isAvailable = isAvailable;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
