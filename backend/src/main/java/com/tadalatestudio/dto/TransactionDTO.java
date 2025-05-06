package com.tadalatestudio.dto;

import com.tadalatestudio.model.Transaction;
import com.tadalatestudio.model.Transaction.TransactionType;
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
public class TransactionDTO {
    private Long id;
    private String transactionNumber;
    private Long orderId;
    private String orderNumber;
    private Long paymentProviderId;
    private String paymentProviderName;
    private BigDecimal amount;
    private String currency;
    private Transaction.TransactionStatus status;
    private String providerTransactionId;
    private TransactionType transactionType;
    private LocalDateTime createdAt;
}

