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
public class WishlistDTO {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventImageUrl;
    private LocalDateTime eventStartDate;
    private Long userId;
    private LocalDateTime addedAt;
}

