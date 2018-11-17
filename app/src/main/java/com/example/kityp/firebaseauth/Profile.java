package com.example.kityp.firebaseauth;

import java.sql.Time;
import java.util.Date;

public class Profile {
    String user_uid;
    String first_name;
    String  last_name;
    Double working_hours;
    String categories;
    String pause_time;
    Double mileage_rate;

    public Profile() {

    }

    public Profile(String user_uid, String first_name, String last_name, Double working_hours, String categories, String pause_time, Double mileage_rate) {
        this.user_uid = user_uid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.working_hours = working_hours;
        this.categories = categories;
        this.pause_time = pause_time;
        this.mileage_rate = mileage_rate;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public Double getWorking_hours() {
        return working_hours;
    }

    public String getCategories() {
        return categories;
    }

    public String getPause_time() {
        return pause_time;
    }

    public Double getMileage_rate() {
        return mileage_rate;
    }
}
