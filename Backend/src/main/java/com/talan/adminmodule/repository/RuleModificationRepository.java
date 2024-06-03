package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.Rule;
import com.talan.adminmodule.entity.RuleModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleModificationRepository extends JpaRepository<RuleModification, Integer> {
    List<RuleModification> findByRuleOrderByModificationDateDesc(Rule rule);
}
