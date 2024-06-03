package com.talan.adminmodule.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RuleUpdateDto {

    private Integer id;
    private String name;
    private String description;
    private CategoryDto category;
    private String status;
    private LocalDateTime createDate;
    private LocalDateTime lastModified;
    private Integer createdBy;
    private Integer lastModifiedBy;
    private List<AttributeDataDto> attributeDtos;
    private String imageUrl ;

}