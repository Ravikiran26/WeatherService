package com.weatherService;

public class WeatherResponse {

    private int temperature_degrees;
    private int wind_speed;

    public WeatherResponse(int temperature_degrees, int wind_speed) {
        this.temperature_degrees = temperature_degrees;
        this.wind_speed = wind_speed;
    }

    public int getTemperature_degrees() {
        return temperature_degrees;
    }

    public int getWind_speed() {
        return wind_speed;
    }
}
