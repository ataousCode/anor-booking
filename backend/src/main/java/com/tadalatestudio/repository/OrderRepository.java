package com.tadalatestudio.repository;

import com.tadalatestudio.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.event.id = :eventId")
    Page<Order> findByEventId(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.event.id = :eventId AND (:status IS NULL OR o.status = :status)")
    Page<Order> findByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") Order.OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.event.id IN :eventIds")
    List<Order> findByEventIdIn(@Param("eventIds") List<Long> eventIds);

    @Query("SELECT o FROM Order o WHERE o.event.id IN :eventIds ORDER BY o.createdAt DESC")
    List<Order> findTop10ByEventIdInOrderByCreatedAtDesc(@Param("eventIds") List<Long> eventIds, Pageable pageable);

    default List<Order> findTop10ByEventIdInOrderByCreatedAtDesc(List<Long> eventIds) {
        return findTop10ByEventIdInOrderByCreatedAtDesc(eventIds, Pageable.ofSize(10));
    }

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate")
    Long countOrdersCreatedAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.event.organizer.id = :organizerId AND o.createdAt >= :startDate")
    Long countOrdersByOrganizerCreatedAfter(@Param("organizerId") Long organizerId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'COMPLETED' AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'COMPLETED' AND o.createdAt BETWEEN :startDate AND :endDate")
    long countCompletedOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
