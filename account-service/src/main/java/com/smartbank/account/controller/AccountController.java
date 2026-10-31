package com.smartbank.account.controller;

import com.smartbank.account.dto.AccountRequest;
import com.smartbank.account.dto.AccountResponse;
import com.smartbank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    // These two endpoints are called BY the Transaction Service, not
    // directly by an end user — this is the actual inter-service
    // communication your interview guide's microservices section
    // talks about, made real.
    @PutMapping("/{id}/debit")
    public ResponseEntity<AccountResponse> debitAccount(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.debit(id, amount));
    }

    @PutMapping("/{id}/credit")
    public ResponseEntity<AccountResponse> creditAccount(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.credit(id, amount));
    }
}
