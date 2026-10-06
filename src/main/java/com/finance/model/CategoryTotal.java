package com.finance.model;

import java.math.BigDecimal;

/** One row of a "spending by category" breakdown. */
public class CategoryTotal {

    private final String category;
    private final BigDecimal amount;
    private BigDecimal percentage = BigDecimal.ZERO;

    public CategoryTotal(String category, BigDecimal amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() { return category; }

    public BigDecimal getAmount() { return amount; }

    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
}
