package com.talan.adminmodule.service;

import com.talan.adminmodule.dto.AttributeDto;

import java.util.List;

public interface AttributeService {
    AttributeDto save(AttributeDto attributeDTO);

    void delete(Integer id);

    AttributeDto findById(Integer id);

    List<AttributeDto> findAll();

    boolean existByName(String name) ;


}
