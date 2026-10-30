package com.finpulse.account_service.dto;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank
        @Size(max = 100)
        String customerReference,

        @NotBlank
        @Pattern(regexp = "[A-Z]{3}")
        String currency,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 12, fraction = 2)
        BigDecimal initialBalance

) {
}