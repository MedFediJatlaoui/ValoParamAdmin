package com.talan.adminmodule.service.impl;

import com.talan.adminmodule.config.exception.EntityNotFoundException;
import com.talan.adminmodule.config.exception.ErrorCodes;
import com.talan.adminmodule.config.exception.InvalidEntityException;
import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.entity.*;
import com.talan.adminmodule.repository.*;
import com.talan.adminmodule.service.RuleService;
import com.talan.adminmodule.validator.RuleUpdateValidator;
import com.talan.adminmodule.validator.RuleValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final AttributeRepository attributeRepository;
    private final CategoryRepository categoryRepository;
    private final RuleModificationRepository ruleModificationRepository ;
    private final RuleAttributeRepository  ruleAttributeRepository ;
    private final RuleUsageRepository  ruleUsageRepository ;

    private final String ruleMessage = "Rule with ID :" ;
    private final String ruleNotFound = "not found" ;
    private final Queue<UpdateRuleRequest> updateQueue = new LinkedList<>();

    private final Queue<DeleteRuleRequest> deleteQueue = new LinkedList<>(); // Queue for disable requests


    @Autowired
    public RuleServiceImpl(RuleRepository ruleRepository, AttributeRepository attributeRepository, CategoryRepository categoryRepository, RuleModificationRepository ruleModificationRepository, RuleAttributeRepository ruleAttributeRepository, RuleUsageRepository ruleUsageRepository) {
        this.ruleRepository = ruleRepository;
        this.attributeRepository = attributeRepository;
        this.categoryRepository = categoryRepository;
        this.ruleModificationRepository = ruleModificationRepository;
        this.ruleAttributeRepository = ruleAttributeRepository;
        this.ruleUsageRepository = ruleUsageRepository;
    }


    @Override
    @Transactional
    public RuleDto save(RuleDto ruleDto) {
        List<String> errors = RuleValidator.validate(ruleDto);
        if (!errors.isEmpty()) {
            throw new InvalidEntityException("Rule is not valid", ErrorCodes.RULE_NOT_VALID, errors);
        }

        Rule rule = new Rule();
        rule.setName(ruleDto.getName());
        rule.setDescription(ruleDto.getDescription());
        Category category = categoryRepository.findByName(ruleDto.getCategory().getName());
        if (category == null) {
            Category newCategory = new Category();
            newCategory.setName(ruleDto.getCategory().getName());
            category = categoryRepository.save(newCategory);
        }
        rule.setCategory(category);
        rule = ruleRepository.save(rule);
        List<RuleAttribute> ruleAttributes = new ArrayList<>();
        for (AttributeDataDto attributeDto : ruleDto.getAttributeDtos()) {
            Attribute attribute = attributeRepository.findByNameIgnoreCase(attributeDto.getName().getName());
            if (attribute == null) {
                // Create a new attribute if it doesn't exist
                attribute = new Attribute();
                attribute.setName(attributeDto.getName().getName());
                // Set other properties of the attribute if needed
                attribute = attributeRepository.save(attribute);
            }

            // Create RuleAttribute and associate with rule and attribute
            RuleAttribute ruleAttribute = new RuleAttribute();
            ruleAttribute.setRule(rule);
            ruleAttribute.setAttribute(attribute);
            ruleAttribute.setPercentage(attributeDto.getPercentage());
            ruleAttribute.setValue(attributeDto.getValue());
            ruleAttributes.add(ruleAttribute);
        }

        // Save the list of RuleAttributes
        rule.setRuleAttributes(ruleAttributes);

        return RuleDto.fromEntity(rule);
    }


    @Transactional
    public boolean updateStatus(Integer id, String status , String modifiedBy , String imageUrl) {
        Rule rule = ruleRepository.findById(id).orElse(null);
        if (rule == null) {
            throw new EntityNotFoundException(ruleMessage + id + ruleNotFound, ErrorCodes.RULE_NOT_FOUND);
        }

        rule.setStatus(status);
        if (status.equalsIgnoreCase("Deleted")){
            this.saveRuleModification(rule, "Rule deleted" , modifiedBy , "deleted" , imageUrl);
        }
        rule = ruleRepository.save(rule);
        return true;
    }
    @Transactional
    public boolean updateRule(Integer id, RuleUpdateDto updatedRuleDto, String modDescription, String modifiedBy, String imageUrl) {
        Rule existingRule = ruleRepository.findById(id).orElse(null);
        if (existingRule == null) {
            throw new EntityNotFoundException(ruleMessage + id + ruleNotFound, ErrorCodes.RULE_NOT_FOUND);
        }

        List<String> errors = RuleUpdateValidator.validate(updatedRuleDto);
        if (!errors.isEmpty()) {
            throw new InvalidEntityException("Updated rule is not valid", ErrorCodes.RULE_NOT_VALID, errors);
        }

        existingRule.setName(updatedRuleDto.getName());
        existingRule.setDescription(updatedRuleDto.getDescription());
        Category category = categoryRepository.findByName(updatedRuleDto.getCategory().getName());
        if (category == null) {
            category = CategoryDto.toEntity(updatedRuleDto.getCategory());
            category = categoryRepository.save(category);
        }
        existingRule.setCategory(category);
        ruleAttributeRepository.deleteByRule(existingRule);
        List<RuleAttribute> updatedRuleAttributes = new ArrayList<>();
        for (AttributeDataDto attributeDto : updatedRuleDto.getAttributeDtos()) {
            Attribute attribute = attributeRepository.findByNameIgnoreCase(attributeDto.getName().getName());
            if (attribute == null) {
                attribute = AttributeDto.toEntity(attributeDto.getName());
                attribute = attributeRepository.save(attribute);
            }
            RuleAttribute ruleAttribute = this.addRuleAttribute(attribute, existingRule, attributeDto);
            updatedRuleAttributes.add(ruleAttribute);
        }
        existingRule.setRuleAttributes(updatedRuleAttributes);
        existingRule = ruleRepository.save(existingRule);
        this.saveRuleModification(existingRule, modDescription , modifiedBy , "updated" , imageUrl);
        return true;
    }

    public void saveRuleModification(Rule existingRule, String modDescription , String modifiedBy , String modificationType , String imageUrl) {
        RuleModification ruleModification = new RuleModification();
        ruleModification.setRule(existingRule);
        ruleModification.setModificationDate(existingRule.getLastModified());
        ruleModification.setModifiedBy(modifiedBy);
        ruleModification.setRuleName(existingRule.getName());
        ruleModification.setModificationDescription(modDescription);
        ruleModification.setModificationType(modificationType);
        ruleModification.setProfileImagePath(imageUrl);
        this.ruleModificationRepository.save(ruleModification);
    }

    public RuleAttribute addRuleAttribute(Attribute attribute, Rule existingRule, AttributeDataDto attributeDto) {
        RuleAttribute ruleAttribute = new RuleAttribute();
        ruleAttribute.setRule(existingRule);
        ruleAttribute.setAttribute(attribute);
        ruleAttribute.setPercentage(attributeDto.getPercentage());
        ruleAttribute.setValue(attributeDto.getValue());
        return ruleAttribute;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Rule rule = ruleRepository.findById(id).orElse(null);
        if (rule == null) {
            throw new EntityNotFoundException(ruleMessage + id + ruleNotFound, ErrorCodes.RULE_NOT_FOUND);
        }
        ruleRepository.deleteById(id);
    }

    @Override
    public RuleDto findById(Integer id) {
        return ruleRepository.findByIdWithAttributes(id).map(RuleDto::fromEntity).orElse(null);
    }

    @Override
    public Page<RuleDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastModified").descending());
        return ruleRepository.findAll(pageable).map(RuleDto::fromEntity);
    }

    @Override
    public List<RuleModificationDto> getModificationsByRuleId(Integer id) {
        Rule rule = ruleRepository.findById(id).orElse(null);
        if (rule == null) {
            throw new EntityNotFoundException(ruleMessage + id + ruleNotFound, ErrorCodes.RULE_NOT_FOUND);
        }

        List<RuleModification> modifications = ruleModificationRepository.findByRuleOrderByModificationDateDesc(rule);
        return modifications.stream()
                .map(RuleModificationDto::fromEntity)
                .toList();
    }



    @Override
    public Page<RuleDto> searchRules(int page, int size, String query) {
        Pageable pageable = PageRequest.of(page, size);
        return ruleRepository.search(query, pageable).map(RuleDto::fromEntity);
    }

    @Override
    public void queueUpdate(Integer id, RuleUpdateDto updatedRuleDto, String modDescription, String modifiedBy, String imageUrl) {
        updateQueue.offer(new UpdateRuleRequest(id, updatedRuleDto, modDescription, modifiedBy, imageUrl));
        this.updateStatus(id,"Scheduled update",modifiedBy,imageUrl) ;
    }
    @Override
    public void queueDelete(Integer id, String modifiedBy, String imageUrl) {
        this.updateStatus(id,"Scheduled delete" , modifiedBy , imageUrl) ;
        this.deleteQueue.offer(new DeleteRuleRequest(id,modifiedBy, imageUrl));
    }

    @Override
    public List<RuleModificationDto> getAllModifications() {
        List<RuleModification> modifications = ruleModificationRepository.findAll();
        List<RuleModificationDto> sortedModifications = modifications.stream()
                .map(RuleModificationDto::fromEntity)
                .sorted(Comparator.comparing(RuleModificationDto::getModificationDate).reversed())
                .toList();

        return sortedModifications;
    }

    @Override
    public List<RuleUsageDTO> getTop5UsedRulesForLast18Days() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(17);

        List<RuleUsage> ruleUsages = ruleUsageRepository.findAllByCreateDateBetween(startDate.atStartOfDay(), today.atTime(23, 59, 59));

        Map<String, Map<LocalDate, Long>> ruleUsageMap = ruleUsages.stream()
                .collect(Collectors.groupingBy(ruleUsage -> ruleUsage.getRule().getName(),
                        Collectors.groupingBy(ruleUsage -> ruleUsage.getCreateDate().toLocalDate(), Collectors.counting())));

        List<RuleUsageDTO> top5UsedRules = ruleUsageMap.entrySet().stream()
                .map(entry -> {
                    String ruleName = entry.getKey();
                    List<DayUsageDTO> dayUsages = entry.getValue().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(subEntry -> new DayUsageDTO(subEntry.getKey(), subEntry.getValue().intValue()))
                            .collect(Collectors.toList());
                    return new RuleUsageDTO(ruleName, dayUsages);
                })
                .sorted(Comparator.comparingInt(ruleUsageDTO ->
                        ruleUsageDTO.getDayUsages().stream().mapToInt(DayUsageDTO::getUsageCount).sum()))
                .limit(5)
                .collect(Collectors.toList());

        return top5UsedRules;
    }

    @Override
    public void createRuleUsage(int ruleId) {
        Optional<Rule> ruleOptional = ruleRepository.findById(ruleId);
        if (ruleOptional.isPresent()) {
            Rule rule = ruleOptional.get();
            RuleUsage ruleUsage = new RuleUsage();
            ruleUsage.setRule(rule);
            ruleUsage.setCreateDate(LocalDateTime.now());
            ruleUsageRepository.save(ruleUsage);
        } else {
            throw new IllegalArgumentException("Rule with ID " + ruleId + " not found.");
        }
    }

    @Override
    public long getTotalRulesCount() {
        return ruleRepository.count();

    }

    @Override
    public long getTotalRuleUsages() {
        return ruleUsageRepository.count();
    }

    @Scheduled(fixedDelay = 9000)
    public void processQueuedUpdates() {
        while (!updateQueue.isEmpty()) {
            UpdateRuleRequest request = updateQueue.poll();
            if (request != null) {
                updateRule(request.getId(), request.getUpdatedRuleDto(), request.getModDescription(), request.getModifiedBy() , request.getImageUrl());
                this.updateStatus(request.getId() , "Enabled" , request.getModifiedBy() , "") ;
            }
        }
    }


    @Scheduled(fixedDelay = 9000)
    public void processQueuedDeletes() {
        while (!deleteQueue.isEmpty()) {
            DeleteRuleRequest request = deleteQueue.poll();
            if (request != null) {
                this.updateStatus(request.getId() , "Deleted" , request.getModifiedBy() , request.getImageUrl()) ;
            }
        }


    }

}
