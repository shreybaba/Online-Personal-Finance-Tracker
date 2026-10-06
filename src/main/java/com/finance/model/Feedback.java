package com.finance.model;

import java.time.LocalDate;
import java.util.List;

public class Feedback {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_IN_REVIEW = "IN_REVIEW";
    public static final String STATUS_RESOLVED = "RESOLVED";
    public static final List<String> STATUSES = List.of(STATUS_PENDING, STATUS_IN_REVIEW, STATUS_RESOLVED);

    private String id;
    private String userId;
    private String message;
    private String status;
    private LocalDate date;

    public Feedback() {
    }

    public Feedback(String id, String userId, String message, String status, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
