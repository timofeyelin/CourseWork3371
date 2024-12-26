package com.hotel.dto;

public class UserDTO {
    private Long id;
    private String username; // Изменено с login на username
    private String role;

    // Конструктор по умолчанию
    public UserDTO() {}

    // Параметризованный конструктор
    public UserDTO(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
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