package io.brickhack.mobile.Model;

import androidx.annotation.NonNull;

public class Event {

    private String time;
    private String desc;
    private String day;
    private boolean favorited;

    public Event() {

    }


    public Event(String day, String time, String desc, boolean favorited) {
        this.day = day;
        this.time = time;
        this.desc = desc;
        this.favorited = favorited;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @NonNull
    @Override
    public String toString() {
        return "The time: " + getTime() + " Description: " + getDesc();
    }
}
