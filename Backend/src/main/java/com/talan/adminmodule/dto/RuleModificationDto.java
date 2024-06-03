package com.talan.adminmodule.dto;

import com.talan.adminmodule.entity.RuleModification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RuleModificationDto {

    private Integer id;
    private RuleDto ruleDto;
    private LocalDateTime modificationDate;
    private String modifiedBy;
    private String ruleName;
    private Integer ruleId ;
    private String modificationDescription;
    private String modificationType;
    private String profileImagePath ;

    public static RuleModificationDto fromEntity(RuleModification ruleModification) {
        if (ruleModification == null) {
            return null;
        }
        return RuleModificationDto.builder()
                .id(ruleModification.getId())
                .modificationDate(ruleModification.getModificationDate())
                .modifiedBy(ruleModification.getModifiedBy())
                .ruleName(ruleModification.getRuleName())
                .modificationDescription(ruleModification.getModificationDescription())
                .modificationType(ruleModification.getModificationType())
                .profileImagePath(ruleModification.getProfileImagePath())
                .ruleId(ruleModification.getRule().getId())
                .build();
    }
}