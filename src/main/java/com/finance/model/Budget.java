package com.finance.model;

import java.math.BigDecimal;

public class Budget {

    public static final String PERIOD_MONTHLY = "Monthly";
    public static final String PERIOD_WEEKLY = "Weekly";
    public static final String PERIOD_ANNUAL = "Annual";

    private String id;
    private String userId;
    private String category;
    private BigDecimal amount;
    private String period;

    // Not stored: calculated from expenses in the budget's current period
    private BigDecimal spent = BigDecimal.ZERO;
    private BigDecimal percentage = BigDecimal.ZERO;

    public Budget() {
    }

    public Budget(String id, String userId, String category, BigDecimal amount, String period) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.period = period;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getSpent() { return spent; }
    public void setSpent(BigDecimal spent) { this.spent = spent; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
}
