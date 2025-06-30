//package com.aleanderchen.weatherapi.service;
//
//import com.aleanderchen.weatherapi.dto.WeatherResponse;
//import com.aleanderchen.weatherapi.util.HttpClientUtil;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CwaApiService {
//
//    @Value("${cwa.apiKey}")
//    private String apiKey;
//
//    public WeatherResponse fetch36HourForecast(String locationName) {
//        String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001"
//                   + "?Authorization=" + apiKey
//                   + "&locationName=" + locationName;
//
//        String json = HttpClientUtil.get(url);
//        JSONObject root = new JSONObject(json);
//        JSONArray locations = root.getJSONObject("records").getJSONArray("location");
//
//        if (locations.length() > 0) {
//            JSONObject firstLocation = locations.getJSONObject(0);
//            String location = firstLocation.getString("locationName");
//            JSONArray weatherElements = firstLocation.getJSONArray("weatherElement");
//
//            JSONArray times = weatherElements.getJSONObject(0).getJSONArray("time");
//
//            StringBuilder description = new StringBuilder();
//            for (int i = 0; i < times.length(); i++) {
//                JSONObject timeBlock = times.getJSONObject(i);
//                String start = timeBlock.getString("startTime");
//                String end = timeBlock.getString("endTime");
//                String value = timeBlock.getJSONObject("parameter").getString("parameterName");
//
//                description.append(start).append(" ~ ").append(end).append(": ").append(value).append("\n");
//            }
//
//            return new WeatherResponse(location, description.toString());
//        }
//
//        return new WeatherResponse(locationName, "查無資料");
//    }
//
//    public WeatherResponse fetch3DayForecast(String locationName) {
//        return fetchLongForecast(locationName, "F-D0047-089");
//    }
//
//    public WeatherResponse fetch7DayForecast(String locationName) {
//        return fetchLongForecast(locationName, "F-D0047-091");
//    }
//
////    private WeatherResponse fetchLongForecast(String locationName, String dataset) {
////        String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/" + dataset
////                   + "?Authorization=" + apiKey
////                   + "&locationName=" + locationName
////                   + "&elementName=天氣預報綜合描述";
////        System.out.println("API Request URL: " + url);
////        String json = HttpClientUtil.get(url);
////        JSONObject root = new JSONObject(json);
////        JSONArray locations = root.getJSONObject("records").getJSONArray("locations").getJSONObject(0)
////                                  .getJSONArray("location");
////
////        if (locations.length() > 0) {
////            JSONObject location = locations.getJSONObject(0);
////            String locName = location.getString("locationName");
////
////            JSONArray elements = location.getJSONArray("weatherElement");
////            JSONObject descElement = elements.getJSONObject(0);
////
////            JSONArray times = descElement.getJSONArray("time");
////
////            StringBuilder description = new StringBuilder();
////            for (int i = 0; i < times.length(); i++) {
////                JSONObject time = times.getJSONObject(i);
////                String startTime = time.getString("startTime");
////                String endTime = time.getString("endTime");
////                String value = time.getJSONArray("elementValue").getJSONObject(0).getString("value");
////
////                description.append(startTime).append(" ~ ").append(endTime).append(": ").append(value).append("\n");
////            }
////
////            return new WeatherResponse(locName, description.toString());
////        }
////
////        return new WeatherResponse(locationName, "查無資料");
////    }
//    
//    private WeatherResponse fetchLongForecast(String locationName, String dataset) {
//        String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/" + dataset
//                   + "?Authorization=" + apiKey
//                   + "&LocationName=" + locationName
//                   + "&ElementName=天氣預報綜合描述";
//
//        String json = HttpClientUtil.get(url);
//        JSONObject root = new JSONObject(json);
//
//        // 確保 records 存在並且包含 locations
//        if (!root.has("records") || !root.getJSONObject("records").has("Locations")) {
//            System.out.println("API 回應格式異常，缺少 'Locations' 欄位");
//            return new WeatherResponse(locationName, "查無資料");
//        }
//
//        JSONArray locations = root.getJSONObject("records").getJSONArray("Locations").getJSONObject(0)
//                                  .getJSONArray("Location");
//
//        if (locations.length() > 0) {
//            JSONObject location = locations.getJSONObject(0);
//            String locName = location.getString("LocationName");
//
//            JSONArray elements = location.getJSONArray("WeatherElement");
//            JSONObject descElement = elements.getJSONObject(0);
//
//            JSONArray times = descElement.getJSONArray("Time");
//
//            StringBuilder description = new StringBuilder();
//            for (int i = 0; i < times.length(); i++) {
//                JSONObject time = times.getJSONObject(i);
//                String startTime = time.getString("StartTime");
//                String endTime = time.getString("EndTime");
//                String value = time.getJSONArray("ElementValue").getJSONObject(0).getString("WeatherDescription");
//
//                description.append(startTime).append(" ~ ").append(endTime).append(": ").append(value).append("\n");
//            }
//
//            return new WeatherResponse(locName, description.toString());
//        }
//
//        return new WeatherResponse(locationName, "查無資料");
//    }
//
//}


package com.aleanderchen.weatherapi.service;

import com.aleanderchen.weatherapi.dto.WeatherResponse;
import com.aleanderchen.weatherapi.dto.WeatherResponse.ForecastPeriod;
import com.aleanderchen.weatherapi.util.HttpClientUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CwaApiService {

    @Value("${cwa.apiKey}")
    private String apiKey;

    @Cacheable(value = "forecast36hr", key = "#locationName")
    public WeatherResponse fetch36HourForecast(String locationName) {
        String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001"
                + "?Authorization=" + apiKey
                + "&locationName=" + locationName;

        String json = HttpClientUtil.get(url);
        JSONObject root = new JSONObject(json);
        JSONArray locations = root.getJSONObject("records").getJSONArray("location");

        if (locations.length() > 0) {
            JSONObject firstLocation = locations.getJSONObject(0);
            String locName = firstLocation.getString("locationName");
            JSONArray weatherElements = firstLocation.getJSONArray("weatherElement");

            Map<String, ForecastPeriod> periodMap = new LinkedHashMap<>();

            for (int i = 0; i < weatherElements.length(); i++) {
                JSONObject element = weatherElements.getJSONObject(i);
                String elementName = element.getString("elementName");
                JSONArray times = element.getJSONArray("time");

                for (int j = 0; j < times.length(); j++) {
                    JSONObject time = times.getJSONObject(j);
                    String startTime = time.getString("startTime");
                    String endTime = time.getString("endTime");
                    String key = startTime + "|" + endTime;
                    String value = time.getJSONObject("parameter").getString("parameterName");

                    ForecastPeriod fp = periodMap.getOrDefault(key,
                        new ForecastPeriod(startTime, endTime, null, null, null, null));

                    switch (elementName) {
                        case "Wx": fp.setWx(value); break;
                        case "PoP": fp.setPop(value); break;
                        case "MinT": fp.setMinT(value); break;
                        case "MaxT": fp.setMaxT(value); break;
                    }
                    periodMap.put(key, fp);
                }
            }

            return new WeatherResponse(locName, new ArrayList<>(periodMap.values()));
        }

        return new WeatherResponse(locationName, List.of());
}


    @Cacheable(value = "forecast3days", key = "#locationName")
    public WeatherResponse fetch3DayForecast(String locationName) {
        return fetchLongForecast(locationName, "F-D0047-089");
    }

    @Cacheable(value = "forecast7days", key = "#locationName")
    public WeatherResponse fetch7DayForecast(String locationName) {
        return fetchLongForecast(locationName, "F-D0047-091");
    }

    private WeatherResponse fetchLongForecast(String locationName, String dataset) {
        String url = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/" + dataset
                   + "?Authorization=" + apiKey
                   + "&LocationName=" + locationName
                   + "&ElementName=天氣預報綜合描述";

        String json = HttpClientUtil.get(url);
        JSONObject root = new JSONObject(json);
	    if (!root.has("records") || !root.getJSONObject("records").has("Locations")) {
		    System.out.println("API 回應格式異常，缺少 'Locations' 欄位");
	//	      return new WeatherResponse(locationName, "查無資料");
	    }

      	JSONArray locations = root.getJSONObject("records").getJSONArray("Locations").getJSONObject(0)
                            .getJSONArray("Location");

        if (locations.length() > 0) {
            JSONObject location = locations.getJSONObject(0);
            String locName = location.getString("LocationName");
            JSONArray elements = location.getJSONArray("WeatherElement");
            JSONObject descElement = elements.getJSONObject(0);
            JSONArray times = descElement.getJSONArray("Time");

            List<ForecastPeriod> forecastList = new ArrayList<>();
            for (int i = 0; i < times.length(); i++) {
                JSONObject time = times.getJSONObject(i);
                if (!time.has("StartTime") || !time.has("EndTime")) {
                	 System.out.println("Skipped time block: " + time.toString());
                    continue; // 跳過這筆資料
                }

                String startTime = time.getString("StartTime");
                String endTime = time.getString("EndTime");
                String value = time.getJSONArray("ElementValue").getJSONObject(0).getString("WeatherDescription");

                forecastList.add(new ForecastPeriod(startTime, endTime, value, null, null, null));

            }

            return new WeatherResponse(locName, forecastList);
        }

        return new WeatherResponse(locationName, List.of());
    }
}

