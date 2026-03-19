package com.sahil.notification_service.service;


import com.sahil.notification_service.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void  sendOrderConfirmation(OrderEvent event){
        log.info("====== EMAIL SENT ======");
        log.info("To       : customer-{}@example.com", event.getCustomerId());
        log.info("Subject  : Your order {} has been {}", event.getOrderId(), event.getStatus());
        log.info("Body     : Item: {}, Amount: ₹{}", event.getItem(), event.getAmount());
        log.info("========================");
    }
}
