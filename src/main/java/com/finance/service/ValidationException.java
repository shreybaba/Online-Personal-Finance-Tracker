package com.finance.service;

/** Thrown when user input is invalid; the message is safe to show on the page. */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
