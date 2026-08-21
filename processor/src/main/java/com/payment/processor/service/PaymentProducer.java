package com.payment.processor.service;

import com.payment.processor.config.RabbitMQConfig;
import com.payment.processor.dto.PaymentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentCreated(PaymentResponseDTO payment) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_PAYMENTS,
                RabbitMQConfig.ROUTING_KEY_PAYMENT_CREATED,
                payment
        );
    }
}