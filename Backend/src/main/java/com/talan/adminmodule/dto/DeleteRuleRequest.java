package com.talan.adminmodule.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteRuleRequest {
    private final Integer id;
    private final String modifiedBy;
    private final String imageUrl ;

    public DeleteRuleRequest(Integer id, String modifiedBy, String imageUrl) {
        this.id = id;
        this.modifiedBy = modifiedBy;
        this.imageUrl = imageUrl;
    }
}