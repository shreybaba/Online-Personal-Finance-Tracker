package com.finance.util;

import com.finance.service.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Parses and checks form input, throwing ValidationException with a user-facing message. */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999.99"); // DECIMAL(10,2)

    private Validator() {
    }

    public static String text(String value, String field, int maxLength) throws ValidationException {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException(field + " is required.");
        }
        if (trimmed.length() > maxLength) {
            throw new ValidationException(field + " must be at most " + maxLength + " characters.");
        }
        return trimmed;
    }

    public static String email(String value) throws ValidationException {
        String email = text(value, "Email", 50).toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new ValidationException("Please enter a valid email address.");
        }
        return email;
    }

    public static String password(String value) throws ValidationException {
        if (value == null || value.length() < 6 || value.length() > 16) {
            throw new ValidationException("Password must be between 6 and 16 characters.");
        }
        return value;
    }

    public static BigDecimal amount(String value) throws ValidationException {
        BigDecimal amount;
        try {
            amount = new BigDecimal(value == null ? "" : value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Please enter a valid amount.");
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Amount must be greater than zero.");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new ValidationException("Amount is too large.");
        }
        if (amount.scale() > 2) {
            throw new ValidationException("Amount can have at most 2 decimal places.");
        }
        return amount.setScale(2);
    }

    public static LocalDate date(String value, String field) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field + " is required.");
        }
        return optionalDate(value, field);
    }

    /** Returns null for an empty value. */
    public static LocalDate optionalDate(String value, String field) throws ValidationException {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException(field + " must be a valid date (YYYY-MM-DD).");
        }
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
