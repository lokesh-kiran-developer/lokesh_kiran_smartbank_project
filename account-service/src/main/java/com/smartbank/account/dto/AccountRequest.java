package com.smartbank.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// DTO = what the API actually accepts, kept separate from the
// JPA Entity. This means we can change the DB schema later
// without breaking the API contract, and we control exactly
// what a client is allowed to send in (they can't set an ID
// or a balance directly on creation, for example).
public class AccountRequest {

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.0", message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
}
