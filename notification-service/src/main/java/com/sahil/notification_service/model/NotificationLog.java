package com.sahil.notification_service.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name="notification_log")
@Data
public class NotificationLog {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
  @Column(nullable = false,unique = true)
  private  String orderId;

    private String customerId;
   private  LocalDateTime processedAt;


}
