package com.hotel.model;

public class Booking {
    private int id;
    private int roomId;
    private int clientId;
    private String startDate;
    private String endDate;

    // Конструкторы
    public Booking() {}

    public Booking(int id, int roomId, int clientId, String startDate, String endDate) {
        this.id = id;
        this.roomId = roomId;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}