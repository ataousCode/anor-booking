package com.tadalatestudio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private Boolean isRead;
    private String notificationType;
    private Long relatedEntityId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}