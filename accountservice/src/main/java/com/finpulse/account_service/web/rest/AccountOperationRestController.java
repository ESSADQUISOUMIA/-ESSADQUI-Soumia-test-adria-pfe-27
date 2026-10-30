package com.finpulse.account_service.web.rest;

import jakarta.validation.Valid;

import com.finpulse.accountservice.service.AccountOperationService;
import com.finpulse.accountservice.service.dto.ApplyOperationRequest;
import com.finpulse.accountservice.service.dto.OperationResponse;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/account-operations")
public class AccountOperationRestController {

    private final AccountOperationService service;

    public AccountOperationRestController(
            AccountOperationService service
    ) {
        this.service = service;
    }

    @PostMapping
    public OperationResponse apply(
            @Valid @RequestBody ApplyOperationRequest request
    ) {
        return service.apply(request);
    }

    @GetMapping("/{operationId}")
    public OperationResponse getById(
            @PathVariable UUID operationId
    ) {
        return service.getById(operationId);
    }
}