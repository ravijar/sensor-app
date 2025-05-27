package com.littlebits.sensorapp.model;

public class Workout {
    private String date;
    private String monthYear;
    private String time;

    public Workout(String date, String monthYear, String time) {
        this.date = date;
        this.monthYear = monthYear;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
