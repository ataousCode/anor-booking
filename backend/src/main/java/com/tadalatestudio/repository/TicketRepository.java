package com.tadalatestudio.repository;

import com.tadalatestudio.model.Event;
import com.tadalatestudio.model.Ticket;
import com.tadalatestudio.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByUser(User user, Pageable pageable);

    Page<Ticket> findByEvent(Event event, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.event = :event AND t.isCheckedIn = :checkedIn")
    List<Ticket> findByEventAndIsCheckedIn(@Param("event") Event event, @Param("checkedIn") boolean checkedIn);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    Optional<Ticket> findByBarcodeUrl(String barcodeUrl);

    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId AND t.user.id = :userId AND t.isCancelled = false")
    List<Ticket> findActiveTicketsByEventAndUser(@Param("eventId") Long eventId, @Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.event.id = :eventId AND t.isCancelled = false")
    long countActiveTicketsByEvent(@Param("eventId") Long eventId);

    @Query("SELECT t FROM Ticket t JOIN t.event e WHERE e.startDate BETWEEN :startDate AND :endDate AND t.user.id = :userId AND t.isCancelled = false")
    List<Ticket> findUpcomingTicketsByUser(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
