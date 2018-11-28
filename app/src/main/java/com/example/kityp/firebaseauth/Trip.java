package com.example.kityp.firebaseauth;

import java.sql.Time;
import java.util.Date;
import java.util.function.DoubleToLongFunction;

public class Trip {
    private String key;
    private long start_time;
    private long end_time;
    private long duration;
    private double start_lat;
    private double start_long;
    private double end_lat;
    private double end_long;
    private String categories;
    private double miles;
    private double cost;

    public Trip() {

    }

    public Trip(double cost) {
        this.key = "";
        this.start_time = 0L;
        this.end_time = 0L;
        this.duration = 0L;
        this.start_lat = 0.0;
        this.start_long = 0.0;
        this.end_lat = 0.0;
        this.end_long = 0.0;
        this.categories = "";
        this.miles = 0.0;
        this.cost = cost;
    }

    //getters
    public String getKey() {
        return key;
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


    //setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStart_lat(double start_lat) {
        this.start_lat = start_lat;
    }

    public void setStart_long(double start_long) {
        this.start_long = start_long;
    }

    public void setEnd_lat(double end_lat) {
        this.end_lat = end_lat;
    }

    public void setEnd_long(double end_long) {
        this.end_long = end_long;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setMiles(double miles) {
        this.miles = miles;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
