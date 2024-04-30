package com.task09;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

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
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://api.open-meteo.com/v1/forecast?latitude=" + this.latitude + "&longitude=" + this.longitude + "&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m")).header("accept", "application/json").build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return (String)response.body();
    }
}

