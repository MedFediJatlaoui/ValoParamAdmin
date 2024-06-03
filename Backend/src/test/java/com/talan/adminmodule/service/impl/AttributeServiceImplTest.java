package com.talan.adminmodule.service.impl;

import com.talan.adminmodule.config.exception.EntityNotFoundException;
import com.talan.adminmodule.config.exception.InvalidEntityException;
import com.talan.adminmodule.dto.AttributeDto;
import com.talan.adminmodule.entity.Attribute;
import com.talan.adminmodule.repository.AttributeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AttributeServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AttributeServiceImplTest {
  @MockBean
  private AttributeRepository attributeRepository;

  @Autowired
  private AttributeServiceImpl attributeServiceImpl;
  
  @Test
  void testSave() {
    // Arrange, Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.save(null));
  }

  @Test
  void testSave2() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());
    when(attributeRepository.save(Mockito.<Attribute>any())).thenReturn(attribute);
    AttributeDto attributeDTO = AttributeDto.builder().id(1).name("Name").build();

    // Act
    AttributeDto actualSaveResult = attributeServiceImpl.save(attributeDTO);

    // Assert
    verify(attributeRepository).save(isA(Attribute.class));
    assertEquals("Name", actualSaveResult.getName());
    assertEquals(1, actualSaveResult.getId().intValue());
  }

 
  @Test
  void testSave3() {
    // Arrange
    when(attributeRepository.save(Mockito.<Attribute>any())).thenThrow(new InvalidEntityException("An error occurred"));
    AttributeDto attributeDTO = AttributeDto.builder().id(1).name("Name").build();

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.save(attributeDTO));
    verify(attributeRepository).save(isA(Attribute.class));
  }

  
  @Test
  void testSave4() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());
    when(attributeRepository.save(Mockito.<Attribute>any())).thenReturn(attribute);
    AttributeDto attributeDTO = mock(AttributeDto.class);
    when(attributeDTO.getName()).thenReturn("Name");

    // Act
    AttributeDto actualSaveResult = attributeServiceImpl.save(attributeDTO);

    // Assert
    verify(attributeDTO).getName();
    verify(attributeRepository).save(isA(Attribute.class));
    assertEquals("Name", actualSaveResult.getName());
    assertEquals(1, actualSaveResult.getId().intValue());
  }

  @Test
  void testSave5() {
    // Arrange
    AttributeDto attributeDTO = mock(AttributeDto.class);
    when(attributeDTO.getName()).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.save(attributeDTO));
    verify(attributeDTO).getName();
  }

 
  @Test
  void testDelete() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());
    Optional<Attribute> ofResult = Optional.of(attribute);
    doNothing().when(attributeRepository).deleteById(Mockito.<Integer>any());
    when(attributeRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act
    attributeServiceImpl.delete(1);

    // Assert that nothing has changed
    verify(attributeRepository).deleteById(1);
    verify(attributeRepository).findById(1);
  }

  @Test
  void testDelete2() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());
    Optional<Attribute> ofResult = Optional.of(attribute);
    doThrow(new InvalidEntityException("An error occurred")).when(attributeRepository)
            .deleteById(Mockito.<Integer>any());
    when(attributeRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.delete(1));
    verify(attributeRepository).deleteById(1);
    verify(attributeRepository).findById(1);
  }

  @Test
  void testDelete3() {
    // Arrange
    Optional<Attribute> emptyResult = Optional.empty();
    when(attributeRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(EntityNotFoundException.class, () -> attributeServiceImpl.delete(1));
    verify(attributeRepository).findById(1);
  }

  
  @Test
  void testFindById() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());
    Optional<Attribute> ofResult = Optional.of(attribute);
    when(attributeRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);

    // Act
    AttributeDto actualFindByIdResult = attributeServiceImpl.findById(1);

    // Assert
    verify(attributeRepository).findById(1);
    assertEquals("Name", actualFindByIdResult.getName());
    assertEquals(1, actualFindByIdResult.getId().intValue());
  }


  @Test
  void testFindById2() {
    // Arrange
    Optional<Attribute> emptyResult = Optional.empty();
    when(attributeRepository.findById(Mockito.<Integer>any())).thenReturn(emptyResult);

    // Act and Assert
    assertThrows(EntityNotFoundException.class, () -> attributeServiceImpl.findById(1));
    verify(attributeRepository).findById(1);
  }

  @Test
  void testFindById3() {
    // Arrange
    when(attributeRepository.findById(Mockito.<Integer>any()))
            .thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.findById(1));
    verify(attributeRepository).findById(1);
  }

  @Test
  void testFindAll() {
    // Arrange
    when(attributeRepository.findAll()).thenReturn(new ArrayList<>());

    // Act
    List<AttributeDto> actualFindAllResult = attributeServiceImpl.findAll();

    // Assert
    verify(attributeRepository).findAll();
    assertTrue(actualFindAllResult.isEmpty());
  }

  @Test
  void testFindAll2() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    ArrayList<Attribute> attributeList = new ArrayList<>();
    attributeList.add(attribute);
    when(attributeRepository.findAll()).thenReturn(attributeList);

    // Act
    List<AttributeDto> actualFindAllResult = attributeServiceImpl.findAll();

    // Assert
    verify(attributeRepository).findAll();
    assertEquals(1, actualFindAllResult.size());
    AttributeDto getResult = actualFindAllResult.get(0);
    assertEquals("Name", getResult.getName());
    assertEquals(1, getResult.getId().intValue());
  }
  
  @Test
  void testFindAll3() {
    // Arrange
    Attribute attribute = new Attribute();
    attribute.setId(1);
    attribute.setName("Name");
    attribute.setRuleAttributes(new ArrayList<>());

    Attribute attribute2 = new Attribute();
    attribute2.setId(2);
    attribute2.setName("com.talan.adminmodule.entity.Attribute");
    attribute2.setRuleAttributes(new ArrayList<>());

    ArrayList<Attribute> attributeList = new ArrayList<>();
    attributeList.add(attribute2);
    attributeList.add(attribute);
    when(attributeRepository.findAll()).thenReturn(attributeList);

    // Act
    List<AttributeDto> actualFindAllResult = attributeServiceImpl.findAll();

    // Assert
    verify(attributeRepository).findAll();
    assertEquals(2, actualFindAllResult.size());
    AttributeDto getResult = actualFindAllResult.get(1);
    assertEquals("Name", getResult.getName());
    AttributeDto getResult2 = actualFindAllResult.get(0);
    assertEquals("com.talan.adminmodule.entity.Attribute", getResult2.getName());
    assertEquals(1, getResult.getId().intValue());
    assertEquals(2, getResult2.getId().intValue());
  }

  
  @Test
  void testFindAll4() {
    // Arrange
    when(attributeRepository.findAll()).thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.findAll());
    verify(attributeRepository).findAll();
  }

  
  @Test
  void testExistByName() {
    // Arrange
    when(attributeRepository.existsByName(Mockito.<String>any())).thenReturn(true);

    // Act
    boolean actualExistByNameResult = attributeServiceImpl.existByName("Name");

    // Assert
    verify(attributeRepository).existsByName("Name");
    assertTrue(actualExistByNameResult);
  }

  @Test
  void testExistByName2() {
    // Arrange
    when(attributeRepository.existsByName(Mockito.<String>any())).thenReturn(false);

    // Act
    boolean actualExistByNameResult = attributeServiceImpl.existByName("Name");

    // Assert
    verify(attributeRepository).existsByName("Name");
    assertFalse(actualExistByNameResult);
  }
  
  @Test
  void testExistByName3() {
    // Arrange
    when(attributeRepository.existsByName(Mockito.<String>any()))
            .thenThrow(new InvalidEntityException("An error occurred"));

    // Act and Assert
    assertThrows(InvalidEntityException.class, () -> attributeServiceImpl.existByName("Name"));
    verify(attributeRepository).existsByName("Name");
  }
}
