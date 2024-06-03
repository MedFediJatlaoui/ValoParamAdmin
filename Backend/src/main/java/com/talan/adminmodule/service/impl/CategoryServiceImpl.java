package com.talan.adminmodule.service.impl;

import com.talan.adminmodule.dto.CategoryDto;
import com.talan.adminmodule.entity.Category;
import com.talan.adminmodule.repository.CategoryRepository;
import com.talan.adminmodule.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        return CategoryDto.fromEntity(categoryRepository.save(CategoryDto.toEntity(categoryDto)));
    }

    @Override
    public void delete(Integer id) {
        this.categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto findById(Integer id) {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "No category with ID = " + id + " found in the database")
        );
        return CategoryDto.fromEntity(category);
    }

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }

    @Override
    public List<CategoryDto> getTopUsedCategories() {
        // Retrieve top 4 categories
        List<Object[]> topCategoryObjects = categoryRepository.findTopUsedCategoriesWithRuleCount(PageRequest.of(0, 4));
        List<CategoryDto> categories = new ArrayList<>();
        long topCategoriesRuleCount = 0;

        for (Object[] categoryObject : topCategoryObjects) {
            Category category = (Category) categoryObject[0];
            Long ruleCount = (Long) categoryObject[1];
            topCategoriesRuleCount += ruleCount;
            CategoryDto categoryDto = CategoryDto.fromEntity(category);
            categoryDto.setRuleCount(ruleCount.intValue());
            categories.add(categoryDto);
        }

        // Retrieve total rule count for all categories
        long totalRuleCount = categoryRepository.findTotalRuleCount();

        // Calculate "Other" category rule count
        long otherRuleCount = totalRuleCount - topCategoriesRuleCount;

        // Add "Other" category using the builder pattern
        CategoryDto otherCategoryDto = CategoryDto.builder()
                .name("Other")
                .ruleCount((int) otherRuleCount)
                .build();
        categories.add(otherCategoryDto);

        return categories;
    }
}
