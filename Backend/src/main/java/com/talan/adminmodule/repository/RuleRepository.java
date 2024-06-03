package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.Rule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Integer> {
    @Query("SELECT r FROM Rule r LEFT JOIN FETCH r.ruleAttributes ra WHERE r.id = :id")
    Optional<Rule> findByIdWithAttributes(@Param("id") Integer id);

    @Query("SELECT r FROM Rule r WHERE LOWER(r.name) LIKE LOWER(CONCAT(:query, '%'))")
    Page<Rule> search(String query, Pageable pageable);

}

