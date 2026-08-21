package com.payment.processor.controller;

import com.payment.processor.dto.PaymentRequestDTO;
import com.payment.processor.dto.PaymentResponseDTO;
import com.payment.processor.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*") // Libera acesso para o Frontend (React)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(
            @RequestBody @Valid PaymentRequestDTO request,
            UriComponentsBuilder uriBuilder
    ) {
        PaymentResponseDTO response = paymentService.createPayment(request);
        URI uri = uriBuilder.path("/api/payments/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> findAll() {
        return ResponseEntity.ok(paymentService.findAll());
    }
}