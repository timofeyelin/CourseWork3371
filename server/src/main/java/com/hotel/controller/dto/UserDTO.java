package com.hotel.controller.dto;

import com.hotel.model.User;

public class UserDTO {
    private Long id;
    private String username; // Изменено с login на username
    private String role;

    public UserDTO() {}

    public UserDTO(User user) { // Добавляем конструктор из User
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() { // Изменено с getLogin на getUsername
        return username;
    }

    public void setUsername(String username) { // Изменено с setLogin на setUsername
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}