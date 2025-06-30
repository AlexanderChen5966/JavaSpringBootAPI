package com.aleanderchen.weatherapi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class HttpClientUtil {
    public static String get(String urlStr) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 seconds timeout
            conn.setReadTimeout(5000);    // 5 seconds timeout

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "{\"error\": \"HTTP error code: " + responseCode + "\"}";
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "{\"error\": \"Invalid URL: " + urlStr + "\"}";
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Network or I/O error: " + e.getMessage() + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Unexpected error: " + e.getMessage() + "\"}";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }
}

//package com.aleanderchen.weatherapi.util;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class HttpClientUtil {
//    public static String get(String urlStr) {
//        StringBuilder result = new StringBuilder();
//        try {
//            URL url = new URL(urlStr);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            try (BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    result.append(line);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "{"error": "API 呼叫失敗"}";
//        }
//        return result.toString();
//    }
//}
