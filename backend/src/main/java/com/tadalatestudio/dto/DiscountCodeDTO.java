package com.tadalatestudio.dto;

import com.tadalatestudio.model.DiscountCode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCodeDTO {
    private Long id;

    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^[A-Z0-9_-]{3,20}$", message = "Code must be 3-20 uppercase letters, numbers, underscores or hyphens")
    private String code;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountCode.DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    private BigDecimal minOrderAmount;

    private BigDecimal maxDiscountAmount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer maxUses;

    private Integer currentUses;

    private boolean isActive;

    private List<Long> applicableEventIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

