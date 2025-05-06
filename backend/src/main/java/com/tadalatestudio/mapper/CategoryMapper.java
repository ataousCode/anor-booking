package com.tadalatestudio.mapper;

import com.tadalatestudio.dto.CategoryDTO;
import com.tadalatestudio.model.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .colorCode(category.getColorCode())
                .isActive(category.isActive())
                .displayOrder(category.getDisplayOrder())
                .eventCount(category.getEvents() != null ? category.getEvents().size() : 0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return null;
        }

        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        Category category = new Category();
        updateEntityFromDTO(categoryDTO, category);
        return category;
    }

    public void updateEntityFromDTO(CategoryDTO dto, Category category) {
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        if (dto.getIconUrl() != null) {
            category.setIconUrl(dto.getIconUrl());
        }
        if (dto.getColorCode() != null) {
            category.setColorCode(dto.getColorCode());
        }
        category.setActive(dto.isActive());
        if (dto.getDisplayOrder() != null) {
            category.setDisplayOrder(dto.getDisplayOrder());
        }
    }
}
