package com.talan.adminmodule.repository;

import com.talan.adminmodule.dto.ParamTableCount;
import com.talan.adminmodule.entity.ParamAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ParamAuditRepository extends JpaRepository<ParamAudit, Integer> {
    @Query("SELECT COALESCE(MAX(p.version), 0) FROM ParamAudit p WHERE p.tableName = :tableName")
    Integer findMaxVersionByTableName(String tableName);
    List<ParamAudit>findByTableName(String tableName);

@Query("SELECT new com.talan.adminmodule.dto.ParamTableCount(e.tableName,COUNT(e))" +"" +
        "FROM ParamAudit e "+ "WHERE (YEAR(e.createdAt) = YEAR(CURRENT_DATE) OR YEAR(e.lastModifiedAt) = YEAR(CURRENT_DATE)) " +
        "GROUP BY e.tableName ")

    List<ParamTableCount> paramTablesTreeMap();

long count();


}
