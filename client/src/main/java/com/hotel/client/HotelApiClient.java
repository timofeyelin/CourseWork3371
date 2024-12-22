package com.hotel.client;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hotel.dto.RoomDTO;
import com.hotel.model.Booking;
import com.hotel.model.User;

import java.time.LocalDate;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;

public class HotelApiClient {
    private final HttpClient httpClient;
    private final Gson gson;
    private static Long currentUserId;
    public HotelApiClient() {
        this.httpClient = HttpClient.newHttpClient();
    this.gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        })
        .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return LocalDate.parse(json.getAsString());
            }
        })
        .create();
    }
    public void setCurrentUserId(Long userId) {
        currentUserId = userId;
    }

    public static Long getCurrentUserId() {
        return currentUserId;
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
    
        // Парсинг userId из ответа
        User user = gson.fromJson(response.body(), User.class);
        setCurrentUserId(user.getId());
    
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

    public void editRoomByNumber(String roomNumber, String type, BigDecimal price, String description) throws Exception {
        Map<String, String> roomData = new HashMap<>();
        roomData.put("type", type);
        roomData.put("price", price.toString());
        if (description != null) {
            roomData.put("description", description);
        }

        String jsonRequest = gson.toJson(roomData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rooms/number/" + roomNumber))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при обновлении номера");
        }
    }

    public void deleteRoomByNumber(String roomNumber) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rooms/number/" + roomNumber))
                .DELETE()
                .build();
    
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при удалении номера");
        }
    }

    public List<RoomDTO> getAvailableRooms() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/rooms/available"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении доступных номеров");
        }

        return gson.fromJson(response.body(), new TypeToken<List<RoomDTO>>(){}.getType());
    }

    public void createBooking(String roomNumber, Long userId, String startDate, String endDate) throws Exception {
        Map<String, String> bookingData = new HashMap<>();
        bookingData.put("roomNumber", roomNumber);
        bookingData.put("userId", userId.toString());
        bookingData.put("startDate", startDate);
        bookingData.put("endDate", endDate);

        String jsonRequest = gson.toJson(bookingData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/bookings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при создании бронирования: " + response.body());
        }
    }

    public List<Booking> getUserBookings(Long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/bookings/user/" + userId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении бронирований: " + response.body());
        }

        return gson.fromJson(response.body(), new TypeToken<List<Booking>>(){}.getType());
    }
}