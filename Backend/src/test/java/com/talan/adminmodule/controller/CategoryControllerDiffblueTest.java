package com.talan.adminmodule.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talan.adminmodule.dto.CategoryDto;
import com.talan.adminmodule.service.CategoryService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {CategoryController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class CategoryControllerDiffblueTest {
  @Autowired
  private CategoryController categoryController;

  @MockBean
  private CategoryService categoryService;
  /**
   * Method under test: {@link CategoryController#deleteCategory(Integer)}
   */
  @Test
  void testDeleteCategory() throws Exception {
    // Arrange
    doNothing().when(categoryService).delete(Mockito.<Integer>any());
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories/delete/{categoryId}", 1);

    // Act and Assert
    MockMvcBuilders.standaloneSetup(categoryController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * Method under test: {@link CategoryController#getTopUsedCategories()}
   */
  @Test
  void testGetTopUsedCategories() throws Exception {
    // Arrange
    when(categoryService.getTopUsedCategories()).thenReturn(new ArrayList<>());
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories/top-used");

    // Act and Assert
    MockMvcBuilders.standaloneSetup(categoryController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.content().string("[]"));
  }

  /**
   * Method under test: {@link CategoryController#refCategory(Integer)}
   */
  @Test
  void testRefCategory() throws Exception {
    // Arrange
    when(categoryService.refCategory(Mockito.<Integer>any())).thenReturn(1L);
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories/ref/{categoryId}", 1);

    // Act and Assert
    MockMvcBuilders.standaloneSetup(categoryController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.content().string("1"));
  }

  /**
   * Method under test: {@link CategoryController#deleteCategory(Integer)}
   */
  @Test
  void testDeleteCategory2() throws Exception {
    // Arrange
    doNothing().when(categoryService).delete(Mockito.<Integer>any());
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories/delete/{categoryId}", 1);
    requestBuilder.contentType("https://example.org/example");

    // Act and Assert
    MockMvcBuilders.standaloneSetup(categoryController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * Method under test: {@link CategoryController#getAllCategories()}
   */
  @Test
  void testGetAllCategories() throws Exception {
    // Arrange
    when(categoryService.findAll()).thenReturn(new ArrayList<>());
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories");

    // Act and Assert
    MockMvcBuilders.standaloneSetup(categoryController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.content().string("[]"));
  }

  /**
   * Method under test: {@link CategoryController#getCategoryById(Integer)}
   */
  @Test
  void testGetCategoryById() throws Exception {
    // Arrange
    CategoryDto buildResult = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
    when(categoryService.findById(Mockito.<Integer>any())).thenReturn(buildResult);
    MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/categories/{id}", 1);

    // Act and Assert
    MockMvcBuilders.standaloneSetup(categoryController)
        .build()
        .perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.content().string("{\"id\":1,\"name\":\"Name\",\"ruleCount\":3}"));
  }

  /**
   * Method under test:
   * {@link CategoryController#updateCategory(Integer, CategoryDto)}
   */
  @Test
  void testUpdateCategory() throws Exception {
      CategoryDto buildResult = CategoryDto.builder()
              .id(1)
              .name("Name")
              .ruleCount(3)
              .build();
      when(categoryService.update(Mockito.any(CategoryDto.class))).thenReturn(buildResult);

      String content = new ObjectMapper().writeValueAsString(buildResult);
      MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/categories/{id}", 1)
              .contentType(MediaType.APPLICATION_JSON)
              .content(content);

      // Act and Assert
      MockMvcBuilders.standaloneSetup(categoryController)
              .build()
              .perform(requestBuilder)
              .andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(MockMvcResultMatchers.content().json("{\"id\":1,\"name\":\"Name\",\"ruleCount\":3}"));
  }
}
