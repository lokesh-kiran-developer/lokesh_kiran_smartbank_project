package com.smartbank.account.repository;

import com.smartbank.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Derived query method — Spring Data JPA generates the query
    // from this method name alone, no implementation needed.
    // This is exactly the "findBy..." pattern from your interview guide.
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
