package com.example.kityp.firebaseauth;

import java.sql.Time;
import java.util.Date;
import java.util.function.DoubleToLongFunction;

public class Trip {
    String user_uid;
    Long start_time;
    Long end_time;
    Long duration;
    Double start_lat;
    Double start_long;
    Double end_lat;
    Double end_long;
    String categories;
    Double miles;
    Double cost;

    public Trip() {

    }

    public Trip(String user_uid, Long start_time, Long end_time, Long duration, Double start_lat, Double start_long, Double end_lat, Double end_long, String categories, Double miles, Double cost) {
        this.user_uid = user_uid;
        this.start_time = start_time;
        this.end_time = end_time;
        this.duration = duration;
        this.start_lat = start_lat;
        this.start_long = start_long;
        this.end_lat = end_lat;
        this.end_long = end_long;
        this.categories = categories;
        this.miles = miles;
        this.cost = cost;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public Long getStart_time() {
        return start_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public Long getDuration() {
        return duration;
    }

    public Double getStart_lat() {
        return start_lat;
    }

    public Double getStart_long() {
        return start_long;
    }

    public Double getEnd_lat() {
        return end_lat;
    }

    public Double getEnd_long() {
        return end_long;
    }

    public String getCategories() {
        return categories;
    }

    public Double getMiles() {
        return miles;
    }

    public Double getCost() {
        return cost;
    }
}
