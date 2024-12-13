package com.hotel.service;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // Проверка существования пользователя
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Хеширование пароля перед сохранением
        user.setPassword(hashPassword(user.getPassword()));

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> checkPassword(password, user.getPassword()))
                .orElse(null);
    }

    private String hashPassword(String password) {

        return password; // Временно без хеширования
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {

        return rawPassword.equals(hashedPassword);
    }
}