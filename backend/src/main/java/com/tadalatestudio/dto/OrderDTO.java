package com.tadalatestudio.dto;

import com.tadalatestudio.model.Order;
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
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private Long eventId;
    private String eventTitle;
    private BigDecimal totalAmount;
    private Order.OrderStatus status;
    private Order.PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentDate;
    private boolean isRefunded;
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    private String refundTransactionId;
    private String refundReason;
    private List<TicketDTO> tickets;
    private String discountCode;
    private BigDecimal discountAmount;
    private LocalDateTime createdAt;
}
