package com.example.myweatherplus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements WeatherService.WeatherCallback {
    private ImageView imageViewWeatherIcon;
    private TextView textViewTemperature;
    private TextView textViewLocation;
    private TextView textViewDate;

    private WeatherService weatherService;

    private List<WeatherData> forecastWeatherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewDate = findViewById(R.id.textViewForecastDate);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        weatherService = new WeatherService(requestQueue);

        getWeatherData("Barcelona");

        forecastWeatherList = new ArrayList<>();
    }

    private void getWeatherData(String city) {
        weatherService.getCurrentWeather(city, this);
    }

    private void updateForecastUI() {
        LinearLayout linearLayoutForecast = findViewById(R.id.linearLayoutForecast);

        linearLayoutForecast.removeAllViews();

        forecastWeatherList.remove(0);

        Calendar calendar = Calendar.getInstance();

        int maxDaysToShow = Math.min(forecastWeatherList.size(), 5);

        for (int i = 0; i < maxDaysToShow; i++) {
            WeatherData forecastWeatherData = forecastWeatherList.get(i);

            View forecastView = getLayoutInflater().inflate(R.layout.activity_future_days, linearLayoutForecast, false);

            ImageView imageViewForecastIcon = forecastView.findViewById(R.id.imageViewForecastIcon);
            TextView textViewForecastTemperature = forecastView.findViewById(R.id.textViewForecastTemperature);
            TextView textViewForecastDate = forecastView.findViewById(R.id.textViewForecastDate);

            String forecastTemperature = forecastWeatherData.getWeatherDetails().getTemperature();
            String formattedTemperature = formatTemperature(forecastTemperature);
            textViewForecastTemperature.setText(formattedTemperature);

            List<WeatherData.Weather> weatherList = forecastWeatherData.getWeatherList();

            if (weatherList != null && !weatherList.isEmpty()) {
                WeatherData.Weather weather = weatherList.get(0);
                String iconCode = weather.getIconCode();

                int iconResourceId = getIconResourceId(iconCode);
                imageViewForecastIcon.setImageResource(iconResourceId);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date forecastDate = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", new Locale("es", "ES"));

            String formattedDate = dateFormat.format(forecastDate);
            textViewForecastDate.setText(formattedDate);

            linearLayoutForecast.addView(forecastView);
        }
    }

    private int getIconResourceId(String iconCode) {
        switch (iconCode) {
            case "01d":
                return R.drawable.sol;
            case "02d":
                return R.drawable.nubesol;
            case "03d":
            case "04d":
                return R.drawable.nubes;
            case "09d":
                return R.drawable.lluvia;
            case "10d":
                return R.drawable.nubesol;
            case "11d":
                return R.drawable.tormenta;
            case "13d":
                return R.drawable.nieve;
            case "50d":
                return R.drawable.niebla;
            case "01n":
                return R.drawable.solnoche;
            case "02n":
                return R.drawable.nubesolnoche;
            case "03n":
            case "04n":
                return R.drawable.nubesnoche;
            case "09n":
                return R.drawable.lluvianoche;
            case "10n":
                return R.drawable.nubesolnoche;
            case "11n":
                return R.drawable.tormentanoche;
            case "13n":
                return R.drawable.nievenoche;
            case "50n":
                return R.drawable.niebla;
            default:
                return R.drawable.error;
        }
    }

    private String formatTemperature(String temperature) {
        double temperatureValue = Double.parseDouble(temperature);
        int roundedTemperature = (int) Math.round(temperatureValue);
        return String.format(Locale.getDefault(), "%dºC", roundedTemperature);
    }

    @Override
    public void onSuccess(WeatherData weatherData) {
        String temperature = weatherData.getWeatherDetails().getTemperature();
        String formattedTemperature = formatTemperature(temperature);
        String location = weatherData.getCityName();
        textViewTemperature.setText(formattedTemperature);
        textViewLocation.setText(location);

        List<WeatherData.Weather> weatherList = weatherData.getWeatherList();

        if (weatherList != null && !weatherList.isEmpty()) {
            WeatherData.Weather weather = weatherList.get(0);
            String iconCode = weather.getIconCode();

            int iconResourceId = getIconResourceId(iconCode);
            imageViewWeatherIcon.setImageResource(iconResourceId);
        }

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", new Locale("es", "ES"));
        String formattedDate = dateFormat.format(currentDate);
        textViewDate.setText(formattedDate);

        weatherService.getForecastWeather(weatherData.getCityName(), this);
    }

    @Override
    public void onError(VolleyError error) {
        error.printStackTrace();
    }

    @Override
    public void onForecastSuccess(List<WeatherData> forecastWeatherList) {
        this.forecastWeatherList = forecastWeatherList;
        updateForecastUI();
    }
}
