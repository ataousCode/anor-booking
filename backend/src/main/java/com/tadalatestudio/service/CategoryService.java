package com.tadalatestudio.service;

import com.tadalatestudio.dto.CategoryDTO;
import com.tadalatestudio.mapper.CategoryMapper;
import com.tadalatestudio.model.Category;
import com.tadalatestudio.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        log.info("Created new category: {}", savedCategory.getName());
        return categoryMapper.toDTO(savedCategory);
    }
}
