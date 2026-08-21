package com.payment.processor.service;

import com.payment.processor.config.RabbitMQConfig;
import com.payment.processor.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAYMENTS)
    public void consumePaymentCreated(PaymentResponseDTO payment) {
        log.info("========== MENSAGEM RECEBIDA DO RABBITMQ ==========");
        log.info("Pagamento ID: {}", payment.id());
        log.info("Chave de Idempotência: {}", payment.idempotencyKey());
        log.info("Valor: R$ {}", payment.amount());
        log.info("Status Atual: {}", payment.status());
        log.info("====================================================");
    }
}