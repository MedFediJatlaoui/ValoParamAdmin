package com.talan.adminmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleUsageDTO {
    private String ruleName;
    private List<DayUsageDTO> dayUsages;

}
