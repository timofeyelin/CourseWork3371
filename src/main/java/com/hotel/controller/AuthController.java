package main.java.com.hotel.controller;

import main.java.com.hotel.model.User;
import main.java.com.hotel.service.UserService;

import java.sql.SQLException;

public class AuthController {
    private UserService userService = new UserService();

    public void register(String username, String password, String role) throws SQLException {
        userService.registerUser(username, password, role);
    }

    public User login(String username, String password) throws SQLException {
        return userService.authenticateUser(username, password);
    }
}