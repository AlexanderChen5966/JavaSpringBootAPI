package com.aleanderchen.weatherapi.service;

import com.aleanderchen.weatherapi.dto.WeatherResponse;
import com.aleanderchen.weatherapi.dto.WeatherResponse.ForecastPeriod;
import com.aleanderchen.weatherapi.util.HttpClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CwaApiService {

    @Value("${cwa.apiKey}")
    private String apiKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebClient webClient;


    @Cacheable(value = "forecast36hr", key = "#locationName")
    public WeatherResponse fetch36HourForecast(String locationName) {
        try {
            String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001"
                    + "?Authorization=" + apiKey
                    + "&locationName=" + locationName;

            JsonNode root = objectMapper.readTree(HttpClientUtil.get(url));
            JsonNode locations = root.path("records").path("location");

            if (!locations.isArray() || locations.size() == 0) {
                return new WeatherResponse(locationName, List.of());
            }

            JsonNode firstLocation = locations.get(0);
            String locName = firstLocation.path("locationName").asText();
            JsonNode weatherElements = firstLocation.path("weatherElement");

            Map<String, ForecastPeriod> periodMap = new LinkedHashMap<>();

            for (JsonNode element : weatherElements) {
                String elementName = element.path("elementName").asText();
                for (JsonNode time : element.path("time")) {
                    String startTime = time.path("startTime").asText();
                    String endTime = time.path("endTime").asText();
                    String key = startTime + "|" + endTime;
                    String value = time.path("parameter").path("parameterName").asText();

                    ForecastPeriod fp = periodMap.getOrDefault(key,
                            new ForecastPeriod(startTime, endTime, null, null, null, null));

                    switch (elementName) {
                        case "Wx" -> fp.setWx(value);
                        case "PoP" -> fp.setPop(value);
                        case "MinT" -> fp.setMinT(value);
                        case "MaxT" -> fp.setMaxT(value);
                    }
                    periodMap.put(key, fp);
                }
            }

            return new WeatherResponse(locName, new ArrayList<>(periodMap.values()));

        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherResponse(locationName, List.of());
        }
    }

    @Cacheable(value = "forecast3days", key = "#locationName")
    public WeatherResponse fetch3DayForecast(String locationName) {
        return fetchLongForecast(locationName, "F-D0047-089");
    }

    @Cacheable(value = "forecast7days", key = "#locationName")
    public WeatherResponse fetch7DayForecast(String locationName) {
        return fetchLongForecast(locationName, "F-D0047-091");
    }

    public WeatherResponse fetchLongForecast(String locationName, String dataset) {
        try {
            String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/" + dataset
                   + "?Authorization=" + apiKey
                   + "&LocationName=" + locationName
                   + "&ElementName=天氣預報綜合描述";

            String json = HttpClientUtil.get(url);
            JsonNode root = objectMapper.readTree(json);

            JsonNode locations = root.path("records").path("Locations");
            if (!locations.isArray() || locations.size() == 0) {
                return new WeatherResponse(locationName, List.of());
            }

            JsonNode locationArray = locations.get(0).path("Location");
            if (!locationArray.isArray() || locationArray.size() == 0) {
                return new WeatherResponse(locationName, List.of());
            }

            JsonNode location = locationArray.get(0);
            String locName = location.path("LocationName").asText();

            JsonNode weatherElements = location.path("WeatherElement");
            if (!weatherElements.isArray() || weatherElements.size() == 0) {
                return new WeatherResponse(locName, List.of());
            }

            JsonNode timeArray = weatherElements.get(0).path("Time");
            List<ForecastPeriod> forecastList = new ArrayList<>();

            for (JsonNode timeNode : timeArray) {
                String startTime = timeNode.path("StartTime").asText();
                String endTime = timeNode.path("EndTime").asText();
                String wx = timeNode.path("ElementValue").get(0).path("WeatherDescription").asText();

                ForecastPeriod period = new ForecastPeriod();
                period.setStartTime(startTime);
                period.setEndTime(endTime);
                period.setWx(wx); // 這裡 wx 表示天氣預測詳細描述

                forecastList.add(period);
            }

            return new WeatherResponse(locName, forecastList);

        } catch (Exception e) {
            e.printStackTrace();
            return new WeatherResponse(locationName, List.of());
        }
    }

}

