package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.RuleUsage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RuleUsageRepository extends CrudRepository<RuleUsage, Integer> {
    List<RuleUsage> findAllByCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate);

  }