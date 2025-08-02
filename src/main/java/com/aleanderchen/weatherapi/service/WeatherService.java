package com.aleanderchen.weatherapi.service;

import com.aleanderchen.weatherapi.dto.WeatherAllResponse;
import com.aleanderchen.weatherapi.dto.WeatherResponse;
import com.aleanderchen.weatherapi.dto.WeatherResponse.ForecastPeriod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    private final CwaApiService cwaApiService;
    private final GoogleMapService googleMapService;

    public WeatherService(CwaApiService cwaApiService, GoogleMapService googleMapService) {
        this.cwaApiService = cwaApiService;
        this.googleMapService = googleMapService;
    }

    public WeatherResponse get36HourForecast(String locationName) {
        return cwaApiService.fetch36HourForecast(locationName);
    }

    public WeatherResponse get3DayForecast(String locationName) {
        return cwaApiService.fetch3DayForecast(locationName);
    }

    public WeatherResponse get7DayForecast(String locationName) {
        return cwaApiService.fetch7DayForecast(locationName);
    }

    public String reverseGeocode(double lat, double lng) {
        return googleMapService.getCityFromCoordinates(lat, lng);
    }

    public WeatherAllResponse getAllForecastsByLatLng(double lat, double lng) {
        String city = googleMapService.getCityFromCoordinates(lat, lng);
        if (city.equals("查無結果") || city.equals("查無縣市名稱")) {
            return new WeatherAllResponse("未知地點", List.of(), List.of(), List.of());
        }

        List<ForecastPeriod> forecast36hr = cwaApiService.fetch36HourForecast(city).getDescription();
        List<ForecastPeriod> forecast3days = cwaApiService.fetch3DayForecast(city).getDescription();
        List<ForecastPeriod> forecast7days = cwaApiService.fetch7DayForecast(city).getDescription();

        return new WeatherAllResponse(city, forecast36hr, forecast3days, forecast7days);
    }
}
