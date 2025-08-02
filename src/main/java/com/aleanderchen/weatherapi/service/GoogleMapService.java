package com.aleanderchen.weatherapi.service;

// import com.aleanderchen.weatherapi.util.HttpClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

// 

@Service
public class GoogleMapService {

    @Value("${google.apiKey}")
    private String googleApiKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebClient webClient;

    public String getCityFromCoordinates(double lat, double lng) {
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + lat + "," + lng + "&key=" + googleApiKey + "&language=zh-TW";

            // String json = HttpClientUtil.get(url);
            String json = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            JsonNode root = objectMapper.readTree(json);

            if (!"OK".equals(root.path("status").asText())) {
                return "查無結果";
            }

            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                JsonNode addressComponents = results.get(0).path("address_components");

                for (JsonNode component : addressComponents) {
                    JsonNode types = component.path("types");
                    for (JsonNode typeNode : types) {
                        String type = typeNode.asText();
                        if ("administrative_area_level_1".equals(type) || "locality".equals(type)) {
                            return component.path("long_name").asText();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "查無縣市名稱";
    }
}


