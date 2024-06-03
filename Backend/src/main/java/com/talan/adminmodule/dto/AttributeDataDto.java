package com.talan.adminmodule.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeDataDto{
    private Integer id ;
    private AttributeDto name ;
    private Double percentage ;
    private Double value ;
}