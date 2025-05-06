package com.tadalatestudio.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 digits in integer part and 2 digits in fraction part")
    private BigDecimal price;

    @NotNull(message = "Quantity available is required")
    @Min(value = 1, message = "Quantity available must be at least 1")
    private Integer quantityAvailable;

    private Integer quantitySold;

    @Min(value = 1, message = "Max per order must be at least 1")
    private Integer maxPerOrder;

    private LocalDateTime saleStartDate;

    private LocalDateTime saleEndDate;

    private Long eventId;

    private Integer quantity;
}

