package com.example.myweatherplus;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherData {
    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private WeatherDetails weatherDetails;

    @SerializedName("weather")
    private List<Weather> weatherList;

    @SerializedName("dt")
    private long timestamp; // Campo de marca de tiempo

    public String getCityName() {
        return cityName;
    }

    public WeatherDetails getWeatherDetails() {
        return weatherDetails;
    }

    public List<Weather> getWeatherList() {
        return weatherList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static class WeatherDetails {
        @SerializedName("temp")
        private String temperature;

        public String getTemperature() {
            return temperature;
        }
    }

    public static class Weather {
        @SerializedName("icon")
        private String iconCode;

        public String getIconCode() {
            return iconCode;
        }
    }
}
