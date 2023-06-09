package com.endava.developement.java.webapphomework.exceptions.exceptionHandling;

import java.time.LocalDate;

public class ErrorDetails {
    private final LocalDate timestamp;
    private final String message;
    private final String details;

    public ErrorDetails(LocalDate timestamp, String mesaage, String details) {
        this.timestamp = timestamp;
        this.message = mesaage;
        this.details = details;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
