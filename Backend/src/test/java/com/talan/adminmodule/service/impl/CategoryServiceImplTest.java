package com.talan.adminmodule.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.talan.adminmodule.dto.CategoryDto;
import com.talan.adminmodule.entity.Category;
import com.talan.adminmodule.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {CategoryServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class CategoryServiceImplTest {
    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(categoryRepository).deleteById(Mockito.<Integer>any());

        // Act
        categoryServiceImpl.delete(1);

        // Assert that nothing has changed
        verify(categoryRepository).deleteById(eq(1));
    }

    @Test
    void testDelete2() {
        // Arrange
        doThrow(new EntityNotFoundException("An error occurred")).when(categoryRepository)
                .deleteById(Mockito.<Integer>any());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.delete(1));
        verify(categoryRepository).deleteById(eq(1));
    }

    @Test
    void testFindById() {
        // Arrange
        Category category = new Category();
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

        // Act
        CategoryDto actualFindByIdResult = categoryServiceImpl.findById(1);

        // Assert
        verify(categoryRepository).findById(eq(1));
        assertEquals("Name", actualFindByIdResult.getName());
        assertNull(actualFindByIdResult.getRuleCount());
        assertEquals(1, actualFindByIdResult.getId().intValue());
    }
    @Test
    void testFindById2() {
        // Arrange
        Optional<Category> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findById(1));
        verify(categoryRepository).findById(eq(1));
    }

    @Test
    void testFindById3() {
        // Arrange
        when(categoryRepository.findById(Mockito.<Integer>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findById(1));
        verify(categoryRepository).findById(eq(1));
    }

    @Test
    void testFindAll() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<CategoryDto> actualFindAllResult = categoryServiceImpl.findAll();

        // Assert
        verify(categoryRepository).findAll();
        assertTrue(actualFindAllResult.isEmpty());
    }

    @Test
    void testFindAll2() {
        // Arrange
        Category category = new Category();
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());

        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(category);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<CategoryDto> actualFindAllResult = categoryServiceImpl.findAll();

        // Assert
        verify(categoryRepository).findAll();
        assertEquals(1, actualFindAllResult.size());
        CategoryDto getResult = actualFindAllResult.get(0);
        assertEquals("Name", getResult.getName());
        assertNull(getResult.getRuleCount());
        assertEquals(1, getResult.getId().intValue());
    }


    @Test
    void testFindAll3() {
        // Arrange
        Category category = new Category();
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());

        Category category2 = new Category();
        category2.setId(2);
        category2.setName("com.talan.adminmodule.entity.Category");
        category2.setRules(new ArrayList<>());

        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(category2);
        categoryList.add(category);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<CategoryDto> actualFindAllResult = categoryServiceImpl.findAll();

        // Assert
        verify(categoryRepository).findAll();
        assertEquals(2, actualFindAllResult.size());
        CategoryDto getResult = actualFindAllResult.get(1);
        assertEquals("Name", getResult.getName());
        CategoryDto getResult2 = actualFindAllResult.get(0);
        assertEquals("com.talan.adminmodule.entity.Category", getResult2.getName());
        assertNull(getResult2.getRuleCount());
        assertNull(getResult.getRuleCount());
        assertEquals(1, getResult.getId().intValue());
        assertEquals(2, getResult2.getId().intValue());
    }


    @Test
    void testFindAll4() {
        // Arrange
        when(categoryRepository.findAll()).thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findAll());
        verify(categoryRepository).findAll();
    }
}
