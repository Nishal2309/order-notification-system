package com.sahil.notification_service.consumer;

import com.sahil.notification_service.model.NotificationLog;
import com.sahil.notification_service.model.OrderEvent;
import com.sahil.notification_service.repository.NotificationLogRepository;
import com.sahil.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final EmailService emailService;
    private final NotificationLogRepository logRepository;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void consumeOrderEvent(
            ConsumerRecord<String, OrderEvent> record,
            Acknowledgment acknowledgment) {

        OrderEvent event = record.value();

        log.info("Received partition={} offset={} orderId={}",
                record.partition(), record.offset(), event.getOrderId());

        // IDEMPOTENCY CHECK — has this orderId been processed before?
        if (logRepository.existsByOrderId(event.getOrderId())) {
            log.warn("Duplicate detected — orderId={} already processed. Skipping.",
                    event.getOrderId());
            acknowledgment.acknowledge(); // still commit — we've handled it
            return;
        }

        try {
            // 1. Send email
            emailService.sendOrderConfirmation(event);

            // 2. Record in DB so future duplicates are caught
            NotificationLog log2 = new NotificationLog();
            log2.setOrderId(event.getOrderId());
            log2.setCustomerId(event.getCustomerId());
            log2.setProcessedAt(LocalDateTime.now());
            logRepository.save(log2);

            // 3. Only now commit the Kafka offset
            acknowledgment.acknowledge();

            log.info("Processed and committed orderId={}", event.getOrderId());

        } catch (Exception e) {
            // Do NOT ack — Kafka will redeliver
            log.error("Processing failed for orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}