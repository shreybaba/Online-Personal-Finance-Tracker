package com.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {

    private String id;
    private String category;
    private BigDecimal amount;
    private LocalDate date;
    private String userId;

    public Expense() {
    }

    public Expense(String id, String category, BigDecimal amount, LocalDate date, String userId) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
