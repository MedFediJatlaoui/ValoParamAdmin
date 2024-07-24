package com.talan.adminmodule.controller;

import com.talan.adminmodule.dto.CategoryDto;
import com.talan.adminmodule.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/categories")
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id) {
        CategoryDto categoryDto = categoryService.findById(id);
        return ResponseEntity.ok(categoryDto);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categoryDtoList = categoryService.findAll();
        return ResponseEntity.ok(categoryDtoList);
    }

    @GetMapping("/top-used")
    public ResponseEntity<List<CategoryDto>> getTopUsedCategories() {
        List<CategoryDto> categories = categoryService.getTopUsedCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto) {
        // Ensure the ID from the path matches the ID in the request body if necessary
        categoryDto.setId(id);
        CategoryDto updatedCategoryDto = categoryService.update(categoryDto);
        return ResponseEntity.ok(updatedCategoryDto);
    }
@GetMapping("/ref/{categoryId}")
    public long refCategory(@PathVariable Integer categoryId){
        return categoryService.refCategory(categoryId);
}
    @GetMapping("/delete/{categoryId}")
    public void deleteCategory(@PathVariable Integer categoryId){
         categoryService.delete(categoryId);
    }
}
