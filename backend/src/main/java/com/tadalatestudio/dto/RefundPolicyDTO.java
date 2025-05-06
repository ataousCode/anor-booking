package com.tadalatestudio.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPolicyDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Min(value = 0, message = "Refund window hours must be at least 0")
    private Integer refundWindowHours;

    @Min(value = 0, message = "Refund percentage must be at least 0")
    @Max(value = 100, message = "Refund percentage must be at most 100")
    private Integer refundPercentage;

    private boolean allowPartialRefunds;

    private boolean requiresApproval;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
