package com.tadalatestudio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventImageDTO {
    private Long id;

    private Long eventId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private boolean isPrimary;

    private Integer displayOrder;

    private String altText;

    private LocalDateTime createdAt;
}

