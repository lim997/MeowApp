package com.example.myapplication;

public class List {
    int _id;
    String text;
    String ampm;
    String hour;
    String minute;

    public List(int _id,String text, String ampm, String hour, String minute) {
        this._id = _id;
        this.text = text;
        this.ampm = ampm;
        this.hour = hour;
        this.minute = minute;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String gettext() {
        return text;
    }

    public void settext(String text) {
        this.text = text;
    }

    public String getampm() {
        return ampm;
    }

    public void setampm(String ampm) {
        this.ampm = ampm;
    }

    public String gethour() {
        return hour;
    }

    public void sethour(String hour) {
        this.hour = hour;
    }

    public String getminute() {
        return minute;
    }

    public void setminute(String minute) {
        this.minute = minute;
    }
}
