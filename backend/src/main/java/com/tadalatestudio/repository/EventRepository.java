package com.tadalatestudio.repository;

import com.tadalatestudio.model.Event;
import com.tadalatestudio.model.Category;
import com.tadalatestudio.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByOrganizerId(Long organizerId, Pageable pageable);

    List<Event> findByOrganizerId(Long organizerId);

    Page<Event> findByStatusAndStartDateAfter(Event.EventStatus status, LocalDateTime startDate, Pageable pageable);

    Page<Event> findByFeaturedTrue(Pageable pageable);

    Page<Event> findByStartDateAfter(LocalDateTime startDate, Pageable pageable);

    Page<Event> findByCategories(Category category, Pageable pageable);

    Page<Event> findByOrganizer(User organizer, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR EXISTS (SELECT c FROM e.categories c WHERE c.name = :category)) AND " +
            "(:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:startDate IS NULL OR e.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.endDate <= :endDate) AND " +
            "(:minPrice IS NULL OR EXISTS (SELECT t FROM TicketType t WHERE t.event = e AND t.price >= :minPrice)) AND " +
            "(:maxPrice IS NULL OR EXISTS (SELECT t FROM TicketType t WHERE t.event = e AND t.price <= :maxPrice)) AND " +
            "e.status = :status")
    Page<Event> searchEvents(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("location") String location,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") Event.EventStatus status,
            Pageable pageable);
}
