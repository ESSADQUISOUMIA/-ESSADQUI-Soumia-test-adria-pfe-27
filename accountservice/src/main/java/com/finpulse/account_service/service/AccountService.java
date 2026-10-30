package com.finpulse.account_service.service;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import ma.adria.accountservice.domain.Account;
import ma.adria.accountservice.domain.AccountStatus;
import ma.adria.accountservice.repository.AccountRepository;
import ma.adria.accountservice.service.dto.AccountResponse;
import ma.adria.accountservice.service.dto.CreateAccountRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;
import java.util.UUID;

@Service
@Validated
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AccountResponse create(
            @NotNull @Valid CreateAccountRequest request
    ) {
        validateCurrency(request.currency());

        Account account = new Account(
                request.customerReference().trim(),
                request.currency(),
                request.initialBalance()
        );

        return AccountResponse.from(repository.save(account));
    }

    public AccountResponse getById(@NotNull UUID id) {
        return AccountResponse.from(findAccount(id));
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(AccountResponse::from);
    }

    @Transactional
    public AccountResponse changeStatus(
            @NotNull UUID id,
            @NotNull AccountStatus status
    ) {
        Account account = findAccount(id);
        account.changeStatus(status);

        return AccountResponse.from(account);
    }

    private Account findAccount(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountException(
                        AccountException.Code.NOT_FOUND,
                        "Compte introuvable : " + id
                ));
    }

    private void validateCurrency(String currency) {
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException exception) {
            throw new AccountException(
                    AccountException.Code.INVALID_CURRENCY,
                    "Devise inconnue : " + currency
            );
        }
    }
}

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import ma.adria.accountservice.domain.Account;
import ma.adria.accountservice.domain.AccountStatus;
import ma.adria.accountservice.repository.AccountRepository;
import ma.adria.accountservice.service.dto.AccountResponse;
import ma.adria.accountservice.service.dto.CreateAccountRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;
import java.util.UUID;

@Service
@Validated
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AccountResponse create(
            @NotNull @Valid CreateAccountRequest request
    ) {
        validateCurrency(request.currency());

        Account account = new Account(
                request.customerReference().trim(),
                request.currency(),
                request.initialBalance()
        );

        return AccountResponse.from(repository.save(account));
    }

    public AccountResponse getById(@NotNull UUID id) {
        return AccountResponse.from(findAccount(id));
    }

    public Page<AccountResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(AccountResponse::from);
    }

    @Transactional
    public AccountResponse changeStatus(
            @NotNull UUID id,
            @NotNull AccountStatus status
    ) {
        Account account = findAccount(id);
        account.changeStatus(status);

        return AccountResponse.from(account);
    }

    private Account findAccount(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountException(
                        AccountException.Code.NOT_FOUND,
                        "Compte introuvable : " + id
                ));
    }

    private void validateCurrency(String currency) {
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException exception) {
            throw new AccountException(
                    AccountException.Code.INVALID_CURRENCY,
                    "Devise inconnue : " + currency
            );
        }
    }
}