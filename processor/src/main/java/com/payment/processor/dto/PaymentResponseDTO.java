package com.payment.processor.dto;

import com.payment.processor.domain.Payment.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        String idempotencyKey,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        LocalDateTime createdAt
) {}