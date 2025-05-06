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
public class WaitlistDTO {
    private Long id;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String userName;
    private Integer position;
    private boolean notified;
    private LocalDateTime notificationSentAt;
    private LocalDateTime joinedAt;
}
