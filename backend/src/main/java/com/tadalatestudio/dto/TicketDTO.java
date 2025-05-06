package com.tadalatestudio.dto;

import com.tadalatestudio.model.Ticket;
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
public class TicketDTO {
    private Long id;
    private String ticketNumber;
    private Long userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private Long eventId;
    private String eventTitle;
    private Long ticketTypeId;
    private String ticketTypeName;
    private BigDecimal price;
    private String barcodeUrl;
    private Ticket.TicketStatus status;
    private boolean isCheckedIn;
    private LocalDateTime checkedInAt;
    private boolean isCancelled;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDateTime createdAt;
}
