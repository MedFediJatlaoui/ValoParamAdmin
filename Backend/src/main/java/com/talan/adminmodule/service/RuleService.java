package com.talan.adminmodule.service;

import com.talan.adminmodule.dto.RuleDto;
import com.talan.adminmodule.dto.RuleModificationDto;
import com.talan.adminmodule.dto.RuleUpdateDto;
import com.talan.adminmodule.dto.RuleUsageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RuleService {

    RuleDto save(RuleDto ruleDto);
    public void queueDelete(Integer id, String modifiedBy, String imageUrl) ;
    void delete(Integer id);

    RuleDto findById(Integer id);

    Page<RuleDto> findAll(int page, int size);

    List<RuleModificationDto> getModificationsByRuleId(Integer id);
    Page<RuleDto> searchRules(int page, int size, String query);
    void queueUpdate(Integer id, RuleUpdateDto updatedRuleDto, String modDescription, String modifiedBy, String imageUrl);


    List<RuleModificationDto> getAllModifications();

    List<RuleUsageDTO> getTop5UsedRulesForLast18Days();

    void createRuleUsage(int ruleId);

    long getTotalRulesCount();

    long getTotalRuleUsages();
}
