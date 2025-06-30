package com.aleanderchen.weatherapi.controller;

import com.aleanderchen.weatherapi.dto.WeatherResponse;
import com.aleanderchen.weatherapi.dto.WeatherAllResponse;
import com.aleanderchen.weatherapi.service.WeatherService;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Weather API", description = "提供台灣地區各式天氣資料")
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(summary = "取得36小時天氣預報")
    @GetMapping("/forecast36hr")
    public WeatherResponse get36HourForecast(@RequestParam String locationName) {
        return weatherService.get36HourForecast(locationName);
    }

    @Operation(summary = "取得3日天氣預報")
    @GetMapping("/forecast3days")
    public WeatherResponse get3DayForecast(@RequestParam String locationName) {
        return weatherService.get3DayForecast(locationName);
    }

    @Operation(summary = "取得7日天氣預報")
    @GetMapping("/forecast7days")
    public WeatherResponse get7DayForecast(@RequestParam String locationName) {
        return weatherService.get7DayForecast(locationName);
    }

    @Operation(summary = "根據經緯度取得地區名稱")
    @GetMapping("/geocode")
    public String getCityFromLatLng(@RequestParam double lat, @RequestParam double lng) {
        return weatherService.reverseGeocode(lat, lng);
    }

//    @Operation(summary = "取得統一天氣預報（輸入經緯度）")
    @GetMapping
    public WeatherAllResponse getWeatherByLatLng(@RequestParam double lat, @RequestParam double lng) {
        return weatherService.getAllForecastsByLatLng(lat, lng);
    }

    @Operation(summary = "取得所有台灣縣市清單")
    @GetMapping("/cities")
    public List<String> getAllCities() {
        return List.of(
            "臺北市", "新北市", "桃園市", "臺中市", "臺南市", "高雄市",
            "基隆市", "新竹市", "嘉義市", 
            "新竹縣", "苗栗縣", "彰化縣", "南投縣", "雲林縣",
            "嘉義縣", "屏東縣", "宜蘭縣", "花蓮縣", "臺東縣",
            "澎湖縣", "金門縣", "連江縣"
        );
    }

}
