package com.smartbank.transaction.controller;

import com.smartbank.transaction.dto.TransferRequest;
import com.smartbank.transaction.dto.TransferResponse;
import com.smartbank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        TransferResponse response = transactionService.processTransfer(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
