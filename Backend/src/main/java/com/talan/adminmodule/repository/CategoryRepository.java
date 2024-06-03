package com.talan.adminmodule.repository;

import com.talan.adminmodule.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);

    @Query("SELECT c, COUNT(r) " +
            "FROM Category c " +
            "JOIN c.rules r " +
            "GROUP BY c " +
            "ORDER BY COUNT(r) DESC")
    List<Object[]> findTopUsedCategoriesWithRuleCount(Pageable pageable);

    @Query("SELECT COUNT(r) " +
            "FROM Category c " +
            "JOIN c.rules r")
    long findTotalRuleCount();}