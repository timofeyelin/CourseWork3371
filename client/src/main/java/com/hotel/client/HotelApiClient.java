package com.hotel.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.util.Map;

public class HotelApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
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
                .uri(URI.create(BASE_URL + "/register"))
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
                .uri(URI.create(BASE_URL + "/login"))
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
}