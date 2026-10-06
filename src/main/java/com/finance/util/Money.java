package com.finance.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {

    private Money() {
    }

    public static BigDecimal of(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    /** part / whole as a percentage with one decimal place, capped at 100 (used for bar widths). */
    public static BigDecimal percent(BigDecimal part, BigDecimal whole) {
        if (part == null || whole == null || whole.signum() <= 0) {
            return BigDecimal.ZERO.setScale(1);
        }
        BigDecimal pct = part.multiply(BigDecimal.valueOf(100)).divide(whole, 1, RoundingMode.HALF_UP);
        return pct.min(BigDecimal.valueOf(100).setScale(1));
    }
}
