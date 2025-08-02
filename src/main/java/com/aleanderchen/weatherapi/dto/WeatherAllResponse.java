package com.aleanderchen.weatherapi.dto;

import java.util.List;

public class WeatherAllResponse {
    private String location;
    private List<WeatherResponse.ForecastPeriod> forecast36hr;
    private List<WeatherResponse.ForecastPeriod> forecast3days;
    private List<WeatherResponse.ForecastPeriod> forecast7days;

    public WeatherAllResponse(String location,
                              List<WeatherResponse.ForecastPeriod> forecast36hr,
                              List<WeatherResponse.ForecastPeriod> forecast3days,
                              List<WeatherResponse.ForecastPeriod> forecast7days) {
        this.location = location;
        this.forecast36hr = forecast36hr;
        this.forecast3days = forecast3days;
        this.forecast7days = forecast7days;
    }

    public String getLocation() {
        return location;
    }

    public List<WeatherResponse.ForecastPeriod> getForecast36hr() {
        return forecast36hr;
    }

    public List<WeatherResponse.ForecastPeriod> getForecast3days() {
        return forecast3days;
    }

    public List<WeatherResponse.ForecastPeriod> getForecast7days() {
        return forecast7days;
    }
}
