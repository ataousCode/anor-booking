package com.tadalatestudio.mapper;

import com.tadalatestudio.dto.CategoryDTO;
import com.tadalatestudio.dto.EventDTO;
import com.tadalatestudio.dto.TicketTypeDTO;
import com.tadalatestudio.model.Event;
import com.tadalatestudio.model.Category;
import com.tadalatestudio.model.EventImage;
import com.tadalatestudio.model.TicketType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        Set<TicketTypeDTO> ticketTypeDTOs = mapTicketTypes(event.getTicketTypes());

        String primaryCategory = event.getCategories() != null && !event.getCategories()
                .isEmpty() ? event.getCategories().iterator().next().getName() : null;

        return EventDTO.builder()
                .id(event.getId())
                .organizerId(event.getOrganizer().getId())
                .organizerName(event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .venueAddress(event.getVenueAddress())
                .isVirtual(event.getIsVirtual())
                .virtualMeetingLink(event.getVirtualMeetingLink())
                .category(primaryCategory) //event.getCategory()
                .status(event.getStatus())
                .maxAttendees(event.getMaxAttendees())
                .featured(event.isFeatured())
                .ticketTypes(ticketTypeDTOs)
                .imageUrls(mapImageUrls(event.getImages()))
                .categories(mapCategories(event.getCategories()))
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public Event toEntity(EventDTO dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        updateEntityFromDTO(dto, event);
        return event;
    }

    public void updateEntityFromDTO(EventDTO dto, Event event) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());
        event.setLocation(dto.getLocation());
        event.setVenueAddress(dto.getVenueAddress());
        event.setIsVirtual(dto.getIsVirtual());
        event.setVirtualMeetingLink(dto.getVirtualMeetingLink());
        //event.setCategory(dto.getCategory());
        event.setStatus(dto.getStatus());
        event.setMaxAttendees(dto.getMaxAttendees());
        event.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
    }

    private Set<TicketTypeDTO> mapTicketTypes(Set<TicketType> ticketTypes) {
        if (ticketTypes == null) {
            return Collections.emptySet();
        }
        return ticketTypes.stream()
                .map(this::mapTicketType)
                .collect(Collectors.toSet());
    }

    private TicketTypeDTO mapTicketType(TicketType ticketType) {
        return TicketTypeDTO.builder()
                .id(ticketType.getId())
                .name(ticketType.getName())
                .description(ticketType.getDescription())
                .price(ticketType.getPrice())
//                .quantity(ticketType.getQuantity())
//                .availableQuantity(ticketType.getAvailableQuantity())
                .build();
    }

    private List<String> mapImageUrls(Set<EventImage> images) {
        if (images == null) {
            return Collections.emptyList();
        }
        return images.stream()
                .map(EventImage::getImageUrl)
                .collect(Collectors.toList());
    }

    private List<CategoryDTO> mapCategories(Set<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(this::mapCategory)
                .collect(Collectors.toList());
    }

    private CategoryDTO mapCategory(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
