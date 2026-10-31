package com.smartbank.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferRequest {

    @NotNull
    private Long fromAccountId;

    @NotNull
    private Long toAccountId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Transfer amount must be positive")
    private BigDecimal amount;

    public Long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }

    public Long getToAccountId() { return toAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
