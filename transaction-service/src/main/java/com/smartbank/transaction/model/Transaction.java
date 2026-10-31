package com.smartbank.transaction.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fromAccountId;

    @Column(nullable = false)
    private Long toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED

    // The idempotency key from your REST API prep — a client-supplied
    // UUID that lets us detect and safely ignore a retried request
    // instead of double-processing a transfer.
    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Transaction() {}

    public Transaction(Long fromAccountId, Long toAccountId, BigDecimal amount, String idempotencyKey) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
