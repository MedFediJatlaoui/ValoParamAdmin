package com.talan.adminmodule.service.impl;

import com.talan.adminmodule.config.exception.EntityNotFoundException;
import com.talan.adminmodule.config.exception.ErrorCodes;
import com.talan.adminmodule.config.exception.InvalidEntityException;
import com.talan.adminmodule.dto.AttributeDto;
import com.talan.adminmodule.entity.Attribute;
import com.talan.adminmodule.repository.AttributeRepository;
import com.talan.adminmodule.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeServiceImpl implements AttributeService {
    private final AttributeRepository attributeRepository;

    @Autowired
    public AttributeServiceImpl(AttributeRepository attributeRepository) {
        this.attributeRepository = attributeRepository;
    }

    @Override
    public AttributeDto save(AttributeDto attributeDTO) {
        if (attributeDTO == null) {
            throw new InvalidEntityException("Attribute is null", ErrorCodes.ATTRIBUTE_NOT_VALID);
        }

        Attribute attribute = AttributeDto.toEntity(attributeDTO);
        attribute = attributeRepository.save(attribute);
        return AttributeDto.fromEntity(attribute);
    }

    @Override
    public void delete(Integer id) {
        Attribute attribute = attributeRepository.findById(id).orElse(null);
        if (attribute == null) {
            throw new EntityNotFoundException("Attribute with ID = " + id + " not found in the database" , ErrorCodes.ATTRIBUTE_NOT_FOUND);
        }
        attributeRepository.deleteById(id);
    }

    @Override
    public AttributeDto findById(Integer id) {
        Attribute attribute = attributeRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Attribute with ID = " + id + " not found in the database", ErrorCodes.ATTRIBUTE_NOT_FOUND)
        );
        return AttributeDto.fromEntity(attribute);
    }

    @Override
    public List<AttributeDto> findAll() {
        return attributeRepository.findAll().stream()
                .map(AttributeDto::fromEntity)
                .toList();
    }

    @Override
    public boolean existByName(String name) {
        return attributeRepository.existsByName(name);
    }
}
