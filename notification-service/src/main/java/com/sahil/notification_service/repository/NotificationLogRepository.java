package com.sahil.notification_service.repository;

import com.sahil.notification_service.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog,Long> {


    boolean existsByOrderId(String orderId);
}
