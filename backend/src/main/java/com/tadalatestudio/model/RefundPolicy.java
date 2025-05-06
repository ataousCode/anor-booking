package com.tadalatestudio.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "refund_policies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RefundPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "refund_window_hours")
    private Integer refundWindowHours;

    @Column(name = "refund_percentage", nullable = false)
    private Integer refundPercentage;

    @Column(name = "allow_partial_refunds")
    private boolean allowPartialRefunds = false;

    @Column(name = "requires_approval")
    private boolean requiresApproval = false;

    // Changed from bidirectional to unidirectional relationship
    // The Event entity has the @OneToOne relationship with RefundPolicy
    // but RefundPolicy doesn't need to reference back to Event
    // Removed: @OneToOne(mappedBy = "refundPolicy")
    // Removed: private Event event;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

