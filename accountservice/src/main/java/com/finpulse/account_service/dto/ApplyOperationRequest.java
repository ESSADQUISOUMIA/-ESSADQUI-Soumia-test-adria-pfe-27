package com.finpulse.account_service.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplyOperationRequest(

        @NotNull
        UUID operationId,

        @NotNull
        UUID sourceAccountId,

        UUID destinationAccountId,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount,

        @NotBlank
        @Pattern(regexp = "[A-Z]{3}")
        String currency

) {
}

