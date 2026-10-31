package com.smartbank.account.dto;

import com.smartbank.account.model.Account;
import java.math.BigDecimal;

public class AccountResponse {
    private Long id;
    private String accountHolderName;
    private String accountNumber;
    private BigDecimal balance;

    // A static factory method to convert Entity -> DTO in one place,
    // instead of scattering .setX(entity.getX()) calls everywhere.
    public static AccountResponse fromEntity(Account account) {
        AccountResponse response = new AccountResponse();
        response.id = account.getId();
        response.accountHolderName = account.getAccountHolderName();
        response.accountNumber = account.getAccountNumber();
        response.balance = account.getBalance();
        return response;
    }

    public Long getId() { return id; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
}
