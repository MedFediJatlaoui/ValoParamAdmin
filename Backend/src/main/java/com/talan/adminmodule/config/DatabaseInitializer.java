package com.talan.adminmodule.config;

import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.service.RuleService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseInitializer {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private static final Logger log =  LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired

    public DatabaseInitializer(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate=jdbcTemplate;
    }
    @Getter
    private TablesWithColumns allTablesWithColumns =new TablesWithColumns();
    @PostConstruct
    private void initialize() {
         allTablesWithColumns = retrieveAllTablesWithColumns();
        for (TableInfo tableInfo : allTablesWithColumns.getAllTablesWithColumns()) {
            String active="active";
            if (tableInfo.getColumns().stream().noneMatch(columnInfo -> columnInfo.getName().equals(active))){
                jdbcTemplate.getJdbcOperations().execute("ALTER TABLE " + tableInfo.getName() + " ADD COLUMN "+ active +" BOOLEAN DEFAULT TRUE");
            }
        }

         allTablesWithColumns =retrieveAllTablesWithColumns();
    }

    public TablesWithColumns retrieveAllTablesWithColumns() {
        List<TableInfo> tablesWithColumnsList = new ArrayList<>();
        List<ForeignKey> foreignKeyList = new ArrayList<>();
        TablesWithColumns tablesWithColumns = new TablesWithColumns();
        List<String> tablesData = new ArrayList<>();
        tablesData.add("_user");
        tablesData.add("attribute");
        tablesData.add("param_audit");
        tablesData.add("rule");
        tablesData.add("rule_attribute");
        tablesData.add("rule_modification");
        tablesData.add("dashboard");
        tablesData.add("_category");
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, "public", null, new String[]{"TABLE"});
            while (tables.next()) {
                TableInfo tableInfo = new TableInfo();

                if (!tablesData.contains(tables.getString("TABLE_NAME"))){
                String tableName = tables.getString("TABLE_NAME");

                    List<ColumnInfo> columns = new ArrayList<>();
                ResultSet tableColumns = metaData.getColumns(null, "public", tableName, null);
                while (tableColumns.next()) {
                    String columnName = tableColumns.getString("COLUMN_NAME");
                    String columnType = tableColumns.getString("TYPE_NAME");
                    String isNullable = tableColumns.getString("IS_NULLABLE");
                    String columnSize = tableColumns.getString("COLUMN_SIZE");
                    String isAutoIncrement = tableColumns.getString("IS_AUTOINCREMENT");
                    columns.add(new ColumnInfo(columnName, columnType,isNullable,columnSize,isAutoIncrement));
                }


                ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
                   ColumnInfo primaryKeyColumn = new ColumnInfo();
                if (primaryKeys.next()) {
                    String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                    String primaryKeyColumnType = columns.stream()
                            .filter(column -> column.getName().equals(primaryKeyColumnName))
                            .findFirst()
                            .map(ColumnInfo::getType)
                            .orElse("");
                    primaryKeyColumn.setName(primaryKeyColumnName);
                    primaryKeyColumn.setType(primaryKeyColumnType);
                }

                        long totalRows = getTotalRowsCount(tableName);
                    ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
                    List<String> fks = new ArrayList<>();
                    while(foreignKeys.next()){
                        String fkTableName = foreignKeys.getString("FKTABLE_NAME");
                        String referencedTable = foreignKeys.getString("PKTABLE_NAME");
                        String referencedColumn = foreignKeys.getString("PKCOLUMN_NAME");
                        String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                        ForeignKey foreignKey = new ForeignKey(fkTableName,fkColumnName,referencedTable,referencedColumn);

                        if (tableName.equals(fkTableName)) {
                            fks.add(fkColumnName);
                        }
                        foreignKeyList.add(foreignKey);

                    }
                tableInfo.setForeignKeys(fks);
                tableInfo.setName(tableName);
                tableInfo.setColumns(columns);
                tableInfo.setPk(primaryKeyColumn);
                tableInfo.setTotalRows(totalRows);
                tablesWithColumnsList.add(tableInfo);
            }}
        } catch (SQLException e) {
            log.error("An error occurred: {}", e.getMessage());
        }

        tablesWithColumns.setAllTablesWithColumns(tablesWithColumnsList);
        tablesWithColumns.setNumberTables((long)tablesWithColumnsList.size());
        tablesWithColumns.setAllforeignKeys(foreignKeyList);
        return tablesWithColumns;
    }

    private int getTotalRowsCount(String tableName)  {
        int totalRows = 0;

        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
        Integer result = jdbcTemplate.getJdbcOperations().queryForObject(sql,Integer.class);
        if (result!=null){
            totalRows=result;
        }
        return totalRows;
    }

}
