package com.example.myweatherplus;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherService {
    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    public static final String API_KEY = "425532784dd64135043c293173b893e5";
    public RequestQueue requestQueue;

    public WeatherService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void getCurrentWeather(String city, WeatherCallback callback) {
        String url = BASE_URL + "?lat=41.3851&lon=2.1734&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Gson gson = new Gson();
                            WeatherData weatherData = gson.fromJson(response.toString(), WeatherData.class);
                            Log.d("weather test", weatherData.getCityName());

                            callback.onSuccess(weatherData);

                            JSONArray forecastArray = response.getJSONArray("list");
                            List<WeatherData> forecastList = new ArrayList<>();

                            for (int i = 0; i < forecastArray.length(); i++) {
                                JSONObject forecastObject = forecastArray.getJSONObject(i);
                                WeatherData forecastWeatherData = gson.fromJson(forecastObject.toString(), WeatherData.class);
                                forecastList.add(forecastWeatherData);
                            }

                            callback.onForecastSuccess(forecastList);

                        } catch (JsonSyntaxException e) {
                            callback.onError(new VolleyError("JSON syntax error: " + e.getMessage()));
                        } catch (Exception e) {
                            callback.onError(new VolleyError("Error deserializing JSON: " + e.getMessage()));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        requestQueue.add(request);
    }

    public void getForecastWeather(String city, WeatherCallback callback) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Gson gson = new Gson();
                            JSONArray forecastArray = response.getJSONArray("list");
                            List<WeatherData> forecastWeatherList = new ArrayList<>();

                            for (int i = 0; i < forecastArray.length(); i++) {
                                JSONObject forecastObject = forecastArray.getJSONObject(i);
                                WeatherData forecastWeatherData = gson.fromJson(forecastObject.toString(), WeatherData.class);
                                forecastWeatherList.add(forecastWeatherData);
                            }

                            callback.onForecastSuccess(forecastWeatherList);
                        } catch (JsonSyntaxException e) {
                            callback.onError(new VolleyError("JSON syntax error: " + e.getMessage()));
                        } catch (Exception e) {
                            callback.onError(new VolleyError("Error deserializing JSON: " + e.getMessage()));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        requestQueue.add(request);
    }


    public interface WeatherCallback {
        void onSuccess(WeatherData weatherData);

        void onError(VolleyError error);

        void onForecastSuccess(List<WeatherData> forecastWeatherList);

    }
}
