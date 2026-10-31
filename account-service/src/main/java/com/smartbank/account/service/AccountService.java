package com.smartbank.account.service;

import com.smartbank.account.dto.AccountRequest;
import com.smartbank.account.dto.AccountResponse;
import com.smartbank.account.exception.AccountNotFoundException;
import com.smartbank.account.exception.InsufficientBalanceException;
import com.smartbank.account.model.Account;
import com.smartbank.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    // Constructor injection — the recommended DI style from your prep:
    // explicit, immutable, easy to unit test without the Spring container.
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account(
                request.getAccountHolderName(),
                request.getAccountNumber(),
                request.getInitialDeposit()
        );
        Account saved = accountRepository.save(account);
        return AccountResponse.fromEntity(saved);
    }

    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        return AccountResponse.fromEntity(account);
    }

    // This method is called BY the Transaction Service (via REST) whenever
    // money needs to move. @Transactional here means: if anything fails
    // partway through, the balance change rolls back completely.
    @Transactional
    public AccountResponse debit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in account " + accountId + " for debit of " + amount);
        }

        account.setBalance(account.getBalance().subtract(amount));
        // No explicit save() call needed here — this is Hibernate's
        // "dirty checking" from your JPA prep: since 'account' is a
        // managed entity inside this @Transactional method, Hibernate
        // detects the balance field changed and issues the UPDATE
        // automatically when the transaction commits.
        return AccountResponse.fromEntity(account);
    }

    @Transactional
    public AccountResponse credit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        account.setBalance(account.getBalance().add(amount));
        return AccountResponse.fromEntity(account);
    }
}
