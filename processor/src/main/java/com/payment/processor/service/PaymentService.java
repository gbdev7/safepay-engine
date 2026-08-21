package com.payment.processor.service;

import com.payment.processor.domain.Payment;
import com.payment.processor.domain.PaymentRepository;
import com.payment.processor.dto.PaymentRequestDTO;
import com.payment.processor.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        return paymentRepository.findByIdempotencyKey(request.idempotencyKey())
                .map(this::mapToDTO)
                .orElseGet(() -> {
                    Payment payment = Payment.builder()
                            .idempotencyKey(request.idempotencyKey())
                            .amount(request.amount())
                            .currency(request.currency())
                            .status(Payment.PaymentStatus.PENDING)
                            .build();

                    Payment savedPayment = paymentRepository.save(payment);
                    PaymentResponseDTO response = mapToDTO(savedPayment);

                    // Dispara evento para o RabbitMQ
                    paymentProducer.publishPaymentCreated(response);

                    return response;
                });
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        return mapToDTO(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getIdempotencyKey(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}