package com.tadalatestudio.service;

import com.tadalatestudio.dto.EventDTO;
import com.tadalatestudio.exception.ResourceNotFoundException;
import com.tadalatestudio.mapper.EventMapper;
import com.tadalatestudio.model.Event;
import com.tadalatestudio.model.Category;
import com.tadalatestudio.model.User;
import com.tadalatestudio.repository.CategoryRepository;
import com.tadalatestudio.repository.EventRepository;
import com.tadalatestudio.repository.UserRepository;
import com.tadalatestudio.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository eventCategoryRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public EventDTO create(EventDTO eventDTO) {
        Event event = eventMapper.toEntity(eventDTO);

        // Set the current user as organizer if not specified
        if (event.getOrganizer() == null) {
            User currentUser = securityUtils.getCurrentUser();
            event.setOrganizer(currentUser);
        }

        // Process categories
        if (eventDTO.getCategoryIds() != null && !eventDTO.getCategoryIds().isEmpty()) {
            Set<Category> categories = eventDTO.getCategoryIds().stream()
                    .map(id -> eventCategoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)))
                    .collect(Collectors.toSet());
            event.setCategories(categories);
        }
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDTO(savedEvent);
    }
}
