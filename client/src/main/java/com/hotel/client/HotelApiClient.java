package com.hotel.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.util.Map;
import java.math.BigDecimal;
import java.util.HashMap;

public class HotelApiClient {
    private final HttpClient httpClient;
    private final Gson gson;

    public HotelApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public String register(String username, String password, String role) throws Exception {
        String jsonRequest = gson.toJson(Map.of(
                "username", username,
                "password", password,
                "role", role
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }

        return response.body();
    }

    public String login(String username, String password) throws Exception {
        String jsonRequest = gson.toJson(Map.of(
                "username", username,
                "password", password
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Неверное имя пользователя или пароль");
        }

        return response.body();
    }

    public void addRoom(String number, String type, BigDecimal price, String description) throws Exception {
        Map<String, String> roomData = new HashMap<>();
        roomData.put("number", number);
        roomData.put("type", type);
        roomData.put("price", price.toString());
        roomData.put("description", description);

        String jsonRequest = gson.toJson(roomData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rooms"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при добавлении номера");
        }
    }
    public void editRoom(Long roomId, String type, BigDecimal price, String description) throws Exception {
        Map<String, String> roomData = new HashMap<>();
        roomData.put("type", type);
        roomData.put("price", price.toString());
        if (description != null) {
            roomData.put("description", description);
        }

        String jsonRequest = gson.toJson(roomData);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rooms/" + roomId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при обновлении номера");
        }
    }
    public void deleteRoom(Long roomId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rooms/" + roomId))
                .DELETE()
                .build();
    
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при удалении номера");
        }
    }
}