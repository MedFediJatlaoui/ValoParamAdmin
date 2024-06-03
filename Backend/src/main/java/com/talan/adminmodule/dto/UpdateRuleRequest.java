package com.talan.adminmodule.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRuleRequest {
    private final Integer id;
    private final RuleUpdateDto updatedRuleDto;
    private final String modDescription;
    private final String modifiedBy;
    private final String imageUrl ;

    public UpdateRuleRequest(Integer id, RuleUpdateDto updatedRuleDto, String modDescription, String modifiedBy, String imageUrl) {
        this.id = id;
        this.updatedRuleDto = updatedRuleDto;
        this.modDescription = modDescription;
        this.modifiedBy = modifiedBy;
        this.imageUrl = imageUrl;
    }
}
