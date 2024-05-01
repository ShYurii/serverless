package com.task09;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "cmtr-c5efef97-Weather-test")
public class WeatherRecord {
    private String id;
    private Forecast forecast;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Forecast getForecast() {
        return forecast;
    }

    @DynamoDBAttribute(attributeName = "forecast")
    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        return "WeatherRecord{" +
                "id='" + id + '\'' +
                ", forecast=" + forecast +
                '}';
    }
}