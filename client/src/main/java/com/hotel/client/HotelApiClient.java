package com.hotel.client;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.hotel.dto.RoomDTO;
import com.hotel.dto.UserDTO;
import com.hotel.dto.BookingDTO;
import com.hotel.model.Booking;
import com.hotel.model.User;

import java.time.LocalDate;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HotelApiClient {
    private final HttpClient httpClient;
    private final Gson gson;
    private static Long currentUserId;
    private static final String BASE_URL = "http://localhost:8080/api";

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

    public void register(String username, String password, String role) throws Exception {
        String jsonRequest = gson.toJson(Map.of(
                "username", username,
                "password", password,
                "role", role
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        sendRequest(request);

    }

    public User login(String username, String password) throws Exception {
        String jsonRequest = gson.toJson(Map.of(
                "username", username,
                "password", password
        ));
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            // Парсинг сообщения об ошибке
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> error = gson.fromJson(response.body(), mapType);
            String errorMessage = error.getOrDefault("error", "Неизвестная ошибка");
            throw new RuntimeException("Ошибка при входе: " + errorMessage);
        }
    
        // Парсинг объекта User из успешного ответа
        User user = gson.fromJson(response.body(), User.class);
        setCurrentUserId(user.getId());
    
        return user;
    }

        public List<UserDTO> getAllUsers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении пользователей: " + response.body());
        }

        return gson.fromJson(response.body(), new TypeToken<List<UserDTO>>(){}.getType());
    }

    public List<BookingDTO> getBookingsByUserId(Long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings/user/" + userId + "/details"))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении бронирований: " + response.body());
        }

        return gson.fromJson(response.body(), new TypeToken<List<BookingDTO>>(){}.getType());
    }

    // Общий метод для отправки запросов
    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void addRoom(String number, String type, BigDecimal price, String description, List<String> photos) throws Exception {
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("number", number);
        roomData.put("type", type);
        roomData.put("price", price.toString());
        roomData.put("description", description);
        roomData.put("photos", photos);
    
        String jsonRequest = gson.toJson(roomData);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/rooms"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при добавлении номера: " + response.body());
        }
    }

    public void editRoomByNumber(String roomNumber, String type, BigDecimal price, String description, List<String> photos) throws Exception {
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("type", type);
        roomData.put("price", price.toString());
        if (description != null) {
            roomData.put("description", description);
        }
        roomData.put("photos", photos);
    
        String jsonRequest = gson.toJson(roomData);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/rooms/number/" + URLEncoder.encode(roomNumber, StandardCharsets.UTF_8)))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при обновлении номера: " + response.body());
        }
    }

    public void deleteRoomByNumber(String roomNumber) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/rooms/number/" + URLEncoder.encode(roomNumber, StandardCharsets.UTF_8)))
                .DELETE()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при удалении номера: " + response.body());
        }
    }

    public List<RoomDTO> getAvailableRooms() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/rooms/available"))
                .GET()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении доступных номеров: " + response.body());
        }
    
        return gson.fromJson(response.body(), new TypeToken<List<RoomDTO>>(){}.getType());
    }

    public boolean checkRoomAvailability(String roomNumber, String startDate, String endDate) throws Exception {
        String encodedRoomNumber = URLEncoder.encode(roomNumber, StandardCharsets.UTF_8);
        String url = String.format("%s/bookings/check-availability?roomNumber=%s&startDate=%s&endDate=%s",
                BASE_URL,
                encodedRoomNumber,
                URLEncoder.encode(startDate, StandardCharsets.UTF_8),
                URLEncoder.encode(endDate, StandardCharsets.UTF_8));
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при проверке доступности номера: " + response.body());
        }
    
        return Boolean.parseBoolean(response.body());
    }

    public List<RoomDTO> getAllRooms() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/rooms"))
                .GET()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при загрузке номеров: " + response.body());
        }
    
        return gson.fromJson(response.body(), new TypeToken<List<RoomDTO>>() {}.getType());
    }

    public void createBooking(String roomNumber, Long userId, String startDate, String endDate) throws Exception {
        Map<String, String> bookingData = new HashMap<>();
        bookingData.put("roomNumber", roomNumber);
        bookingData.put("userId", userId.toString());
        bookingData.put("startDate", startDate);
        bookingData.put("endDate", endDate);
    
        String jsonRequest = gson.toJson(bookingData);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при создании бронирования: " + response.body());
        }
    }

    public List<Booking> getUserBookings(Long userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/bookings/user/" + userId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении бронирований: " + response.body());
        }

        return gson.fromJson(response.body(), new TypeToken<List<Booking>>(){}.getType());
    }

    public void cancelBooking(Long bookingId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings/" + bookingId))
                .DELETE()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при отмене бронирования: " + response.body());
        }
    }

    public List<BookingDTO> getBookingByRoomNumber(String roomNumber) throws Exception {
        String encodedRoomNumber = URLEncoder.encode(roomNumber, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings/room/" + encodedRoomNumber))
                .GET()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении бронирований: " + response.body());
        }
    
        Type listType = new TypeToken<List<BookingDTO>>(){}.getType();
        return gson.fromJson(response.body(), listType);
    }

    public void updateBooking(Long bookingId, String startDate, String endDate) throws Exception {
        Map<String, String> bookingData = new HashMap<>();
        bookingData.put("startDate", startDate);
        bookingData.put("endDate", endDate);
    
        String jsonRequest = gson.toJson(bookingData);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings/" + bookingId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при обновлении бронирования: " + response.body());
        }
    }

    public void deleteBooking(Long bookingId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings/" + bookingId))
                .DELETE()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при удалении бронирования: " + response.body());
        }
    }

    public List<BookingDTO> getAllBookingsByRoomNumber(String roomNumber) throws Exception {
        String encodedRoomNumber = URLEncoder.encode(roomNumber, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bookings/room/" + encodedRoomNumber + "/all"))
                .GET()
                .build();
    
        HttpResponse<String> response = sendRequest(request);
    
        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка при получении бронирований: " + response.body());
        }
    
        Type listType = new TypeToken<List<BookingDTO>>(){}.getType();
        return gson.fromJson(response.body(), listType);
    }

    public List<RoomDTO> getFilteredRooms(String roomType, BigDecimal minPrice, BigDecimal maxPrice, LocalDate startDate, LocalDate endDate) throws IOException {
        String encodedRoomType = URLEncoder.encode(roomType, StandardCharsets.UTF_8);
        String url = String.format("%s/rooms/search?type=%s&minPrice=%s&maxPrice=%s&startDate=%s&endDate=%s",
                BASE_URL,
                encodedRoomType,
                minPrice != null ? URLEncoder.encode(minPrice.toString(), StandardCharsets.UTF_8) : "",
                maxPrice != null ? URLEncoder.encode(maxPrice.toString(), StandardCharsets.UTF_8) : "",
                startDate != null ? URLEncoder.encode(startDate.toString(), StandardCharsets.UTF_8) : "",
                endDate != null ? URLEncoder.encode(endDate.toString(), StandardCharsets.UTF_8) : ""
        );
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    
        try {
            HttpResponse<String> response = sendRequest(request);
            if (response.statusCode() != 200) {
                throw new RuntimeException("Ошибка при получении отфильтрованных номеров: " + response.body());
            }
    
            Type listType = new TypeToken<List<RoomDTO>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        } catch (InterruptedException e) {
            throw new IOException("Запрос прерван", e);
        }
    }
}