package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.Rule;
import com.talan.adminmodule.entity.RuleAttribute;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleAttributeRepository extends JpaRepository<RuleAttribute, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM RuleAttribute ra WHERE ra.rule = :rule")
    void deleteByRule(@Param("rule") Rule rule);
}
