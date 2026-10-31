package com.smartbank.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation failed");
        return new ResponseEntity<>(errorBody(HttpStatus.BAD_REQUEST, message), HttpStatus.BAD_REQUEST);
    }

    // account-service said 404 / 409 etc. — pass that status through
    // rather than pretending the service is down.
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleDownstreamClientError(HttpClientErrorException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return new ResponseEntity<>(errorBody(status, ex.getResponseBodyAsString()), status);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        // Catches the circuit breaker fallback exception from AccountClient too —
        // if account-service is down, this returns a clean 503 instead of a
        // raw stack trace leaking to the caller.
        return new ResponseEntity<>(
                errorBody(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());
        return body;
    }
}
