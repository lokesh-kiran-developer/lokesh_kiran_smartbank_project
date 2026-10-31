package com.smartbank.transaction.dto;

import com.smartbank.transaction.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponse {
    private Long transferId;
    private String status;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public static TransferResponse fromEntity(Transaction txn) {
        TransferResponse response = new TransferResponse();
        response.transferId = txn.getId();
        response.status = txn.getStatus();
        response.amount = txn.getAmount();
        response.timestamp = txn.getCreatedAt();
        return response;
    }

    public Long getTransferId() { return transferId; }
    public String getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
