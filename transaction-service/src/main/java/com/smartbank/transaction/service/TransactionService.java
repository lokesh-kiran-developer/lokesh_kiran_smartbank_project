package com.smartbank.transaction.service;

import com.smartbank.transaction.client.AccountClient;
import com.smartbank.transaction.dto.TransferRequest;
import com.smartbank.transaction.dto.TransferResponse;
import com.smartbank.transaction.model.Transaction;
import com.smartbank.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountClient accountClient) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
    }

    public TransferResponse processTransfer(TransferRequest request, String idempotencyKey) {
        // Idempotency check FIRST, before doing any real work — this is
        // the actual implementation of the "idempotency key" pattern
        // from your REST API prep, not just the concept.
        var existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            // Same key seen before — return the ORIGINAL result,
            // do NOT process the transfer again.
            return TransferResponse.fromEntity(existing.get());
        }

        Transaction transaction = new Transaction(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                idempotencyKey
        );
        transaction = transactionRepository.save(transaction);

        try {
            // Each call below is a separate local transaction inside
            // Account Service (its own database). There is no single
            // transaction spanning both calls, so a failure between them
            // needs explicit handling (see the note in the catch block).
            accountClient.debitAccount(request.getFromAccountId(), request.getAmount());
            accountClient.creditAccount(request.getToAccountId(), request.getAmount());

            transaction.setStatus("COMPLETED");
        } catch (Exception ex) {
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);
            // KNOWN LIMITATION: if the debit succeeded and the credit then
            // failed, the debit is not yet reversed here. Next step: track
            // whether the debit completed and, if so, credit the amount
            // back to the source account before rethrowing.
            throw ex;
        }

        transaction = transactionRepository.save(transaction);
        return TransferResponse.fromEntity(transaction);
    }
}
