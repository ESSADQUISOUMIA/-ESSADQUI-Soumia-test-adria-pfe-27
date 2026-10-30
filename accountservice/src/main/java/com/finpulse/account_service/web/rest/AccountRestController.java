package com.finpulse.account_service.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.finpulse.accountservice.domain.AccountStatus;
import com.finpulse.accountservice.service.AccountService;
import com.finpulse.accountservice.service.dto.AccountResponse;
import com.finpulse.accountservice.service.dto.CreateAccountRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService service;

    public AccountRestController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        AccountResponse account = service.create(request);

        return ResponseEntity
                .created(URI.create("/api/accounts/" + account.id()))
                .body(account);
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<AccountResponse> findAll(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.findAll(pageable);
    }

    @PatchMapping("/{id}/status")
    public AccountResponse changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeStatusRequest request
    ) {
        return service.changeStatus(id, request.status());
    }

    public record ChangeStatusRequest(
            @NotNull AccountStatus status
    ) {
    }
}