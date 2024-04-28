package com.task08;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class OpenMeteoAPI {
    private static final String BASE_URL = "http://api.open-meteo.com/v1/forecast";

    private double latitude;
    private double longitude;

    public OpenMeteoAPI(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getWeatherForecast() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "?latitude=" + latitude + "&longitude=" + longitude))
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}