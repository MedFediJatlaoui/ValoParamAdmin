package com.talan.adminmodule.dto;

import com.talan.adminmodule.entity.Rule;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RuleDto {

    private Integer id;
    private String name;
    private String description;
    private CategoryDto category ;
    private String status;
    private LocalDateTime createDate;
    private LocalDateTime lastModified;
    private Integer createdBy;
    private Integer lastModifiedBy;
    private List<AttributeDataDto> attributeDtos ;


    public static RuleDto fromEntity(Rule rule) {
        if (rule == null) {
            return null;
        }
        RuleDto ruleDto = RuleDto.builder()
                .id(rule.getId())
                .name(rule.getName())
                .description(rule.getDescription())
                .status(rule.getStatus())
                .createDate(rule.getCreateDate())
                .lastModified(rule.getLastModified())
                .createdBy(rule.getCreatedBy())
                .lastModifiedBy(rule.getLastModifiedBy())
                .category(CategoryDto.fromEntity(rule.getCategory()))
                .build();

        List<AttributeDataDto> attributeDtos = rule.getRuleAttributes().stream()
                .map(ruleAttribute -> AttributeDataDto.builder()
                        .id(ruleAttribute.getAttribute().getId())
                        .name(AttributeDto.builder().id(ruleAttribute.getAttribute().getId()).name(ruleAttribute.getAttribute().getName()).build())
                        .percentage(ruleAttribute.getPercentage())
                        .value(ruleAttribute.getValue())
                        .build())
                .toList();

        ruleDto.setAttributeDtos(attributeDtos);

        return ruleDto;
    }


    public static Rule toEntity(RuleDto ruleDTO) {
        if (ruleDTO == null) {
            return null;
        }
        Rule rule = new Rule();
        rule.setName(ruleDTO.getName());
        rule.setDescription(ruleDTO.getDescription());
        rule.setStatus(ruleDTO.getStatus());
        return rule;
    }
}