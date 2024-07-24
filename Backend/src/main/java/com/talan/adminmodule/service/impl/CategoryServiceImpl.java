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
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
       Category newcat =  CategoryDto.toEntity(categoryDto);
       newcat.setActive(true);
        return CategoryDto.fromEntity(newcat);
    }

    @Override
    public void delete(Integer id) {

      Category cat = categoryRepository.findById(id).orElse(null);
        assert cat != null;
        cat.setActive(false);
        categoryRepository.save(cat);
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
                .filter(category -> Boolean.TRUE.equals(category.getActive()))
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

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        // Check if the category exists
        Category existingCategory = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No category with ID = " + categoryDto.getId() + " found in the database")
                );

        // Update the existing category's properties
        existingCategory.setName(categoryDto.getName());
        // Add any other properties that need to be updated

        // Save the updated category
        Category updatedCategory = categoryRepository.save(existingCategory);

        // Return the updated CategoryDto
        return CategoryDto.fromEntity(updatedCategory);
    }
    @Override
    public long refCategory(Integer categoryId){
        return categoryRepository.countRulesByCategoryAndStatusNotEnabled(categoryId);
    }
}
