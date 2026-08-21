package com.payment.processor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequestDTO(
        @NotBlank(message = "A chave de idempotência é obrigatória")
        String idempotencyKey,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal amount,

        @NotBlank(message = "A moeda é obrigatória")
        String currency
) {}