package com.talan.adminmodule.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
import org.springframework.data.domain.Pageable;
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

    /**
     * Method under test: {@link CategoryServiceImpl#save(CategoryDto)}
     */
    @Test
    void testSave() {
        // Arrange
        CategoryDto categoryDto = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();

        // Act
        CategoryDto actualSaveResult = categoryServiceImpl.save(categoryDto);

        // Assert
        assertEquals("Name", actualSaveResult.getName());
        assertNull(actualSaveResult.getRuleCount());
        assertEquals(1, actualSaveResult.getId().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#save(CategoryDto)}
     */
    @Test
    void testSave2() {
        // Arrange
        CategoryDto.CategoryDtoBuilder categoryDtoBuilder = mock(CategoryDto.CategoryDtoBuilder.class);
        when(categoryDtoBuilder.id(Mockito.<Integer>any())).thenReturn(CategoryDto.builder());
        CategoryDto categoryDto = categoryDtoBuilder.id(1).name("Name").ruleCount(3).build();

        // Act
        CategoryDto actualSaveResult = categoryServiceImpl.save(categoryDto);

        // Assert
        verify(categoryDtoBuilder).id(Mockito.<Integer>any());
        assertEquals("Name", actualSaveResult.getName());
        assertNull(actualSaveResult.getId());
        assertNull(actualSaveResult.getRuleCount());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#save(CategoryDto)}
     */
    @Test
    void testSave3() {
        // Arrange
        CategoryDto categoryDto = mock(CategoryDto.class);
        when(categoryDto.getId()).thenReturn(1);
        when(categoryDto.getName()).thenReturn("Name");

        // Act
        CategoryDto actualSaveResult = categoryServiceImpl.save(categoryDto);

        // Assert
        verify(categoryDto).getId();
        verify(categoryDto).getName();
        assertEquals("Name", actualSaveResult.getName());
        assertNull(actualSaveResult.getRuleCount());
        assertEquals(1, actualSaveResult.getId().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#delete(Integer)}
     */
    @Test
    void testDelete() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);

        Category category2 = new Category();
        category2.setActive(true);
        category2.setId(1);
        category2.setName("Name");
        category2.setRules(new ArrayList<>());
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category2);
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

        // Act
        categoryServiceImpl.delete(1);

        // Assert
        verify(categoryRepository).findById(Mockito.<Integer>any());
        verify(categoryRepository).save(Mockito.<Category>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#delete(Integer)}
     */
    @Test
    void testDelete2() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);
        when(categoryRepository.save(Mockito.<Category>any())).thenThrow(new EntityNotFoundException("An error occurred"));
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.delete(1));
        verify(categoryRepository).findById(Mockito.<Integer>any());
        verify(categoryRepository).save(Mockito.<Category>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#findById(Integer)}
     */
    @Test
    void testFindById() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

        // Act
        CategoryDto actualFindByIdResult = categoryServiceImpl.findById(1);

        // Assert
        verify(categoryRepository).findById(Mockito.<Integer>any());
        assertEquals("Name", actualFindByIdResult.getName());
        assertNull(actualFindByIdResult.getRuleCount());
        assertEquals(1, actualFindByIdResult.getId().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#findById(Integer)}
     */
    @Test
    void testFindById2() {
        // Arrange
        Optional<Category> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findById(1));
        verify(categoryRepository).findById(Mockito.<Integer>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#findById(Integer)}
     */
    @Test
    void testFindById3() {
        // Arrange
        when(categoryRepository.findById(Mockito.<Integer>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findById(1));
        verify(categoryRepository).findById(Mockito.<Integer>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#findAll()}
     */
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

    /**
     * Method under test: {@link CategoryServiceImpl#findAll()}
     */
    @Test
    void testFindAll2() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
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

    /**
     * Method under test: {@link CategoryServiceImpl#findAll()}
     */
    @Test
    void testFindAll3() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());

        Category category2 = new Category();
        category2.setActive(false);
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
        assertEquals(1, actualFindAllResult.size());
        CategoryDto getResult = actualFindAllResult.get(0);
        assertEquals("Name", getResult.getName());
        assertNull(getResult.getRuleCount());
        assertEquals(1, getResult.getId().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#findAll()}
     */
    @Test
    void testFindAll4() {
        // Arrange
        when(categoryRepository.findAll()).thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.findAll());
        verify(categoryRepository).findAll();
    }

    /**
     * Method under test: {@link CategoryServiceImpl#getTopUsedCategories()}
     */
    @Test
    void testGetTopUsedCategories() {
        // Arrange
        when(categoryRepository.findTopUsedCategoriesWithRuleCount(Mockito.<Pageable>any())).thenReturn(new ArrayList<>());
        when(categoryRepository.findTotalRuleCount()).thenReturn(3L);

        // Act
        List<CategoryDto> actualTopUsedCategories = categoryServiceImpl.getTopUsedCategories();

        // Assert
        verify(categoryRepository).findTopUsedCategoriesWithRuleCount(Mockito.<Pageable>any());
        verify(categoryRepository).findTotalRuleCount();
        assertEquals(1, actualTopUsedCategories.size());
        CategoryDto getResult = actualTopUsedCategories.get(0);
        assertEquals("Other", getResult.getName());
        assertNull(getResult.getId());
        assertEquals(3, getResult.getRuleCount().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#getTopUsedCategories()}
     */
    @Test
    void testGetTopUsedCategories2() {
        // Arrange
        when(categoryRepository.findTopUsedCategoriesWithRuleCount(Mockito.<Pageable>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.getTopUsedCategories());
        verify(categoryRepository).findTopUsedCategoriesWithRuleCount(Mockito.<Pageable>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#update(CategoryDto)}
     */
    @Test
    void testUpdate() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);

        Category category2 = new Category();
        category2.setActive(true);
        category2.setId(1);
        category2.setName("Name");
        category2.setRules(new ArrayList<>());
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category2);
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        CategoryDto categoryDto = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();

        // Act
        CategoryDto actualUpdateResult = categoryServiceImpl.update(categoryDto);

        // Assert
        verify(categoryRepository).findById(Mockito.<Integer>any());
        verify(categoryRepository).save(Mockito.<Category>any());
        assertEquals("Name", actualUpdateResult.getName());
        assertNull(actualUpdateResult.getRuleCount());
        assertEquals(1, actualUpdateResult.getId().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#update(CategoryDto)}
     */
    @Test
    void testUpdate2() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);
        when(categoryRepository.save(Mockito.<Category>any())).thenThrow(new EntityNotFoundException("An error occurred"));
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        CategoryDto categoryDto = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.update(categoryDto));
        verify(categoryRepository).findById(Mockito.<Integer>any());
        verify(categoryRepository).save(Mockito.<Category>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#update(CategoryDto)}
     */
    @Test
    void testUpdate3() {
        // Arrange
        Optional<Category> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);
        CategoryDto categoryDto = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.update(categoryDto));
        verify(categoryRepository).findById(Mockito.<Integer>any());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#update(CategoryDto)}
     */
    @Test
    void testUpdate4() {
        // Arrange
        Category category = new Category();
        category.setActive(true);
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());
        Optional<Category> ofResult = Optional.of(category);

        Category category2 = new Category();
        category2.setActive(true);
        category2.setId(1);
        category2.setName("Name");
        category2.setRules(new ArrayList<>());
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category2);
        when(categoryRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        CategoryDto categoryDto = mock(CategoryDto.class);
        when(categoryDto.getId()).thenReturn(1);
        when(categoryDto.getName()).thenReturn("Name");

        // Act
        CategoryDto actualUpdateResult = categoryServiceImpl.update(categoryDto);

        // Assert
        verify(categoryDto).getId();
        verify(categoryDto).getName();
        verify(categoryRepository).findById(Mockito.<Integer>any());
        verify(categoryRepository).save(Mockito.<Category>any());
        assertEquals("Name", actualUpdateResult.getName());
        assertNull(actualUpdateResult.getRuleCount());
        assertEquals(1, actualUpdateResult.getId().intValue());
    }

    /**
     * Method under test: {@link CategoryServiceImpl#refCategory(Integer)}
     */
    @Test
    void testRefCategory() {
        // Arrange
        when(categoryRepository.countRulesByCategoryAndStatusNotEnabled(Mockito.<Integer>any())).thenReturn(3L);

        // Act
        long actualRefCategoryResult = categoryServiceImpl.refCategory(1);

        // Assert
        verify(categoryRepository).countRulesByCategoryAndStatusNotEnabled(Mockito.<Integer>any());
        assertEquals(3L, actualRefCategoryResult);
    }

    /**
     * Method under test: {@link CategoryServiceImpl#refCategory(Integer)}
     */
    @Test
    void testRefCategory2() {
        // Arrange
        when(categoryRepository.countRulesByCategoryAndStatusNotEnabled(Mockito.<Integer>any()))
                .thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.refCategory(1));
        verify(categoryRepository).countRulesByCategoryAndStatusNotEnabled(Mockito.<Integer>any());
    }
}
