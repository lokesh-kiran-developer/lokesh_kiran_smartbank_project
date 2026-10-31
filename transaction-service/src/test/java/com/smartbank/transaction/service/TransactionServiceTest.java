package com.smartbank.transaction.service;

import com.smartbank.transaction.client.AccountClient;
import com.smartbank.transaction.dto.TransferRequest;
import com.smartbank.transaction.dto.TransferResponse;
import com.smartbank.transaction.model.Transaction;
import com.smartbank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// Plain Mockito (not @MockBean/@SpringBootTest) — no Spring context
// loaded at all, so this test runs fast. This is the right tool when
// you're testing pure business logic in isolation, exactly matching
// the "@Mock vs @MockBean" distinction from your prep.
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private TransactionService transactionService;

    private TransferRequest request;

    @BeforeEach
    void setUp() {
        request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("500.00"));
    }

    @Test
    void shouldProcessNewTransferSuccessfully() {
        when(transactionRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.empty());
        Transaction savedTxn = new Transaction(1L, 2L, new BigDecimal("500.00"), "key-123");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTxn);

        TransferResponse response = transactionService.processTransfer(request, "key-123");

        // Verify BOTH inter-service calls actually happened
        verify(accountClient, times(1)).debitAccount(1L, new BigDecimal("500.00"));
        verify(accountClient, times(1)).creditAccount(2L, new BigDecimal("500.00"));
        assertEquals("COMPLETED", response.getStatus());
    }

    @Test
    void shouldNotReprocessTransferWithSameIdempotencyKey() {
        Transaction existingTxn = new Transaction(1L, 2L, new BigDecimal("500.00"), "key-123");
        existingTxn.setStatus("COMPLETED");
        when(transactionRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.of(existingTxn));

        TransferResponse response = transactionService.processTransfer(request, "key-123");

        // The whole point of the idempotency test: debit/credit should
        // NEVER be called a second time for the same key.
        verify(accountClient, never()).debitAccount(anyLong(), any(BigDecimal.class));
        verify(accountClient, never()).creditAccount(anyLong(), any(BigDecimal.class));
        assertEquals("COMPLETED", response.getStatus());
    }
}
