package com.example.kityp.firebaseauth;

import java.sql.Time;
import java.util.Date;

public class Profile {
    String user_uid;
    String display_name;
    String emailAddress;
    double working_hours;
    String categories;
    int pause_time;
    double mileage_rate;

    public Profile() {

    }

    public Profile(String user_uid, String display_name, String emailAddress, double working_hours, String categories, int pause_time, double mileage_rate) {
        this.user_uid = user_uid;
        this.display_name = display_name;
        this.emailAddress = emailAddress;
        this.working_hours = working_hours;
        this.categories = categories;
        this.pause_time = pause_time;
        this.mileage_rate = mileage_rate;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Double getWorking_hours() {
        return working_hours;
    }

    public String getCategories() {
        return categories;
    }

    public Integer getPause_time() {
        return pause_time;
    }

    public Double getMileage_rate() {
        return mileage_rate;
    }
}
