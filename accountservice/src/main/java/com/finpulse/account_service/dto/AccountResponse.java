package com.finpulse.account_service.dto;
import com.finpulse.micros_adria.accountservice.domain.Account;
import com.finpulse.micros_adria.accountservice.domain.AccountStatus;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String customerReference,
        AccountStatus status,
        String currency,
        double balance
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getCustomerReference(),
                account.getStatus(),
                account.getCurrency(),
                account.getBalance()
        );
    }
}