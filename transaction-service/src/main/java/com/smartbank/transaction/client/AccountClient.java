package com.smartbank.transaction.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

// This class is the entire point of the project, architecturally —
// it's real inter-service communication, exactly like the
// "Show how you'd add a circuit breaker to a service call" question
// in your Microservices interview prep, but actually running.
@Component
public class AccountClient {

    private final RestTemplate restTemplate;

    // Note: no hardcoded host:port here. "account-service" is resolved
    // via Eureka + the @LoadBalanced RestTemplate bean.
    private static final String ACCOUNT_SERVICE_URL = "http://account-service/api/accounts";

    @Autowired
    public AccountClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "debitFallback")
    public void debitAccount(Long accountId, BigDecimal amount) {
        String url = ACCOUNT_SERVICE_URL + "/" + accountId + "/debit?amount=" + amount;
        restTemplate.put(url, null);
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "creditFallback")
    public void creditAccount(Long accountId, BigDecimal amount) {
        String url = ACCOUNT_SERVICE_URL + "/" + accountId + "/credit?amount=" + amount;
        restTemplate.put(url, null);
    }

    // Fallback method signature MUST match the original method,
    // plus a Throwable parameter at the end — exactly as covered
    // in your interview guide's Resilience4j question.
    private void debitFallback(Long accountId, BigDecimal amount, Throwable t) {
        throw new RuntimeException(
                "Account Service is currently unavailable. Debit could not be processed for account "
                        + accountId + ". Reason: " + t.getMessage());
    }

    private void creditFallback(Long accountId, BigDecimal amount, Throwable t) {
        throw new RuntimeException(
                "Account Service is currently unavailable. Credit could not be processed for account "
                        + accountId + ". Reason: " + t.getMessage());
    }
}
