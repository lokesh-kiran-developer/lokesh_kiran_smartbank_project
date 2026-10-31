package com.smartbank.account.exception;

import java.time.LocalDateTime;

// One consistent error shape used across EVERY endpoint —
// this is the "consistent error response design" pattern
// from your REST API interview prep.
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
