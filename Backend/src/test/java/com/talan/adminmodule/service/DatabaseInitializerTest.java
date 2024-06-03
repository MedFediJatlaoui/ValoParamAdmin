package com.talan.adminmodule.service;
import com.talan.adminmodule.config.DatabaseInitializer;
import com.talan.adminmodule.dto.ColumnInfo;
import com.talan.adminmodule.dto.TableInfo;
import com.talan.adminmodule.dto.TablesWithColumns;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
class DatabaseInitializerTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Test
    void testRetrieveAllTablesWithColumns() {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(dataSource, jdbcTemplate);
        TablesWithColumns result = databaseInitializer.retrieveAllTablesWithColumns();

         assertFalse(result.getAllTablesWithColumns().isEmpty());

        for (TableInfo tableInfo : result.getAllTablesWithColumns()) {
            assertNotNull(tableInfo.getName());
            assertFalse(tableInfo.getColumns().isEmpty());
            ColumnInfo primaryKey = tableInfo.getPk();
            assertNotNull(primaryKey);
            assertNotNull(primaryKey.getName());
            assertNotNull(primaryKey.getType());
            for (ColumnInfo column : tableInfo.getColumns()) {
                assertNotNull(column.getName());
                assertNotNull(column.getType());
            }
        }
    }
}
