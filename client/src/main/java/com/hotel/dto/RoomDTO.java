package com.hotel.dto;

import java.math.BigDecimal;
import java.util.List;

import com.hotel.model.Room;

public class RoomDTO {
    private Long id;
    private Room room;
    private String number;
    private String type;
    private BigDecimal price;
    private String description;
    private List<String> photos;

    // Default Constructor
    public RoomDTO() {}

    // Parameterized Constructor
    public RoomDTO(Long id, String number, String type, BigDecimal price, String description, List<String> photos) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.price = price;
        this.description = description;
        this.photos = photos;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Room getRoom() {
            return room;
    }

    @Override
    public String toString() {
        return "RoomDTO{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", photos=" + photos +
                '}';
    }
}