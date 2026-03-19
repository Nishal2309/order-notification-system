package com.sahil.notification_service.consumer;


import com.sahil.notification_service.model.OrderEvent;
import com.sahil.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "order-events",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public  void consumeOrderEvent(ConsumerRecord<String, OrderEvent> record,
                                   Acknowledgment acknowledgment){
        log.info("Received → partition={} offset={} key={}",
                record.partition(),
                record.offset(),
                record.key());

        try{
            OrderEvent event = record.value();

            emailService.sendOrderConfirmation(event);
             acknowledgment.acknowledge();


            log.info("Committed offset={} for partition={}",
                    record.offset(), record.partition());


        }catch (Exception e){

            log.error("Failed to process orderId={}, will retry: {}",
                    record.value().getOrderId(), e.getMessage());

        }

    }




}
