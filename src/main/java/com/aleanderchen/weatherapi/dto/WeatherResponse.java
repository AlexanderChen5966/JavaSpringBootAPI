package com.aleanderchen.weatherapi.dto;

import java.util.List;

public class WeatherResponse {
    private String location;
    private List<ForecastPeriod> description;

    public WeatherResponse(String location, List<ForecastPeriod> description) {
        this.location = location;
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public List<ForecastPeriod> getDescription() {
        return description;
    }

    public static class ForecastPeriod {
        private String startTime;
        private String endTime;
        private String wx;     // 天氣現象
        private String pop;    // 降雨機率百分比
        private String minT;   // 最低溫度
        private String maxT;   // 最高溫度

    public ForecastPeriod(String startTime, String endTime, String wx, String pop, String minT, String maxT) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.wx = wx;
        this.pop = pop;
        this.minT = minT;
        this.maxT = maxT;
    }
        public ForecastPeriod() {
        //TODO Auto-generated constructor stub
        }

        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getWx() { return wx; }
        public String getPop() { return pop; }
        public String getMinT() { return minT; }
        public String getMaxT() { return maxT; }

        public void setWx(String wx) { this.wx = wx; }
        public void setPop(String pop) { this.pop = pop; }
        public void setMinT(String minT) { this.minT = minT; }
        public void setMaxT(String maxT) { this.maxT = maxT; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

    }
}
