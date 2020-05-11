package com.example.mytrip.data.model;

public class Moment {
    private String id;
    private String message;
    private String uri;
    private String date;

    public Moment() {
    }

    public Moment(String id, String message, String uri, String date) {
        this.id = id;
        this.message = message;
        this.uri = uri;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getUri() {
        return uri;
    }

    public String getDate() {
        return date;
    }
}
