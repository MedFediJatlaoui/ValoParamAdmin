package com.talan.adminmodule.service;

import com.talan.adminmodule.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    void delete(Integer id);

    CategoryDto findById(Integer id);

    List<CategoryDto> findAll();

     List<CategoryDto> getTopUsedCategories() ;
}