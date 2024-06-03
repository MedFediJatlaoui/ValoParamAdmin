package com.talan.adminmodule.service;

import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.entity.ParamAudit;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

public interface ParamTableService {
     TablesWithColumns retrieveAllTablesWithFilteredColumns(int limit, int offset) ;
     List<ColumnInfo> getAllColumns(String tableName);

    DataFromTable getDataFromTable(String tableName, TableDataRequest request);

    StringBuilder buildSqlQuery(String tableName, TableDataRequest request);

    StringBuilder buildSelectClause(String tableName, TableDataRequest request);

    void appendAllColumns(StringBuilder selectClause, List<String> columns);

    void appendSpecifiedColumns(StringBuilder selectClause, List<String> columns, String pk, TableDataRequest request);

    String orderByClause(String tableName, TableDataRequest request);

    String limitOffsetClause(TableDataRequest request);

    ResponseDto addInstance(Map<String, String> instanceData, String tableName);

    String getUsernameFromSecurityContext();

    ResponseDto addUpdateRequest(UpdateRequest updateRequest);

    ResponseDto cancelUpdateRequest(String primaryKeyValue, String tableName);

    List<UpdateRequest> getUpdateRequestByTable(String tableName);

    List<DeleteRequest> getDeleteRequestByTable(String tableName);

    ResponseDto updateInstance(UpdateRequest updateRequest, Integer version);

    boolean simulateDelete(DeleteRequest request);

    boolean simulateUpdate(UpdateRequest updateRequest);

    List<ForeignKeyOption> foreignKeyoptions(String tableName);

    ResponseDto addDeleteRequest(DeleteRequest deleteRequest);

    @Transactional
    ResponseDto deletecascade(DeleteRequest deleteRequest);

    TreeMapData tablesforDashboard();

    void checkReferencedRecursive(DeleteRequest deleteRequest, List<DeleteRequest> response);

    Boolean checkunicity(String primaryKeyValue, String tableName);

    List<DeleteRequest> checkReferenced(DeleteRequest deleteRequest);

    @Transactional
    ResponseDto cancelDeleteRequest(String tableName, String primaryKeyValue);

    ResponseDto deleteInstance(DeleteRequest deleteRequest, Integer version);

    Integer findMaxVersionByTableName(String tableName);

    Object convertToDataType(String inputValue, String columnType);

    ColumnInfo primaryKeyDetails(String tableName);

    List<ParamAudit> paramHistory(String tableName);

}
