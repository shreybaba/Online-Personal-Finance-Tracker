package com.finance.model;

import java.time.LocalDate;

public class Advice {

    private String id;
    private String advisorId;
    private String message;
    private LocalDate date;
    private String userId;

    public Advice() {
    }

    public Advice(String id, String advisorId, String message, LocalDate date, String userId) {
        this.id = id;
        this.advisorId = advisorId;
        this.message = message;
        this.date = date;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAdvisorId() { return advisorId; }
    public void setAdvisorId(String advisorId) { this.advisorId = advisorId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
