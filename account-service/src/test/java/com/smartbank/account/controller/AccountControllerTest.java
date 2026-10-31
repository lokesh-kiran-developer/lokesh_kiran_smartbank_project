package com.smartbank.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbank.account.dto.AccountRequest;
import com.smartbank.account.dto.AccountResponse;
import com.smartbank.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest loads ONLY the web layer — fast, and it mocks out
// everything below the controller. This is the test-slice distinction
// from your JPA/Spring prep: narrowest slice that tests what you need.
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // @MockBean = a Spring-context-aware mock, replacing the real
    // AccountService bean inside this test's application context.
    @MockBean
    private AccountService accountService;

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setAccountHolderName("Lokesh Kiran");
        request.setAccountNumber("ACC1001");
        request.setInitialDeposit(new BigDecimal("5000.00"));

        AccountResponse response = new AccountResponse();
        // Using reflection-free construction via fromEntity isn't possible
        // here since AccountResponse fields are private with no setters —
        // in a real project you'd add a small test-only builder or make
        // the fromEntity approach testable directly. For this test we
        // rely on the service mock returning a fully-formed DTO.
        when(accountService.createAccount(org.mockito.ArgumentMatchers.any(AccountRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenAccountHolderNameMissing() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setAccountNumber("ACC1002");
        request.setInitialDeposit(new BigDecimal("1000.00"));
        // accountHolderName intentionally left null — @NotBlank should
        // trigger a 400 via MethodArgumentNotValidException, handled by
        // our GlobalExceptionHandler.

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAccountById() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(new AccountResponse());

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk());
    }
}
