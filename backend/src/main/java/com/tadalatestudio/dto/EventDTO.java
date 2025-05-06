package com.tadalatestudio.dto;

import com.tadalatestudio.model.Event;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;

    private Long organizerId;
    private String organizerName;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    private String location;
    private String venueAddress;
    private Boolean isVirtual;
    private String virtualMeetingLink;
    private String category;
    private Event.EventStatus status;
    private Integer maxAttendees;
    private Boolean featured;

    private Set<TicketTypeDTO> ticketTypes;
    private List<String> imageUrls;
    private List<CategoryDTO> categories;
    private List<Long> categoryIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

