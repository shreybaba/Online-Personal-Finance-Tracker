package com.finance.model;

import java.math.BigDecimal;

/** One row of the report's "budget vs. spent" table. */
public class BudgetComparison {

    private final String category;
    private final BigDecimal budget;
    private final BigDecimal spent;

    public BudgetComparison(String category, BigDecimal budget, BigDecimal spent) {
        this.category = category;
        this.budget = budget;
        this.spent = spent;
    }

    public String getCategory() { return category; }

    public BigDecimal getBudget() { return budget; }

    public BigDecimal getSpent() { return spent; }
}
