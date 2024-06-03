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
}
