package com.payment.processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // Fila Principal e Exchange
    public static final String QUEUE_PAYMENTS = "payments.v1.payment-created";
    public static final String EXCHANGE_PAYMENTS = "payments.v1.events";
    public static final String ROUTING_KEY_PAYMENT_CREATED = "payment.created";

    // DLQ (Dead Letter Queue)
    public static final String QUEUE_PAYMENTS_DLQ = "payments.v1.payment-created.dlq";
    public static final String EXCHANGE_PAYMENTS_DLX = "payments.v1.events.dlx";
    public static final String ROUTING_KEY_PAYMENT_CREATED_DLQ = "payment.created.dlq";

    // 1. Configuração da Fila Principal conectada à DLX
    @Bean
    public Queue paymentQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_PAYMENTS_DLX);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_PAYMENT_CREATED_DLQ);
        return QueueBuilder.durable(QUEUE_PAYMENTS).withArguments(args).build();
    }

    @Bean
    public TopicExchange paymentExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_PAYMENTS).build();
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentQueue).to(paymentExchange).with(ROUTING_KEY_PAYMENT_CREATED);
    }

    // 2. Configuração da DLQ e DLX
    @Bean
    public Queue paymentDlq() {
        return QueueBuilder.durable(QUEUE_PAYMENTS_DLQ).build();
    }

    @Bean
    public TopicExchange paymentDlx() {
        return ExchangeBuilder.topicExchange(EXCHANGE_PAYMENTS_DLX).build();
    }

    @Bean
    public Binding paymentDlqBinding(Queue paymentDlq, TopicExchange paymentDlx) {
        return BindingBuilder.bind(paymentDlq).to(paymentDlx).with(ROUTING_KEY_PAYMENT_CREATED_DLQ);
    }

    // Converter para JSON com suporte a LocalDateTime
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }
}