package com.fraudshield.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record EvaluateTransactionRequest(

        @NotBlank
        String transactionId,

        @NotBlank
        String userId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency,

        @NotBlank
        String merchantId,

        String country,

        String deviceId,

        String ipAddress,

        @Size(min = 6, max = 6)
        String cardBin,

        @NotNull
        ZonedDateTime transactionTime
) {}
