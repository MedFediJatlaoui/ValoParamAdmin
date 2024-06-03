package com.talan.adminmodule.service.impl;

import com.talan.adminmodule.config.exception.EntityNotFoundException;
import com.talan.adminmodule.config.exception.InvalidEntityException;
import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.entity.*;
import com.talan.adminmodule.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {RuleServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class RuleServiceImplTest {
  @MockBean
  private AttributeRepository attributeRepository;

  @MockBean
  private CategoryRepository categoryRepository;

  @MockBean
  private RuleAttributeRepository ruleAttributeRepository;

  @MockBean
  private RuleModificationRepository ruleModificationRepository;

  @MockBean
  private RuleRepository ruleRepository;

  @Autowired
  private RuleServiceImpl ruleServiceImpl;

  @MockBean
  private RuleUsageRepository ruleUsageRepository;

  @Test
  void testSave() {
    // Arrange, Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.save(null));
  }

  @Test
  void testSave2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule);

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Category category3 = new Category();
    category3.setId(1);
    category3.setName("Name");
    category3.setRules(new ArrayList<>());
    when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(category2);
    when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category3);
    RuleDto.RuleDtoBuilder builderResult = RuleDto.builder();
    RuleDto.RuleDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
    CategoryDto category4 = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
    RuleDto.RuleDtoBuilder categoryResult = attributeDtosResult.category(category4);
    RuleDto.RuleDtoBuilder idResult = categoryResult.createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
        .createdBy(1)
        .description("The characteristics of someone or something")
        .id(1);
    RuleDto ruleDto = idResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
        .lastModifiedBy(1)
        .name("Name")
        .status("Status")
        .build();

    // Act
    RuleDto actualSaveResult = ruleServiceImpl.save(ruleDto);

    // Assert
    verify(categoryRepository).findByName("Name");
    verify(ruleRepository).save(isA(Rule.class));
    LocalTime expectedToLocalTimeResult = actualSaveResult.getLastModified().toLocalTime();
    assertSame(expectedToLocalTimeResult, actualSaveResult.getCreateDate().toLocalTime());
  }


  @Test
  void testSave3() {
    // Arrange
    when(categoryRepository.findByName(Mockito.<String>any())).thenThrow(new IllegalArgumentException("Enabled"));
    RuleDto.RuleDtoBuilder builderResult = RuleDto.builder();
    RuleDto.RuleDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
    CategoryDto category = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
    RuleDto.RuleDtoBuilder categoryResult = attributeDtosResult.category(category);
    RuleDto.RuleDtoBuilder idResult = categoryResult.createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
        .createdBy(1)
        .description("The characteristics of someone or something")
        .id(1);
    RuleDto ruleDto = idResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
        .lastModifiedBy(1)
        .name("Name")
        .status("Status")
        .build();

    // Act and Assert
    assertThrows(IllegalArgumentException.class, () -> ruleServiceImpl.save(ruleDto));
    verify(categoryRepository).findByName("Name");
  }
  @Test
  void testSave6() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());
    Rule rule = mock(Rule.class);
    when(rule.getCategory()).thenReturn(category2);
    when(rule.getCreatedBy()).thenReturn(1);
    when(rule.getId()).thenReturn(1);
    when(rule.getLastModifiedBy()).thenReturn(1);
    when(rule.getDescription()).thenReturn("The characteristics of someone or something");
    when(rule.getName()).thenReturn("Name");
    when(rule.getStatus()).thenReturn("Status");
    when(rule.getCreateDate()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule.getLastModified()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule.getRuleAttributes()).thenReturn(new ArrayList<>());
    doNothing().when(rule).setCategory(Mockito.<Category>any());
    doNothing().when(rule).setCreateDate(Mockito.<LocalDateTime>any());
    doNothing().when(rule).setCreatedBy(Mockito.<Integer>any());
    doNothing().when(rule).setDescription(Mockito.<String>any());
    doNothing().when(rule).setId(Mockito.<Integer>any());
    doNothing().when(rule).setLastModified(Mockito.<LocalDateTime>any());
    doNothing().when(rule).setLastModifiedBy(Mockito.<Integer>any());
    doNothing().when(rule).setName(Mockito.<String>any());
    doNothing().when(rule).setRuleAttributes(Mockito.<List<RuleAttribute>>any());
    doNothing().when(rule).setRuleModifications(Mockito.<List<RuleModification>>any());
    doNothing().when(rule).setStatus(Mockito.<String>any());
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule);

    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    Attribute attribute2 = new Attribute();
    attribute2.setId(1);
    attribute2.setName("Name");
    attribute2.setRuleAttributes(new ArrayList<>());
    when(attributeRepository.findByNameIgnoreCase(Mockito.<String>any())).thenReturn(attribute);
    when(attributeRepository.save(Mockito.<Attribute>any())).thenReturn(attribute2);

    Category category3 = new Category();
    category3.setId(1);
    category3.setName("Name");
    category3.setRules(new ArrayList<>());

    Category category4 = new Category();
    category4.setId(1);
    category4.setName("Name");
    category4.setRules(new ArrayList<>());
    when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(category3);
    when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category4);

    ArrayList<AttributeDataDto> attributeDtos = new ArrayList<>();
    AttributeDataDto.AttributeDataDtoBuilder idResult = AttributeDataDto.builder().id(1);
    AttributeDto name = AttributeDto.builder().id(1).name("Name").build();
    AttributeDataDto buildResult = idResult.name(name).percentage(10.0d).value(10.0d).build();
    attributeDtos.add(buildResult);
    RuleDto.RuleDtoBuilder attributeDtosResult = RuleDto.builder().attributeDtos(attributeDtos);
    CategoryDto category5 = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
    RuleDto.RuleDtoBuilder categoryResult = attributeDtosResult.category(category5);
    RuleDto.RuleDtoBuilder idResult2 = categoryResult.createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
        .createdBy(1)
        .description("The characteristics of someone or something")
        .id(1);
    RuleDto ruleDto = idResult2.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
        .lastModifiedBy(1)
        .name("Name")
        .status("Status")
        .build();

    // Act
    RuleDto actualSaveResult = ruleServiceImpl.save(ruleDto);

    // Assert
    verify(rule).getCategory();
    verify(rule).getCreateDate();
    verify(rule).getCreatedBy();
    verify(rule).getDescription();
    verify(rule).getId();
    verify(rule).getLastModified();
    verify(rule).getLastModifiedBy();
    verify(rule).getName();
    verify(rule).getRuleAttributes();
    verify(rule).getStatus();
    verify(rule).setCategory(isA(Category.class));
    verify(rule).setCreateDate(isA(LocalDateTime.class));
    verify(rule).setCreatedBy(1);
    verify(rule).setDescription("The characteristics of someone or something");
    verify(rule).setId(1);
    verify(rule).setLastModified(isA(LocalDateTime.class));
    verify(rule).setLastModifiedBy(1);
    verify(rule).setName("Name");
    verify(rule, atLeast(1)).setRuleAttributes(Mockito.<List<RuleAttribute>>any());
    verify(rule).setRuleModifications(isA(List.class));
    verify(rule).setStatus("Status");
    verify(attributeRepository).findByNameIgnoreCase("Name");
    verify(categoryRepository).findByName("Name");
    verify(ruleRepository).save(isA(Rule.class));
    LocalTime expectedToLocalTimeResult = actualSaveResult.getLastModified().toLocalTime();
    assertSame(expectedToLocalTimeResult, actualSaveResult.getCreateDate().toLocalTime());
  }
  @Test
  void testSaveRuleModification() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());
    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    RuleModification ruleModification = new RuleModification();
    ruleModification.setId(1);
    ruleModification.setModificationDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    ruleModification.setModificationDescription("Modification Description");
    ruleModification.setModificationType("Modification Type");
    ruleModification.setModifiedBy("Jan 1, 2020 9:00am GMT+0100");
    ruleModification.setProfileImagePath("Profile Image Path");
    ruleModification.setRule(rule);
    ruleModification.setRuleName("Rule Name");
    when(ruleModificationRepository.save(Mockito.<RuleModification>any())).thenReturn(ruleModification);
    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Rule existingRule = new Rule();
    existingRule.setCategory(category2);
    existingRule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    existingRule.setCreatedBy(1);
    existingRule.setDescription("The characteristics of someone or something");
    existingRule.setId(1);
    existingRule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    existingRule.setLastModifiedBy(1);
    existingRule.setName("Name");
    existingRule.setRuleAttributes(new ArrayList<>());
    existingRule.setRuleModifications(new ArrayList<>());
    existingRule.setStatus("Status");

    // Act
    ruleServiceImpl.saveRuleModification(existingRule, "Mod Description", "Jan 1, 2020 9:00am GMT+0100",
        "Modification Type", "https://example.org/example");

    // Assert
    verify(ruleModificationRepository).save(isA(RuleModification.class));
  }

  @Test
  void testAddRuleAttribute() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule existingRule = new Rule();
    existingRule.setCategory(category);
    existingRule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    existingRule.setCreatedBy(1);
    existingRule.setDescription("The characteristics of someone or something");
    existingRule.setId(1);
    existingRule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    existingRule.setLastModifiedBy(1);
    existingRule.setName("Name");
    existingRule.setRuleAttributes(new ArrayList<>());
    existingRule.setRuleModifications(new ArrayList<>());
    existingRule.setStatus("Status");
    AttributeDataDto.AttributeDataDtoBuilder idResult = AttributeDataDto.builder().id(1);
    AttributeDto name = AttributeDto.builder().id(1).name("Name").build();
    AttributeDataDto attributeDto = idResult.name(name).percentage(10.0d).value(10.0d).build();

    // Act and Assert
    Rule rule = ruleServiceImpl.addRuleAttribute(attribute, existingRule, attributeDto).getRule();
    LocalTime expectedToLocalTimeResult = rule.getLastModified().toLocalTime();
    assertSame(expectedToLocalTimeResult, rule.getCreateDate().toLocalTime());
  }

  @Test
  void testDelete() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    doNothing().when(ruleRepository).deleteById(Mockito.<Integer>any());
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act
    ruleServiceImpl.delete(1);

    // Assert that nothing has changed
    verify(ruleRepository).deleteById(1);
    verify(ruleRepository).findById(1);
  }

  @Test
  void testDelete2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    doThrow(new InvalidEntityException("An error occurred")).when(ruleRepository).deleteById(Mockito.<Integer>any());
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.delete(1));
    verify(ruleRepository).deleteById(1);
    verify(ruleRepository).findById(1);
  }

  @Test
  void testDelete3() {
    // Arrange
    Optional<Rule> emptyResult = Optional.empty();
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(EntityNotFoundException.class, () -> ruleServiceImpl.delete(1));
    verify(ruleRepository).findById(1);
  }


  @Test
  void testFindById() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.findByIdWithAttributes(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act
    RuleDto actualFindByIdResult = ruleServiceImpl.findById(1);

    // Assert
    verify(ruleRepository).findByIdWithAttributes(1);
    LocalTime expectedToLocalTimeResult = actualFindByIdResult.getLastModified().toLocalTime();
    assertSame(expectedToLocalTimeResult, actualFindByIdResult.getCreateDate().toLocalTime());
  }

  @Test
  void testFindAll() {
    // Arrange
    when(ruleRepository.findAll(Mockito.<Pageable>any())).thenReturn(new PageImpl<>(new ArrayList<>()));

    // Act
    Page<RuleDto> actualFindAllResult = ruleServiceImpl.findAll(1, 3);

    // Assert
    verify(ruleRepository).findAll(isA(Pageable.class));
    assertTrue(actualFindAllResult.toList().isEmpty());
  }


  @Test
  void testFindAll2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("lastModified");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("lastModified");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("lastModified");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.findAll(Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualFindAllResult = ruleServiceImpl.findAll(1, 3);

    // Assert
    verify(ruleRepository).findAll(isA(Pageable.class));
    assertEquals(1, actualFindAllResult.toList().size());
  }


  @Test
  void testFindAll3() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("lastModified");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("lastModified");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("lastModified");

    Category category2 = new Category();
    category2.setId(2);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(0);
    rule2.setDescription("lastModified");
    rule2.setId(2);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(0);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule2);
    content.add(rule);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.findAll(Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualFindAllResult = ruleServiceImpl.findAll(1, 3);

    // Assert
    verify(ruleRepository).findAll(isA(Pageable.class));
    assertEquals(2, actualFindAllResult.toList().size());
  }


  @Test
  void testFindAll4() {
    // Arrange
    when(ruleRepository.findAll(Mockito.<Pageable>any())).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.findAll(1, 3));
    verify(ruleRepository).findAll(isA(Pageable.class));
  }

  @Test
  void testFindAll5() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("lastModified");
    category.setRules(new ArrayList<>());

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    Category category3 = new Category();
    category3.setId(1);
    category3.setName("Name");
    category3.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category3);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    RuleAttribute ruleAttribute = new RuleAttribute();
    ruleAttribute.setAttribute(attribute);
    ruleAttribute.setId(1L);
    ruleAttribute.setPercentage(10.0d);
    ruleAttribute.setRule(rule);
    ruleAttribute.setValue(10.0d);

    ArrayList<RuleAttribute> ruleAttributeList = new ArrayList<>();
    ruleAttributeList.add(ruleAttribute);
    Rule rule2 = mock(Rule.class);
    when(rule2.getCategory()).thenReturn(category2);
    when(rule2.getCreatedBy()).thenReturn(1);
    when(rule2.getId()).thenReturn(1);
    when(rule2.getLastModifiedBy()).thenReturn(1);
    when(rule2.getDescription()).thenReturn("The characteristics of someone or something");
    when(rule2.getName()).thenReturn("Name");
    when(rule2.getStatus()).thenReturn("Status");
    when(rule2.getCreateDate()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule2.getLastModified()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule2.getRuleAttributes()).thenReturn(ruleAttributeList);
    doNothing().when(rule2).setCategory(Mockito.<Category>any());
    doNothing().when(rule2).setCreateDate(Mockito.<LocalDateTime>any());
    doNothing().when(rule2).setCreatedBy(Mockito.<Integer>any());
    doNothing().when(rule2).setDescription(Mockito.<String>any());
    doNothing().when(rule2).setId(Mockito.<Integer>any());
    doNothing().when(rule2).setLastModified(Mockito.<LocalDateTime>any());
    doNothing().when(rule2).setLastModifiedBy(Mockito.<Integer>any());
    doNothing().when(rule2).setName(Mockito.<String>any());
    doNothing().when(rule2).setRuleAttributes(Mockito.<List<RuleAttribute>>any());
    doNothing().when(rule2).setRuleModifications(Mockito.<List<RuleModification>>any());
    doNothing().when(rule2).setStatus(Mockito.<String>any());
    rule2.setCategory(category);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(1);
    rule2.setDescription("The characteristics of someone or something");
    rule2.setId(1);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(1);
    rule2.setName("lastModified");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("lastModified");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule2);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.findAll(Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualFindAllResult = ruleServiceImpl.findAll(1, 3);

    // Assert
    verify(rule2).getCategory();
    verify(rule2).getCreateDate();
    verify(rule2).getCreatedBy();
    verify(rule2).getDescription();
    verify(rule2).getId();
    verify(rule2).getLastModified();
    verify(rule2).getLastModifiedBy();
    verify(rule2).getName();
    verify(rule2).getRuleAttributes();
    verify(rule2).getStatus();
    verify(rule2).setCategory(isA(Category.class));
    verify(rule2).setCreateDate(isA(LocalDateTime.class));
    verify(rule2).setCreatedBy(1);
    verify(rule2).setDescription("The characteristics of someone or something");
    verify(rule2).setId(1);
    verify(rule2).setLastModified(isA(LocalDateTime.class));
    verify(rule2).setLastModifiedBy(1);
    verify(rule2).setName("lastModified");
    verify(rule2).setRuleAttributes(isA(List.class));
    verify(rule2).setRuleModifications(isA(List.class));
    verify(rule2).setStatus("lastModified");
    verify(ruleRepository).findAll(isA(Pageable.class));
    assertEquals(1, actualFindAllResult.toList().size());
  }


  @Test
  void testFindAll6() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("lastModified");
    category.setRules(new ArrayList<>());

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    Category category3 = new Category();
    category3.setId(1);
    category3.setName("Name");
    category3.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category3);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    RuleAttribute ruleAttribute = new RuleAttribute();
    ruleAttribute.setAttribute(attribute);
    ruleAttribute.setId(1L);
    ruleAttribute.setPercentage(10.0d);
    ruleAttribute.setRule(rule);
    ruleAttribute.setValue(10.0d);

    Attribute attribute2 = new Attribute();
    attribute2.setId(2);
    attribute2.setName("Name");
    attribute2.setRuleAttributes(new ArrayList<>());

    Category category4 = new Category();
    category4.setId(2);
    category4.setName("Name");
    category4.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category4);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(0);
    rule2.setDescription("lastModified");
    rule2.setId(2);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(0);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");

    RuleAttribute ruleAttribute2 = new RuleAttribute();
    ruleAttribute2.setAttribute(attribute2);
    ruleAttribute2.setId(2L);
    ruleAttribute2.setPercentage(0.5d);
    ruleAttribute2.setRule(rule2);
    ruleAttribute2.setValue(0.5d);

    ArrayList<RuleAttribute> ruleAttributeList = new ArrayList<>();
    ruleAttributeList.add(ruleAttribute2);
    ruleAttributeList.add(ruleAttribute);
    Rule rule3 = mock(Rule.class);
    when(rule3.getCategory()).thenReturn(category2);
    when(rule3.getCreatedBy()).thenReturn(1);
    when(rule3.getId()).thenReturn(1);
    when(rule3.getLastModifiedBy()).thenReturn(1);
    when(rule3.getDescription()).thenReturn("The characteristics of someone or something");
    when(rule3.getName()).thenReturn("Name");
    when(rule3.getStatus()).thenReturn("Status");
    when(rule3.getCreateDate()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule3.getLastModified()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule3.getRuleAttributes()).thenReturn(ruleAttributeList);
    doNothing().when(rule3).setCategory(Mockito.<Category>any());
    doNothing().when(rule3).setCreateDate(Mockito.<LocalDateTime>any());
    doNothing().when(rule3).setCreatedBy(Mockito.<Integer>any());
    doNothing().when(rule3).setDescription(Mockito.<String>any());
    doNothing().when(rule3).setId(Mockito.<Integer>any());
    doNothing().when(rule3).setLastModified(Mockito.<LocalDateTime>any());
    doNothing().when(rule3).setLastModifiedBy(Mockito.<Integer>any());
    doNothing().when(rule3).setName(Mockito.<String>any());
    doNothing().when(rule3).setRuleAttributes(Mockito.<List<RuleAttribute>>any());
    doNothing().when(rule3).setRuleModifications(Mockito.<List<RuleModification>>any());
    doNothing().when(rule3).setStatus(Mockito.<String>any());
    rule3.setCategory(category);
    rule3.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule3.setCreatedBy(1);
    rule3.setDescription("The characteristics of someone or something");
    rule3.setId(1);
    rule3.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule3.setLastModifiedBy(1);
    rule3.setName("lastModified");
    rule3.setRuleAttributes(new ArrayList<>());
    rule3.setRuleModifications(new ArrayList<>());
    rule3.setStatus("lastModified");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule3);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.findAll(Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualFindAllResult = ruleServiceImpl.findAll(1, 3);

    // Assert
    verify(rule3).getCategory();
    verify(rule3).getCreateDate();
    verify(rule3).getCreatedBy();
    verify(rule3).getDescription();
    verify(rule3).getId();
    verify(rule3).getLastModified();
    verify(rule3).getLastModifiedBy();
    verify(rule3).getName();
    verify(rule3).getRuleAttributes();
    verify(rule3).getStatus();
    verify(rule3).setCategory(isA(Category.class));
    verify(rule3).setCreateDate(isA(LocalDateTime.class));
    verify(rule3).setCreatedBy(1);
    verify(rule3).setDescription("The characteristics of someone or something");
    verify(rule3).setId(1);
    verify(rule3).setLastModified(isA(LocalDateTime.class));
    verify(rule3).setLastModifiedBy(1);
    verify(rule3).setName("lastModified");
    verify(rule3).setRuleAttributes(isA(List.class));
    verify(rule3).setRuleModifications(isA(List.class));
    verify(rule3).setStatus("lastModified");
    verify(ruleRepository).findAll(isA(Pageable.class));
    assertEquals(1, actualFindAllResult.toList().size());
  }


  @Test
  void testGetModificationsByRuleId() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
    when(ruleModificationRepository.findByRuleOrderByModificationDateDesc(Mockito.<Rule>any()))
        .thenReturn(new ArrayList<>());

    // Act
    List<RuleModificationDto> actualModificationsByRuleId = ruleServiceImpl.getModificationsByRuleId(1);

    // Assert
    verify(ruleModificationRepository).findByRuleOrderByModificationDateDesc(isA(Rule.class));
    verify(ruleRepository).findById(1);
    assertTrue(actualModificationsByRuleId.isEmpty());
  }


  @Test
  void testGetModificationsByRuleId2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
    when(ruleModificationRepository.findByRuleOrderByModificationDateDesc(Mockito.<Rule>any()))
        .thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.getModificationsByRuleId(1));
    verify(ruleModificationRepository).findByRuleOrderByModificationDateDesc(isA(Rule.class));
    verify(ruleRepository).findById(1);
  }


  @Test
  void testGetModificationsByRuleId3() {
    // Arrange
    Optional<Rule> emptyResult = Optional.empty();
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(EntityNotFoundException.class, () -> ruleServiceImpl.getModificationsByRuleId(1));
    verify(ruleRepository).findById(1);
  }


  @Test
  void testSearchRules() {
    // Arrange
    when(ruleRepository.search(Mockito.<String>any(), Mockito.<Pageable>any()))
        .thenReturn(new PageImpl<>(new ArrayList<>()));

    // Act
    Page<RuleDto> actualSearchRulesResult = ruleServiceImpl.searchRules(1, 3, "Query");

    // Assert
    verify(ruleRepository).search(eq("Query"), isA(Pageable.class));
    assertTrue(actualSearchRulesResult.toList().isEmpty());
  }


  @Test
  void testSearchRules2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.search(Mockito.<String>any(), Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualSearchRulesResult = ruleServiceImpl.searchRules(1, 3, "Query");

    // Assert
    verify(ruleRepository).search(eq("Query"), isA(Pageable.class));
    assertEquals(1, actualSearchRulesResult.toList().size());
  }


  @Test
  void testSearchRules3() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    Category category2 = new Category();
    category2.setId(2);
    category2.setName("com.talan.adminmodule.entity.Category");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(0);
    rule2.setDescription("Description");
    rule2.setId(2);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(0);
    rule2.setName("Enabled");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Enabled");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule2);
    content.add(rule);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.search(Mockito.<String>any(), Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualSearchRulesResult = ruleServiceImpl.searchRules(1, 3, "Query");

    // Assert
    verify(ruleRepository).search(eq("Query"), isA(Pageable.class));
    assertEquals(2, actualSearchRulesResult.toList().size());
  }

  @Test
  void testSearchRules4() {
    // Arrange
    when(ruleRepository.search(Mockito.<String>any(), Mockito.<Pageable>any()))
        .thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.searchRules(1, 3, "Query"));
    verify(ruleRepository).search(eq("Query"), isA(Pageable.class));
  }


  @Test
  void testSearchRules5() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    Category category3 = new Category();
    category3.setId(1);
    category3.setName("Name");
    category3.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category3);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    RuleAttribute ruleAttribute = new RuleAttribute();
    ruleAttribute.setAttribute(attribute);
    ruleAttribute.setId(1L);
    ruleAttribute.setPercentage(10.0d);
    ruleAttribute.setRule(rule);
    ruleAttribute.setValue(10.0d);

    ArrayList<RuleAttribute> ruleAttributeList = new ArrayList<>();
    ruleAttributeList.add(ruleAttribute);
    Rule rule2 = mock(Rule.class);
    when(rule2.getCategory()).thenReturn(category2);
    when(rule2.getCreatedBy()).thenReturn(1);
    when(rule2.getId()).thenReturn(1);
    when(rule2.getLastModifiedBy()).thenReturn(1);
    when(rule2.getDescription()).thenReturn("The characteristics of someone or something");
    when(rule2.getName()).thenReturn("Name");
    when(rule2.getStatus()).thenReturn("Status");
    when(rule2.getCreateDate()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule2.getLastModified()).thenReturn(LocalDate.of(1970, 1, 1).atStartOfDay());
    when(rule2.getRuleAttributes()).thenReturn(ruleAttributeList);
    doNothing().when(rule2).setCategory(Mockito.<Category>any());
    doNothing().when(rule2).setCreateDate(Mockito.<LocalDateTime>any());
    doNothing().when(rule2).setCreatedBy(Mockito.<Integer>any());
    doNothing().when(rule2).setDescription(Mockito.<String>any());
    doNothing().when(rule2).setId(Mockito.<Integer>any());
    doNothing().when(rule2).setLastModified(Mockito.<LocalDateTime>any());
    doNothing().when(rule2).setLastModifiedBy(Mockito.<Integer>any());
    doNothing().when(rule2).setName(Mockito.<String>any());
    doNothing().when(rule2).setRuleAttributes(Mockito.<List<RuleAttribute>>any());
    doNothing().when(rule2).setRuleModifications(Mockito.<List<RuleModification>>any());
    doNothing().when(rule2).setStatus(Mockito.<String>any());
    rule2.setCategory(category);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(1);
    rule2.setDescription("The characteristics of someone or something");
    rule2.setId(1);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(1);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");

    ArrayList<Rule> content = new ArrayList<>();
    content.add(rule2);
    PageImpl<Rule> pageImpl = new PageImpl<>(content);
    when(ruleRepository.search(Mockito.<String>any(), Mockito.<Pageable>any())).thenReturn(pageImpl);

    // Act
    Page<RuleDto> actualSearchRulesResult = ruleServiceImpl.searchRules(1, 3, "Query");

    // Assert
    verify(rule2).getCategory();
    verify(rule2).getCreateDate();
    verify(rule2).getCreatedBy();
    verify(rule2).getDescription();
    verify(rule2).getId();
    verify(rule2).getLastModified();
    verify(rule2).getLastModifiedBy();
    verify(rule2).getName();
    verify(rule2).getRuleAttributes();
    verify(rule2).getStatus();
    verify(rule2).setCategory(isA(Category.class));
    verify(rule2).setCreateDate(isA(LocalDateTime.class));
    verify(rule2).setCreatedBy(1);
    verify(rule2).setDescription("The characteristics of someone or something");
    verify(rule2).setId(1);
    verify(rule2).setLastModified(isA(LocalDateTime.class));
    verify(rule2).setLastModifiedBy(1);
    verify(rule2).setName("Name");
    verify(rule2).setRuleAttributes(isA(List.class));
    verify(rule2).setRuleModifications(isA(List.class));
    verify(rule2).setStatus("Status");
    verify(ruleRepository).search(eq("Query"), isA(Pageable.class));
    assertEquals(1, actualSearchRulesResult.toList().size());
  }


  @Test
  void testQueueUpdate() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(1);
    rule2.setDescription("The characteristics of someone or something");
    rule2.setId(1);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(1);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");
    when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule2);
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act
    ruleServiceImpl.queueUpdate(1, null, "Mod Description", "Jan 1, 2020 9:00am GMT+0100",
        "https://example.org/example");

    // Assert
    verify(ruleRepository).findById(1);
    verify(ruleRepository).save(isA(Rule.class));
  }


  @Test
  void testQueueUpdate2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.save(Mockito.<Rule>any())).thenThrow(new InvalidEntityException("An error occurred"));
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.queueUpdate(1, null, "Mod Description",
        "Jan 1, 2020 9:00am GMT+0100", "https://example.org/example"));
    verify(ruleRepository).findById(1);
    verify(ruleRepository).save(isA(Rule.class));
  }


  @Test
  void testQueueUpdate3() {
    // Arrange
    Optional<Rule> emptyResult = Optional.empty();
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(EntityNotFoundException.class, () -> ruleServiceImpl.queueUpdate(1, null, "Mod Description",
        "Jan 1, 2020 9:00am GMT+0100", "https://example.org/example"));
    verify(ruleRepository).findById(1);
  }


  @Test
  void testQueueDelete() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(1);
    rule2.setDescription("The characteristics of someone or something");
    rule2.setId(1);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(1);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");
    when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule2);
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act
    ruleServiceImpl.queueDelete(1, "Jan 1, 2020 9:00am GMT+0100", "https://example.org/example");

    // Assert
    verify(ruleRepository).findById(1);
    verify(ruleRepository).save(isA(Rule.class));
  }


  @Test
  void testQueueDelete2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.save(Mockito.<Rule>any())).thenThrow(new InvalidEntityException("An error occurred"));
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act and Assert
    assertThrows(InvalidEntityException.class,
        () -> ruleServiceImpl.queueDelete(1, "Jan 1, 2020 9:00am GMT+0100", "https://example.org/example"));
    verify(ruleRepository).findById(1);
    verify(ruleRepository).save(isA(Rule.class));
  }

  @Test
  void testQueueDelete3() {
    // Arrange
    Optional<Rule> emptyResult = Optional.empty();
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(EntityNotFoundException.class,
        () -> ruleServiceImpl.queueDelete(1, "Jan 1, 2020 9:00am GMT+0100", "https://example.org/example"));
    verify(ruleRepository).findById(1);
  }


  @Test
  void testGetAllModifications() {
    // Arrange
    when(ruleModificationRepository.findAll()).thenReturn(new ArrayList<>());

    // Act
    List<RuleModificationDto> actualAllModifications = ruleServiceImpl.getAllModifications();

    // Assert
    verify(ruleModificationRepository).findAll();
    assertTrue(actualAllModifications.isEmpty());
  }


  @Test
  void testGetAllModifications2() {
    // Arrange
    when(ruleModificationRepository.findAll()).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.getAllModifications());
    verify(ruleModificationRepository).findAll();
  }

  @Test
  void testGetTop5UsedRulesForLast18Days() {
    // Arrange
    when(ruleUsageRepository.findAllByCreateDateBetween(Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any()))
        .thenReturn(new ArrayList<>());

    // Act
    List<RuleUsageDTO> actualTop5UsedRulesForLast18Days = ruleServiceImpl.getTop5UsedRulesForLast18Days();

    // Assert
    verify(ruleUsageRepository).findAllByCreateDateBetween(isA(LocalDateTime.class), isA(LocalDateTime.class));
    assertTrue(actualTop5UsedRulesForLast18Days.isEmpty());
  }


  @Test
  void testGetTop5UsedRulesForLast18Days2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(17);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(17);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    RuleUsage ruleUsage = new RuleUsage();
    ruleUsage.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    ruleUsage.setId(1);
    ruleUsage.setRule(rule);

    ArrayList<RuleUsage> ruleUsageList = new ArrayList<>();
    ruleUsageList.add(ruleUsage);
    when(ruleUsageRepository.findAllByCreateDateBetween(Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any()))
        .thenReturn(ruleUsageList);

    // Act
    List<RuleUsageDTO> actualTop5UsedRulesForLast18Days = ruleServiceImpl.getTop5UsedRulesForLast18Days();

    // Assert
    verify(ruleUsageRepository).findAllByCreateDateBetween(isA(LocalDateTime.class), isA(LocalDateTime.class));
    assertEquals(1, actualTop5UsedRulesForLast18Days.size());
    RuleUsageDTO getResult = actualTop5UsedRulesForLast18Days.get(0);
    List<DayUsageDTO> dayUsages = getResult.getDayUsages();
    assertEquals(1, dayUsages.size());
    DayUsageDTO getResult2 = dayUsages.get(0);
    assertEquals("1970-01-01", getResult2.getDate().toString());
    assertEquals("Name", getResult.getRuleName());
    assertEquals(1, getResult2.getUsageCount());
  }

  @Test
  void testGetTop5UsedRulesForLast18Days3() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(17);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(17);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    RuleUsage ruleUsage = new RuleUsage();
    ruleUsage.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    ruleUsage.setId(1);
    ruleUsage.setRule(rule);

    Category category2 = new Category();
    category2.setId(2);
    category2.setName("com.talan.adminmodule.entity.Category");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(23);
    rule2.setDescription("Description");
    rule2.setId(2);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(23);
    rule2.setName("Enabled");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Enabled");

    RuleUsage ruleUsage2 = new RuleUsage();
    ruleUsage2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    ruleUsage2.setId(2);
    ruleUsage2.setRule(rule2);

    ArrayList<RuleUsage> ruleUsageList = new ArrayList<>();
    ruleUsageList.add(ruleUsage2);
    ruleUsageList.add(ruleUsage);
    when(ruleUsageRepository.findAllByCreateDateBetween(Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any()))
        .thenReturn(ruleUsageList);

    // Act
    List<RuleUsageDTO> actualTop5UsedRulesForLast18Days = ruleServiceImpl.getTop5UsedRulesForLast18Days();

    // Assert
    verify(ruleUsageRepository).findAllByCreateDateBetween(isA(LocalDateTime.class), isA(LocalDateTime.class));
    assertEquals(2, actualTop5UsedRulesForLast18Days.size());
    RuleUsageDTO getResult = actualTop5UsedRulesForLast18Days.get(0);
    List<DayUsageDTO> dayUsages = getResult.getDayUsages();
    assertEquals(1, dayUsages.size());
    DayUsageDTO getResult2 = dayUsages.get(0);
    assertEquals("1970-01-01", getResult2.getDate().toString());
    RuleUsageDTO getResult3 = actualTop5UsedRulesForLast18Days.get(1);
    List<DayUsageDTO> dayUsages2 = getResult3.getDayUsages();
    assertEquals(1, dayUsages2.size());
    DayUsageDTO getResult4 = dayUsages2.get(0);
    assertEquals("1970-01-01", getResult4.getDate().toString());
    assertEquals("Enabled", getResult.getRuleName());
    assertEquals("Name", getResult3.getRuleName());
    assertEquals(1, getResult2.getUsageCount());
    assertEquals(1, getResult4.getUsageCount());
  }


  @Test
  void testGetTop5UsedRulesForLast18Days4() {
    // Arrange
    when(ruleUsageRepository.findAllByCreateDateBetween(Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any()))
        .thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.getTop5UsedRulesForLast18Days());
    verify(ruleUsageRepository).findAllByCreateDateBetween(isA(LocalDateTime.class), isA(LocalDateTime.class));
  }

  @Test
  void testGetTop5UsedRulesForLast18Days5() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(17);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(17);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(1);
    rule2.setDescription("The characteristics of someone or something");
    rule2.setId(1);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(1);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");
    RuleUsage ruleUsage = mock(RuleUsage.class);
    when(ruleUsage.getCreateDate()).thenThrow(new InvalidEntityException("An error occurred"));
    when(ruleUsage.getRule()).thenReturn(rule2);
    doNothing().when(ruleUsage).setCreateDate(Mockito.<LocalDateTime>any());
    doNothing().when(ruleUsage).setId(Mockito.<Integer>any());
    doNothing().when(ruleUsage).setRule(Mockito.<Rule>any());
    ruleUsage.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    ruleUsage.setId(1);
    ruleUsage.setRule(rule);

    ArrayList<RuleUsage> ruleUsageList = new ArrayList<>();
    ruleUsageList.add(ruleUsage);
    when(ruleUsageRepository.findAllByCreateDateBetween(Mockito.<LocalDateTime>any(), Mockito.<LocalDateTime>any()))
        .thenReturn(ruleUsageList);

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.getTop5UsedRulesForLast18Days());
    verify(ruleUsage).getCreateDate();
    verify(ruleUsage).getRule();
    verify(ruleUsage).setCreateDate(isA(LocalDateTime.class));
    verify(ruleUsage).setId(1);
    verify(ruleUsage).setRule(isA(Rule.class));
    verify(ruleUsageRepository).findAllByCreateDateBetween(isA(LocalDateTime.class), isA(LocalDateTime.class));
  }


  @Test
  void testCreateRuleUsage() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    Category category2 = new Category();
    category2.setId(1);
    category2.setName("Name");
    category2.setRules(new ArrayList<>());

    Rule rule2 = new Rule();
    rule2.setCategory(category2);
    rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setCreatedBy(1);
    rule2.setDescription("The characteristics of someone or something");
    rule2.setId(1);
    rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule2.setLastModifiedBy(1);
    rule2.setName("Name");
    rule2.setRuleAttributes(new ArrayList<>());
    rule2.setRuleModifications(new ArrayList<>());
    rule2.setStatus("Status");

    RuleUsage ruleUsage = new RuleUsage();
    ruleUsage.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    ruleUsage.setId(1);
    ruleUsage.setRule(rule2);
    when(ruleUsageRepository.save(Mockito.<RuleUsage>any())).thenReturn(ruleUsage);

    // Act
    ruleServiceImpl.createRuleUsage(1);

    // Assert
    verify(ruleRepository).findById(1);
    verify(ruleUsageRepository).save(isA(RuleUsage.class));
  }

  @Test
  void testCreateRuleUsage2() {
    // Arrange
    Category category = new Category();
    category.setId(1);
    category.setName("Name");
    category.setRules(new ArrayList<>());

    Rule rule = new Rule();
    rule.setCategory(category);
    rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setCreatedBy(1);
    rule.setDescription("The characteristics of someone or something");
    rule.setId(1);
    rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
    rule.setLastModifiedBy(1);
    rule.setName("Name");
    rule.setRuleAttributes(new ArrayList<>());
    rule.setRuleModifications(new ArrayList<>());
    rule.setStatus("Status");
    Optional<Rule> ofResult = Optional.of(rule);
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
    when(ruleUsageRepository.save(Mockito.<RuleUsage>any())).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.createRuleUsage(1));
    verify(ruleRepository).findById(1);
    verify(ruleUsageRepository).save(isA(RuleUsage.class));
  }


  @Test
  void testCreateRuleUsage3() {
    // Arrange
    Optional<Rule> emptyResult = Optional.empty();
    when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(IllegalArgumentException.class, () -> ruleServiceImpl.createRuleUsage(1));
    verify(ruleRepository).findById(1);
  }

  @Test
  void testGetTotalRulesCount() {
    // Arrange
    when(ruleRepository.count()).thenReturn(3L);

    // Act
    long actualTotalRulesCount = ruleServiceImpl.getTotalRulesCount();

    // Assert
    verify(ruleRepository).count();
    assertEquals(3L, actualTotalRulesCount);
  }


  @Test
  void testGetTotalRulesCount2() {
    // Arrange
    when(ruleRepository.count()).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.getTotalRulesCount());
    verify(ruleRepository).count();
  }


  @Test
  void testGetTotalRuleUsages() {
    // Arrange
    when(ruleUsageRepository.count()).thenReturn(3L);

    // Act
    long actualTotalRuleUsages = ruleServiceImpl.getTotalRuleUsages();

    // Assert
    verify(ruleUsageRepository).count();
    assertEquals(3L, actualTotalRuleUsages);
  }

 
  @Test
  void testGetTotalRuleUsages2() {
    // Arrange
    when(ruleUsageRepository.count()).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> ruleServiceImpl.getTotalRuleUsages());
    verify(ruleUsageRepository).count();
  }


}
