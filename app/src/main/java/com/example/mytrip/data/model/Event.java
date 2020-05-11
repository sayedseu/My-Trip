package com.example.mytrip.data.model;

public class Event {
    private String id;
    private String destination;
    private String budget;
    private String fromDate;
    private String toDate;

    public Event() {
    }

    public Event(String id, String destination, String budget, String fromDate, String toDate) {
        this.id = id;
        this.destination = destination;
        this.budget = budget;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public String getBudget() {
        return budget;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
}
