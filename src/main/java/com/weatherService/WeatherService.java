package com.weatherService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String WEATHERSTACK_URL = "http://api.weatherstack.com/current?access_key=58b3c08c4024b9dc994726586467141f&query=Melbourne";
    private static final Map<String, WeatherResponse> cache = new ConcurrentHashMap<>();

    @Cacheable(value = "weather", key = "'melbourne'", unless = "#result == null")
    public WeatherResponse getWeather() {
        try {
            WeatherResponse response = fetchWeather(WEATHERSTACK_URL, "weatherstack");
            cache.put("melbourne", response);
            return response;
        } catch (Exception e) {
            try {
                WeatherResponse response = fetchWeather(WEATHERSTACK_URL, "weatherstack");
                cache.put("melbourne", response);
                return response;
            } catch (Exception ex) {
                return cache.getOrDefault("melbourne", new WeatherResponse(0, 0));
            }
        }
    }

    private WeatherResponse fetchWeather(String url, String provider) {
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseResponse(response.getBody(), provider);
        }
        throw new RuntimeException("Weather API failed");
    }

    private WeatherResponse parseResponse(Map<String, Object> response, String provider) {
        if (provider.equals("weatherstack")) {
            Map<String, Object> current = (Map<String, Object>) response.get("current");
            return new WeatherResponse((Integer) current.get("temperature"),(Integer) current.get("wind_speed"));
        } else {
            Map<String, Object> wind = (Map<String, Object>) response.get("wind");
            Map<String, Object> main = (Map<String, Object>) response.get("main");
            return new WeatherResponse((Integer) main.get("temp"), (Integer) wind.get("speed"));
        }
    }
}
