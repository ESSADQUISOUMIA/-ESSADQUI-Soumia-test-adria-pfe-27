package com.finpulse.account_service.dto.repository;


import com.finpulse.micros_adria.accountservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}