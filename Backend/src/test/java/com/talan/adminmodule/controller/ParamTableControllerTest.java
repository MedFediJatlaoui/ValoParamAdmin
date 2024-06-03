package com.talan.adminmodule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.entity.ParamAudit;
import com.talan.adminmodule.service.impl.ParamTableServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ParamTableController.class})
@WebMvcTest(ParamTableController.class)
class ParamTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParamTableServiceImpl tableService;
    @Autowired
    ParamTableController paramTableController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testRetrieveAllTablesAndColumns() throws Exception {
        TablesWithColumns tablesWithColumns = new TablesWithColumns();
        tablesWithColumns.setAllTablesWithColumns(Collections.emptyList());

        when(tableService.retrieveAllTablesWithFilteredColumns(3, 0))
                .thenReturn(tablesWithColumns);

        mockMvc.perform(get("/api/tables/3/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allTablesWithColumns").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testGetDataFromTable() throws Exception {
        DataFromTable dataFromTable = new DataFromTable();
        dataFromTable.setData(Collections.emptyList());

        when(tableService.getDataFromTable(ArgumentMatchers.anyString(), ArgumentMatchers.any(TableDataRequest.class)))
                .thenReturn(dataFromTable);

        mockMvc.perform(get("/api/tables/actor")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testCheckUnicity() throws Exception {
        when(tableService.checkunicity("1", "actor"))
                .thenReturn(true);

        mockMvc.perform(get("/api/tables/unicity/actor/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testDataForDashboard() throws Exception {
        TreeMapData treeMapData = new TreeMapData();
        treeMapData.setNumberupdates(1);
        treeMapData.setData(Collections.emptyList());

        when(tableService.tablesforDashboard()).thenReturn(treeMapData);

        mockMvc.perform(get("/api/tables/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberupdates").value(1));
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testCheckReferences() throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest("country", "1");
        deleteRequest.setTableName("address");
        deleteRequest.setOccurences(Collections.emptyList());

        when(tableService.checkReferenced(ArgumentMatchers.any(DeleteRequest.class)))
                .thenReturn(Collections.singletonList(deleteRequest));

        mockMvc.perform(get("/api/tables/country/references/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableName").value("address"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testDeleteRecord() throws Exception {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setSuccess("INSTANCE ADDED TO DELETE 8 AM");

        when(tableService.addDeleteRequest(ArgumentMatchers.any(DeleteRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/tables/language/delete/lan3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("INSTANCE ADDED TO DELETE 8 AM"));
    }
    @Test
    void testDeleteCascade() throws Exception {
        // Arrange
        when(tableService.deletecascade(Mockito.<DeleteRequest>any()))
                .thenReturn(new ResponseDto("Success", "An error occurred"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/tables/{tableName}/cascade/{primaryKeyValue}", "Table Name", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(paramTableController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"success\":\"Success\",\"error\":\"An error occurred\"}"));
    }

    /**
     * Method under test: {@link ParamTableController#deleteCascade(String, String)}
     */
    @Test
    void testDeleteCascade2() throws Exception {
        when(tableService.deletecascade(Mockito.<DeleteRequest>any()))
                .thenReturn(new ResponseDto("Success", "An error occurred"));
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(paramTableController)
                .build()
                .perform(requestBuilder);

        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})

    void testParamHistory() throws Exception {
        ParamAudit paramAudit = new ParamAudit();
        paramAudit.setTableName("language");

        when(tableService.paramHistory("language"))
                .thenReturn(Collections.singletonList(paramAudit));

        mockMvc.perform(get("/api/tables/language/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableName").value("language"));
    }
    @Test
    void testCanceldeleterequest() throws Exception {
        // Arrange
        when(tableService.cancelDeleteRequest(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(new ResponseDto("Success", "An error occurred"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/tables/{tableName}/canceldeletion/{primaryKeyValue}", "Table Name", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(paramTableController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"success\":\"Success\",\"error\":\"An error occurred\"}"));
    }

    @Test
    void testCanceldeleterequest2() throws Exception {
        // Arrange
        when(tableService.cancelDeleteRequest(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(new ResponseDto("Success", "An error occurred"));
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(paramTableController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
