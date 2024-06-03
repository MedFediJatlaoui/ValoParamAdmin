package com.talan.adminmodule.controller;

import com.talan.adminmodule.dto.AttributeDto;
import com.talan.adminmodule.service.AttributeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Attribute")
@CrossOrigin(origins = "*")
@RequestMapping("api/attributes")
public class AttributeController {

    private final AttributeService attributeService;

    @Autowired
    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributeDto> getAttributeById(@PathVariable Integer id) {
        AttributeDto attributeDto = attributeService.findById(id);
        return ResponseEntity.ok(attributeDto);
    }

    @GetMapping
    public ResponseEntity<List<AttributeDto>> getAllAttributes() {
        List<AttributeDto> attributeDtoList = attributeService.findAll();
        return ResponseEntity.ok(attributeDtoList);
    }
}
