package com.talan.adminmodule.service.impl;
import com.talan.adminmodule.config.DatabaseInitializer;
import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.dto.ColumnInfo;
import com.talan.adminmodule.entity.ParamAudit;
import com.talan.adminmodule.repository.ParamAuditRepository;
import com.talan.adminmodule.service.ParamTableService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.postgresql.jdbc.PgArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Service
public class ParamTableServiceImpl implements ParamTableService {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private DatabaseInitializer databaseInitializer;
    @Autowired
    private ParamAuditRepository paramAuditRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PlatformTransactionManager transactionManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamTableServiceImpl.class);
    private TablesWithColumns allTablesWithColumns =new TablesWithColumns();
    public static final String ACTIVE ="active";
    public static final String UNDEFINED ="undefined";
    List<UpdateRequest>updateRequests = new ArrayList<>();
    List<DeleteRequest> deleteRequests =new ArrayList<>();


    @PostConstruct
    public void initialize() {
        allTablesWithColumns = databaseInitializer.getAllTablesWithColumns();
    }
    public ParamTableServiceImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
@Override
    public TablesWithColumns retrieveAllTablesWithFilteredColumns(int limit, int offset) {
        List<TableInfo> paginatedTables = new ArrayList<>();
        TablesWithColumns tablesWithColumns = new TablesWithColumns();
        int endIndex = (int) Math.min(allTablesWithColumns.getNumberTables(), offset + (long)limit);

        for (int i = offset; i < endIndex; i++) {
            TableInfo paramTable = allTablesWithColumns.getAllTablesWithColumns().get(i);
            List<ColumnInfo> columns = paramTable.getColumns();
            List<ColumnInfo> filteredColumns =  columns.stream().filter(column->!column.getName().equals(ACTIVE)).toList();
            paramTable.setColumns(filteredColumns);
            paginatedTables.add(paramTable);
        }
        tablesWithColumns.setAllTablesWithColumns(paginatedTables);
        tablesWithColumns.setNumberTables(allTablesWithColumns.getNumberTables());
        return tablesWithColumns;
    }
    @Override
    public List<ColumnInfo> getAllColumns(String tableName) {
        TableInfo tableInfoOptional=allTablesWithColumns.getAllTablesWithColumns().stream()
                .filter(table -> table.getName().equals(tableName))
                .findFirst().orElse(null);
        List<ColumnInfo> alltablecolumns=new ArrayList<>();
        if (tableInfoOptional!=null){
            alltablecolumns  = tableInfoOptional.getColumns();
        }
        return alltablecolumns;
    }
    @Override
    public DataFromTable getDataFromTable(String tableName, TableDataRequest request) {
        String sqlQuery = buildSqlQuery(tableName, request).toString();
        LOGGER.debug("Executing SQL: {}", sqlQuery);
        DataFromTable dataFromTable = new DataFromTable();
        List<String> deletedRequestsData = new ArrayList<>();
        List<String> updatedRequestsData = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put(ACTIVE, true);

        if (request.getSearch() != null && !request.getSearch().isEmpty() && !request.getSearch().equals(UNDEFINED) && !request.getColumns().isEmpty()) {
            for (int i = 0; i < request.getColumns().size(); i++) {
                params.put("search" + i, request.getSearch().toLowerCase() + "%");
            }
        }

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(sqlQuery, params);
        processColumnsForSpecialTypes(tableName, queryResult);

        dataFromTable.setData(queryResult);
        for (Map<String, Object> row : dataFromTable.getData()) {
            String primaryKeyValue = row.get(primaryKeyDetails(tableName).getName()).toString();
            for (DeleteRequest deleteRequest : getDeleteRequestByTable(tableName)) {
                if (primaryKeyValue.equals(deleteRequest.getPrimaryKeyValue())) {
                    deletedRequestsData.add(deleteRequest.getPrimaryKeyValue());
                }
            }
            for (UpdateRequest updateRequest : getUpdateRequestByTable(tableName)) {
                if (primaryKeyValue.equals(updateRequest.getInstanceData().get(primaryKeyDetails(tableName).getName()))) {
                    updatedRequestsData.add(updateRequest.getInstanceData().get(primaryKeyDetails(tableName).getName()));
                }
            }
        }
        dataFromTable.setDeleteRequests(deletedRequestsData);
        dataFromTable.setUpdateRequests(updatedRequestsData);
        return dataFromTable;
    }

    private void processColumnsForSpecialTypes(String tableName, List<Map<String, Object>> queryResult) {
        Optional<TableInfo> tab = allTablesWithColumns.getAllTablesWithColumns()
                .stream()
                .filter(tableInfo -> tableInfo.getName().equalsIgnoreCase(tableName))
                .findFirst();

        if (tab.isPresent()) {
            TableInfo table = tab.get();
            List<ColumnInfo> columns = table.getColumns()
                    .stream()
                    .filter(columnInfo -> columnInfo.getType().startsWith("_") || columnInfo.getType().startsWith("ts"))
                    .toList();
            if (!columns.isEmpty()) {
                for (Map<String, Object> row : queryResult) {
                    for (ColumnInfo column : columns) {
                        String columnName = column.getName();
                        if (row.containsKey(columnName)) {
                            Object columnValue = row.get(columnName);
                            if (columnValue instanceof PgArray || column.getType().equalsIgnoreCase("tsvector")) {
                                row.put(columnName, columnValue.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public StringBuilder buildSqlQuery(String tableName, TableDataRequest request) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append(buildSelectClause(tableName, request))
                .append(" FROM ").append(tableName)
                .append(" WHERE ").append(tableName).append(".active = 'true'");
        if (request.getSearch() != null && !request.getSearch().isEmpty() && !request.getSearch().equals(UNDEFINED) && !request.getColumns().isEmpty()) {
            sqlQuery.append(" AND (")
                    .append(request.getColumns().stream()
                            .map(column -> "LOWER ( CAST ("+column+" AS TEXT ) )" + " LIKE  '"+request.getSearch().toLowerCase()+"%'")
                            .collect(Collectors.joining(" OR ")));
            sqlQuery.append(")");
        }
        sqlQuery.append(orderByClause(tableName, request))
                .append(limitOffsetClause(request));
        return sqlQuery;
    }
    @Override
    public StringBuilder buildSelectClause(String tableName, TableDataRequest request) {
        StringBuilder selectClause = new StringBuilder("SELECT ");
        List<String> columns = getAllColumns(tableName).stream().filter(Objects::nonNull)
                .map(ColumnInfo::getName)
                .filter(column -> !column.contains(ACTIVE))
                .toList();
        String pk = primaryKeyDetails(tableName).getName();
        if (request.getColumns().isEmpty()) {
            appendAllColumns(selectClause, columns);
        } else {
            appendSpecifiedColumns(selectClause, columns,pk, request);
        }
        if (selectClause.charAt(selectClause.length() - 1) == ' ') {
            selectClause.delete(selectClause.length() - 2, selectClause.length());
        }
        return selectClause;
    }
    @Override
    public void appendAllColumns(StringBuilder selectClause, List<String> columns) {
        for (String column : columns) {
            selectClause.append(column).append(", ");
        }
    }
    @Override
    public void appendSpecifiedColumns(StringBuilder selectClause, List<String> columns, String pk, TableDataRequest request) {
        selectClause.append(pk).append(", ");
        for (String column : request.getColumns()) {
            if (columns.contains(column)&& !column.equalsIgnoreCase(pk)) {
                selectClause.append(column).append(", ");
            }
        }
    }
    @Override
    public String orderByClause(String tableName, TableDataRequest request) {
        String sortByColumn = request.getSortByColumn() != null && !request.getSortByColumn().equals("null") && !request.getSortByColumn().isEmpty()
                ? request.getSortByColumn()
                : primaryKeyDetails(tableName).getName();
        String order= request.getSortOrder()!=null &&!request.getSortOrder().equals("null") &&!request.getSortOrder().isEmpty()&& sortByColumn.equals(request.getSortByColumn()) ? request.getSortOrder() :" " ;
        return " ORDER BY " + sortByColumn +" "+order;
    }
    @Override
    public String limitOffsetClause(TableDataRequest request) {
        StringBuilder limitOffsetClause = new StringBuilder();
        if (request.getLimit() > 0) {
            limitOffsetClause.append(" LIMIT ").append(request.getLimit());
            if (request.getOffset() > 0) {
                limitOffsetClause.append(" OFFSET ").append(request.getOffset());
            }
        }
        return limitOffsetClause.toString();
    }


    @Override
    public ResponseDto addInstance(Map<String, String> instanceData, String tableName) {
        List<ColumnInfo> allColumns = getAllColumns(tableName).stream().filter(columnInfo -> !columnInfo.getName().equalsIgnoreCase(ACTIVE)).toList();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(":tableName",tableName);
        String primaryKeyName = primaryKeyDetails(tableName).getName();
        String primaryKeyType = primaryKeyDetails(tableName).getType();

        for (ColumnInfo column : allColumns) {
            String columnName = column.getName();

            if (!columnName.equalsIgnoreCase(primaryKeyName) ||
                    (columnName.equalsIgnoreCase(primaryKeyName) &&
                            !(primaryKeyType.equalsIgnoreCase("serial") || primaryKeyType.equalsIgnoreCase("bigserial")))) {
                String dataValue = instanceData.get(columnName);
                Object convertedValue = convertToDataType(dataValue, column.getType());
                params.addValue(columnName, convertedValue);
                columns.append(columnName).append(",");
                values.append(":").append(columnName).append(",");
            }
        }

        if (!columns.toString().isEmpty()) {
            columns.deleteCharAt(columns.length() - 1);
            values.deleteCharAt(values.length() - 1);
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",tableName, columns, values);
        //NOSONAR
        jdbcTemplate.update(sql, params);

        String username = getUsernameFromSecurityContext();
        Integer version = findMaxVersionByTableName(tableName) + 1;
        Map<String, String> cleanmap = instanceData.entrySet().stream()
                .filter(value -> !(value.getValue().isEmpty()) && !(value.getValue().equals(UNDEFINED)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String auditRow = cleanmap.toString().substring(1, cleanmap.toString().length() - 1);
        ParamAudit audit = ParamAudit.constructForInsertion(tableName, "ADDED", version, auditRow, username);
        paramAuditRepository.save(audit);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setSuccess("Record added successfully");
        return responseDto;
    }

    @Override
    public String getUsernameFromSecurityContext() {
        SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
        return requestWrapper.getRemoteUser();
    }
@Override
    public ResponseDto addUpdateRequest(UpdateRequest updateRequest) {
        ResponseDto responseDto = new ResponseDto();
        boolean exists = updateRequests.stream()
                .anyMatch(req -> req.getTableName().equals(updateRequest.getTableName()) &&
                        req.getInstanceData().equals(updateRequest.getInstanceData()));
        if (!exists) {
            if (simulateUpdate(updateRequest)) {
                updateRequests.add(updateRequest);
                responseDto.setSuccess("Update request validated and added successfully.");
            } else {
                responseDto.setError("Validation failed, update request not added.");
            }
        } else {
            responseDto.setError("Update request already exists.");
        }
        return responseDto;
    }
    @Override
    public ResponseDto cancelUpdateRequest(String primaryKeyValue, String tableName) {
        ResponseDto responseDto = new ResponseDto();
        String primaryKey= primaryKeyDetails(tableName).getName();
        boolean requestFound = false;
        for (UpdateRequest update : updateRequests) {
            if (update.getTableName().equals(tableName) &&
                    update.getInstanceData().get(primaryKey).equals(primaryKeyValue)) {
                int size = updateRequests.size();
                updateRequests.remove(update);
                int newsize = updateRequests.size();
                if (size != newsize) {
                    responseDto.setSuccess("Update of " + primaryKeyValue + " cancelled successfully");
                } else {
                    responseDto.setError("Update of " + primaryKeyValue + " not cancelled");
                }
                requestFound = true;
                break;
            }
        }
        if (!requestFound) {
            responseDto.setError("Request not found: " + primaryKeyValue);
        }
        return responseDto;
    }
    @Override
    public  List<UpdateRequest> getUpdateRequestByTable(String tableName){
        return updateRequests.stream()
                .filter(updateRequest -> updateRequest.getTableName().equals(tableName)).toList();
    }
    @Override
    public List<DeleteRequest> getDeleteRequestByTable(String tableName){
        return deleteRequests.stream()
                .filter(deleteRequest -> deleteRequest.getTableName().equals(tableName)).toList();
    }
    @Override
    public ResponseDto updateInstance(UpdateRequest updateRequest, Integer version) {
        ResponseDto responseDto = new ResponseDto();
        List<ColumnInfo> allColumns = getAllColumns(updateRequest.getTableName());
        StringBuilder setClause = new StringBuilder();
        StringBuilder sqlQuery = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        String primaryKeyColumn = primaryKeyDetails(updateRequest.getTableName()).getName();
        sqlQuery.append("UPDATE ")
                .append(updateRequest.getTableName())
                .append(" SET ");

        for (ColumnInfo columnMap : allColumns) {
            String columnName = columnMap.getName();
            String columnType = columnMap.getType();
            if (updateRequest.getInstanceData().containsKey(columnName)) {
                Object convertedValue = convertToDataType(updateRequest.getInstanceData().get(columnName), columnType);

                if (!columnName.equals(primaryKeyColumn)) {
                    setClause.append(columnName).append(" = :").append(columnName).append(", ");
                    params.addValue(columnName, convertedValue);
                } else {
                    params.addValue(primaryKeyColumn, convertedValue);
                }
            }
        }
        setClause.delete(setClause.length() - 2, setClause.length());
        sqlQuery.append(setClause)
                .append(" WHERE ")
                .append(primaryKeyColumn)
                .append(" = :").append(primaryKeyColumn);

        int rowsUpdated = jdbcTemplate.update(sqlQuery.toString(), params);
        if (rowsUpdated > 0) {
            responseDto.setSuccess("Instance updated successfully");
            Map<String, String> cleanmap = updateRequest.getInstanceData().entrySet().stream().filter(value -> !(value.getValue().isEmpty()) && !(value.getValue().equals(UNDEFINED))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            String auditRow = cleanmap.toString().substring(1, cleanmap.toString().length() - 1);
            ParamAudit paramAudit = ParamAudit.constructForUpdate(updateRequest.getTableName(), Objects.requireNonNull(params.getValue(primaryKeyColumn)).toString(), auditRow, "EDITED", version, updateRequest.getUsername());
            paramAuditRepository.save(paramAudit);
        } else {
            responseDto.setError("No records updated");
        }
        return responseDto;
    }

    @Override
    public boolean simulateDelete(DeleteRequest request){
        boolean result=false;
        Integer version =5;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        def.setName("simulateDeleteTransaction");
        TransactionStatus status = transactionManager.getTransaction(def);
        try { ResponseDto responseDto=deleteInstance(request,version);
            if (responseDto.getSuccess()!=null){
                result=true;}
            return result;
        }   finally {
            transactionManager.rollback(status);
        }
    }
    @Override
    public boolean simulateUpdate(UpdateRequest updateRequest) {
        boolean result=false;
        Integer version=5;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        def.setName("simulateUpdateTransaction");
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            ResponseDto responseDto=updateInstance(updateRequest,version);
            if (responseDto.getSuccess()!=null){
                result=true;}
            return result;
        }   finally {
            transactionManager.rollback(status);
        }
    }
    @Override
    public List<ForeignKeyOption> foreignKeyoptions(String tableName) {
        List<ForeignKeyOption> result = new ArrayList<>();
        List<ForeignKey> fks = allTablesWithColumns.getAllforeignKeys().stream()
                .filter(fk -> fk.getFkTableName().equals(tableName))
                .toList();

        for (ForeignKey fk : fks) {
            MapSqlParameterSource params =new MapSqlParameterSource();
            List<Map<String, Object>> options = jdbcTemplate.queryForList("SELECT "+fk.getReferencedColumn()+" FROM "+fk.getReferencedTable()+" WHERE active = 'true'",params);
            ForeignKeyOption ref = new ForeignKeyOption();
            ref.setColumn(fk.getFkColumnName());
            List<String> refoptions = new ArrayList<>();
            for (Map<String, Object> option : options) {
                refoptions.add(option.get(fk.getReferencedColumn()).toString());
            }
            ref.setOptions(refoptions);
            result.add(ref);
        }
        return result;
    }
    @Override
    public ResponseDto addDeleteRequest(DeleteRequest deleteRequest) {
        ResponseDto responseDto = new ResponseDto();
        if (deleteRequests.stream().noneMatch(req ->
                req.getTableName().equals(deleteRequest.getTableName()) &&
                        req.getPrimaryKeyValue().equals(deleteRequest.getPrimaryKeyValue()))&&simulateDelete(deleteRequest)){
            deleteRequest.setUssername(getUsernameFromSecurityContext());
            deleteRequests.add(deleteRequest);
            responseDto.setSuccess("INSTANCE ADDED TO DELETE 8 AM");
        }
        else {
            responseDto.setError("INSTANCE ALREADY EXISTS");
        }
        return responseDto ;
    }
    @Override
    @Transactional
    public ResponseDto deletecascade(DeleteRequest deleteRequest){
        List<DeleteRequest> response = new ArrayList<>();
        checkReferencedRecursive(deleteRequest, response);
        for (DeleteRequest del : response){
            addDeleteRequest(del);
        }
        ResponseDto result = new ResponseDto();
        result.setSuccess("Deletion will be executed at 8 AM");
        return result;
    }
    @Override
    public TreeMapData tablesforDashboard(){
        TreeMapData data = new TreeMapData();
        data.setData(paramAuditRepository.paramTablesTreeMap());
        data.setNumberupdates(paramAuditRepository.count());
        return data;
    }
    @Override
    public void checkReferencedRecursive(DeleteRequest deleteRequest, List<DeleteRequest> response) {
        Set<DeleteRequest> deleted= new HashSet<>(response);

        if (!deleted.contains(deleteRequest)){
            response.add(deleteRequest);
        List<ForeignKey> fks = allTablesWithColumns.getAllforeignKeys().stream().filter( fk -> fk.getReferencedTable().equals(deleteRequest.getTableName())).toList();
        ColumnInfo pk = primaryKeyDetails(deleteRequest.getTableName());
        String typePk = pk.getType();
        MapSqlParameterSource params =new MapSqlParameterSource();
        Object pkValue = convertToDataType(deleteRequest.getPrimaryKeyValue(),typePk);
        String sql="SELECT DISTINCT  * FROM "+deleteRequest.getTableName()+" WHERE "+pk.getName()+" = :pkValue ";
            params.addValue("pkValue",pkValue);
            //NOSONAR
            Map<String,Object> row = jdbcTemplate.queryForMap(sql,params);
        for (ForeignKey fk:fks){
            Object fkValue= row.get(fk.getReferencedColumn());

            params.addValue("fkValue",fkValue);
            //String sqlQuerry ="SELECT  "+primaryKeyDetails(fk.getFkTableName()).getName()+" FROM "+fk.getFkTableName()+" WHERE "+primaryKeyDetails(fk.getFkColumnName()) +" = :fkValue";
            String sqlQuerry ="SELECT  "+primaryKeyDetails(fk.getFkTableName()).getName()+" FROM "+fk.getFkTableName()+" WHERE "+fk.getFkColumnName() +" = :fkValue";
//NOSONAR
            List<Map<String,Object>>rowref = jdbcTemplate.queryForList(sqlQuerry,params);
            if (!rowref.isEmpty()) {
                List<String> occurences = rowref.stream().map(map -> map.get(primaryKeyDetails(fk.getFkTableName()).getName()).toString()).toList();
                for (String refId :occurences) {
                    DeleteRequest deleteRequest1 = new DeleteRequest(fk.getFkTableName(), refId);
                    response.add(deleteRequest1);
                }
                for (String primaryKeyValue : occurences) {
                    DeleteRequest childDeleteRequest = new DeleteRequest(fk.getFkTableName(), primaryKeyValue);
                    checkReferencedRecursive(childDeleteRequest, response);
                }
            }
        }}
    }
    @Override
    public Boolean checkunicity(String primaryKeyValue, String tableName){
        String pkType  =primaryKeyDetails(tableName).getType();
        Integer count =0;
        Object convertedValue = convertToDataType(primaryKeyValue,pkType);
        String sql = "SELECT COUNT(*) FROM "+tableName+"  WHERE "+primaryKeyDetails(tableName).getName()+" = :pkValue";
//      List<Object> params = new ArrayList<>();
//        params.add(primaryKeyDetails(tableName).getName());
//        params.add(convertedValue);

MapSqlParameterSource params = new MapSqlParameterSource();
params.addValue("pkValue",convertedValue);
//NOSONAR
        Integer countresult = jdbcTemplate.queryForObject(sql,params, Integer.class);
        if (countresult !=null){count=countresult;}
        return count.equals(0) ;
    }
    @Override
    public List<DeleteRequest> checkReferenced(DeleteRequest deleteRequest) {
        List<DeleteRequest> response = new ArrayList<>();
        List<ForeignKey> fks = allTablesWithColumns.getAllforeignKeys()
                .stream()
                .filter(fk -> fk.getReferencedTable().equals(deleteRequest.getTableName()))
                .toList();
        ColumnInfo pk = primaryKeyDetails(deleteRequest.getTableName());
        String typePk = pk.getType();
        Object pkValue = convertToDataType(deleteRequest.getPrimaryKeyValue(), typePk);

        String sql = "SELECT * FROM " + deleteRequest.getTableName() + " WHERE " + pk.getName() + " = :pkValue ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pkValue", pkValue);
//NOSONAR
        Map<String, Object> row = jdbcTemplate.queryForMap(sql, params);

        for (ForeignKey fk : fks) {
            Object fkValue = row.get(fk.getReferencedColumn());

            String querySql = "SELECT DISTINCT " + primaryKeyDetails(fk.getFkTableName()).getName() + " FROM " + fk.getFkTableName() + " WHERE " + fk.getFkColumnName() + " = :fkValue ";
            MapSqlParameterSource queryParams = new MapSqlParameterSource();
            queryParams.addValue("fkValue", fkValue);
//NOSONAR
            List<Map<String, Object>> rowRef = jdbcTemplate.queryForList(querySql, queryParams);

            if (!rowRef.isEmpty()) {
                List<String> occurrences = rowRef.stream()
                        .map(map -> map.get(primaryKeyDetails(fk.getFkTableName()).getName()).toString())
                        .collect(Collectors.toList());
                DeleteRequest deleteRequest1 = new DeleteRequest(fk.getFkTableName(), occurrences);
                response.add(deleteRequest1);
            }
        }
        return response;
    }

    @Override
    @Transactional
    public ResponseDto cancelDeleteRequest(String tableName, String primaryKeyValue) {
        List<DeleteRequest> response = new ArrayList<>();
        ResponseDto responseDto = new ResponseDto();
        boolean requestFound = false;
        DeleteRequest deleteRequest = new DeleteRequest(tableName,primaryKeyValue);
        response.add(deleteRequest);
        checkReferencedRecursive(deleteRequest, response);

        for (DeleteRequest del :response) {
            for (DeleteRequest delete : deleteRequests) {
                if (delete.getTableName().equals(del.getTableName()) &&
                        delete.getPrimaryKeyValue().equals(del.getPrimaryKeyValue())) {
                    int size = deleteRequests.size();
                    deleteRequests.remove(delete);
                    int newsize = deleteRequests.size();
                    if (size != newsize) {
                        responseDto.setSuccess("Deletion of " + primaryKeyValue + " cancelled successfully");
                    } else {
                        responseDto.setError("Deletion of " + primaryKeyValue + " not cancelled");
                    }
                    requestFound = true;
                    break;
                }
            }
        }
        if (!requestFound) {
            responseDto.setError("Request not found: " + primaryKeyValue);
        }
        return responseDto;
    }
    @Transactional
    @Scheduled(cron = "0 07 11 * * *")
    public void executeUpdate() {
        List<String> uniqueTableNames = new ArrayList<>();
        for (UpdateRequest updateRequest : updateRequests) {
            if (!uniqueTableNames.contains(updateRequest.getTableName())) {
                uniqueTableNames.add(updateRequest.getTableName());
            }
        }
        for (String uniqueTableName : uniqueTableNames) {
            List<UpdateRequest> tableUpdateRequests = getUpdateRequestByTable(uniqueTableName);
            Integer version = findMaxVersionByTableName(uniqueTableName) + 1;
            for (UpdateRequest tableupdateRequest : tableUpdateRequests) {
                ResponseDto  responseDto = updateInstance(tableupdateRequest, version);
                if (responseDto.getSuccess() != null) {
                    updateRequests.remove(tableupdateRequest);
                } else break;
            }
        }
    }
    @Transactional
    @Scheduled(cron = "0 07 11 * * *")
    public void executeDeletion () {
        List<String> uniqueTableNames = new ArrayList<>();
        for (DeleteRequest deleteRequest : deleteRequests) {
            if (!uniqueTableNames.contains(deleteRequest.getTableName())) {
                uniqueTableNames.add(deleteRequest.getTableName());
            }
        }
        for (String uniqueTableName : uniqueTableNames){
            List <DeleteRequest>tableDeletedRequests = getDeleteRequestByTable(uniqueTableName);
            Integer version=findMaxVersionByTableName(uniqueTableName)+1;
            for (DeleteRequest tableDeletedRequest:tableDeletedRequests){
                ResponseDto responseDto = deleteInstance(tableDeletedRequest,version);
                if (responseDto.getSuccess()!=null){
                    deleteRequests.remove(tableDeletedRequest);
                }else break;
            }}

    }
    @Override
    public ResponseDto deleteInstance(DeleteRequest deleteRequest, Integer version){
        ResponseDto responseDto = new ResponseDto();
        int rowsUpdated = 0;
        String inputValue = deleteRequest.getPrimaryKeyValue();
        String primaryKeyColumn = primaryKeyDetails(deleteRequest.getTableName()).getName();
        String primaryKeyColumnType = primaryKeyDetails(deleteRequest.getTableName()).getType();
        Object primaryKeyValue = convertToDataType(inputValue, primaryKeyColumnType);
        MapSqlParameterSource params =new MapSqlParameterSource();
        StringBuilder sqlQuery;
        sqlQuery = new StringBuilder();
        sqlQuery.append("UPDATE ")
                .append(deleteRequest.getTableName())
                .append(" SET active = false WHERE ")
                .append(primaryKeyColumn);
        sqlQuery.append(" = :pkValue");
        params.addValue("pkValue",primaryKeyValue);
        rowsUpdated =jdbcTemplate.update(sqlQuery.toString(),params );
        if (rowsUpdated > 0) {
            responseDto.setSuccess("Record updated successfully ");
            deleteRequests.remove(deleteRequest);
            ParamAudit paramAudit = ParamAudit.constructForDeletion(deleteRequest.getTableName(),inputValue,"DELETED",version,deleteRequest.getUssername());
            paramAuditRepository.save(paramAudit);
        }
        return responseDto;
    }
    @Override
    public Integer findMaxVersionByTableName(String tableName){
        return paramAuditRepository.findMaxVersionByTableName(tableName);
    }
    @Override
    public Object convertToDataType(String inputValue, String columnType) {
        Object convertedValue;
        convertedValue = switch (columnType.toLowerCase()) {
            case "int8" -> inputValue != null ? Long.parseLong(inputValue) : 0L;
            case "bigserial", "serial","serial4","serial8" -> inputValue != null ? Long.parseLong(inputValue) : null;
            case "int", "int4", "integer", "int2" ,"numeric","bigint"-> inputValue != null ? Integer.parseInt(inputValue) : 0;
            case "varchar", "text", "bpchar" -> inputValue;
            case "real","float4" ->inputValue != null ?  Float.parseFloat(inputValue):0f ;
            case "double","precision","float8" ->inputValue != null ?  Double.parseDouble(inputValue) :0d;
            case "bool" -> Boolean.parseBoolean(inputValue);
            case "bytea" -> inputValue != null ? Base64.getDecoder().decode(inputValue) : null;
            case "timestamptz","date","timetz" -> {
                ZonedDateTime zonedDateTime;
                if (inputValue == null || inputValue.isEmpty() || inputValue.equalsIgnoreCase("null") || inputValue.equalsIgnoreCase(UNDEFINED)) {zonedDateTime = ZonedDateTime.now();
                } else {
                    try {
                        zonedDateTime = ZonedDateTime.parse(inputValue);
                    } catch (Exception e) {
                        LocalDateTime dateTime = LocalDateTime.parse(inputValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        zonedDateTime = dateTime.atZone(ZoneId.systemDefault());}
                }
                yield Timestamp.from(zonedDateTime.toInstant());
            }default -> inputValue;
        };
        return convertedValue;
    }
@Override
    public ColumnInfo primaryKeyDetails(String tableName) {
        Optional <ColumnInfo> optionalPk= allTablesWithColumns.getAllTablesWithColumns().stream()
                .filter(table -> table.getName().equals(tableName)).findFirst()
                .map(TableInfo::getPk);
        return optionalPk.orElseThrow(() -> new IllegalStateException("Primary key not found for table: " + tableName));
    }
    @Override
    public List<ParamAudit> paramHistory(String tableName){
        return paramAuditRepository.findByTableName(tableName);
    }
}
