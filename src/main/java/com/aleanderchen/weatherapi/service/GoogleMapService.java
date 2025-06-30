package com.aleanderchen.weatherapi.service;

import com.aleanderchen.weatherapi.util.HttpClientUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleMapService {

    @Value("${google.apiKey}")
    private String googleApiKey;

    public String getCityFromCoordinates(double lat, double lng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" 
                   + lat + "," + lng + "&key=" + googleApiKey + "&language=zh-TW";

        String json = HttpClientUtil.get(url);
        JSONObject root = new JSONObject(json);

        if (!root.getString("status").equals("OK")) {
            return "查無結果";
        }

        JSONArray results = root.getJSONArray("results");
        if (results.length() == 0) {
            return "查無結果";
        }

        JSONArray addressComponents = results.getJSONObject(0).getJSONArray("address_components");

        for (int i = 0; i < addressComponents.length(); i++) {
            JSONObject component = addressComponents.getJSONObject(i);
            JSONArray types = component.getJSONArray("types");

            for (int j = 0; j < types.length(); j++) {
                String type = types.getString(j);
                if (type.equals("administrative_area_level_1") || type.equals("locality")) {
                    return component.getString("long_name");
                }
            }
        }

        return "查無縣市名稱";
    }
}
