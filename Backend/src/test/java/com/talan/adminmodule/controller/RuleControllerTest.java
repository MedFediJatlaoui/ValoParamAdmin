package com.talan.adminmodule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talan.adminmodule.dto.CategoryDto;
import com.talan.adminmodule.dto.DeleteRuleRequest;
import com.talan.adminmodule.dto.RuleDto;
import com.talan.adminmodule.dto.RuleUpdateDto;
import com.talan.adminmodule.entity.Category;
import com.talan.adminmodule.entity.Rule;
import com.talan.adminmodule.repository.*;
import com.talan.adminmodule.service.RuleService;
import com.talan.adminmodule.service.impl.RuleServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {RuleController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class RuleControllerTest {
    @Autowired
    private RuleController ruleController;

    @MockBean
    private RuleService ruleService;

    @Test
    void testSaveRule() {
        Category category = new Category();
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());

        Rule rule = new Rule();
        rule.setCategory(category);
        rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        rule.setCreatedBy(1);
        rule.setDescription("The characteristics of someone or something");
        rule.setId(1);
        rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
        rule.setLastModifiedBy(1);
        rule.setName("Name");
        rule.setRuleAttributes(new ArrayList<>());
        rule.setRuleModifications(new ArrayList<>());
        rule.setStatus("Status");
        RuleRepository ruleRepository = mock(RuleRepository.class);
        when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule);

        Category category2 = new Category();
        category2.setId(1);
        category2.setName("Name");
        category2.setRules(new ArrayList<>());

        Category category3 = new Category();
        category3.setId(1);
        category3.setName("Name");
        category3.setRules(new ArrayList<>());
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(category2);
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category3);
        RuleController ruleController = new RuleController(new RuleServiceImpl(ruleRepository,
                mock(AttributeRepository.class), categoryRepository, mock(RuleModificationRepository.class),
                mock(RuleAttributeRepository.class), mock(RuleUsageRepository.class)));
        RuleDto.RuleDtoBuilder builderResult = RuleDto.builder();
        RuleDto.RuleDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
        CategoryDto category4 = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
        RuleDto.RuleDtoBuilder categoryResult = attributeDtosResult.category(category4);
        RuleDto.RuleDtoBuilder idResult = categoryResult.createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
                .createdBy(1)
                .description("The characteristics of someone or something")
                .id(1);
        RuleDto ruleDto = idResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
                .lastModifiedBy(1)
                .name("Name")
                .status("Status")
                .build();

        ResponseEntity<RuleDto> actualSaveRuleResult = ruleController.saveRule(ruleDto);

        verify(categoryRepository).findByName(eq("Name"));
        verify(ruleRepository).save(isA(Rule.class));
        RuleDto body = actualSaveRuleResult.getBody();
        assertEquals("1970-01-01", body.getCreateDate().toLocalDate().toString());
        assertEquals("1970-01-01", body.getLastModified().toLocalDate().toString());
        CategoryDto category5 = body.getCategory();
        assertEquals("Name", category5.getName());
        assertEquals("Name", body.getName());
        assertEquals("Status", body.getStatus());
        assertEquals("The characteristics of someone or something", body.getDescription());
        assertNull(category5.getRuleCount());
        assertEquals(1, category5.getId().intValue());
        assertEquals(1, body.getCreatedBy().intValue());
        assertEquals(1, body.getId().intValue());
        assertEquals(1, body.getLastModifiedBy().intValue());
        assertEquals(201, actualSaveRuleResult.getStatusCodeValue());
        assertTrue(body.getAttributeDtos().isEmpty());
        assertTrue(actualSaveRuleResult.hasBody());
        assertTrue(actualSaveRuleResult.getHeaders().isEmpty());
    }

    @Test
    void testUpdateRule() {
        Category category = new Category();
        category.setId(1);
        category.setName("Name");
        category.setRules(new ArrayList<>());

        Rule rule = new Rule();
        rule.setCategory(category);
        rule.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        rule.setCreatedBy(1);
        rule.setDescription("The characteristics of someone or something");
        rule.setId(1);
        rule.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
        rule.setLastModifiedBy(1);
        rule.setName("Name");
        rule.setRuleAttributes(new ArrayList<>());
        rule.setRuleModifications(new ArrayList<>());
        rule.setStatus("Status");
        Optional<Rule> ofResult = Optional.of(rule);

        Category category2 = new Category();
        category2.setId(1);
        category2.setName("Name");
        category2.setRules(new ArrayList<>());

        Rule rule2 = new Rule();
        rule2.setCategory(category2);
        rule2.setCreateDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        rule2.setCreatedBy(1);
        rule2.setDescription("The characteristics of someone or something");
        rule2.setId(1);
        rule2.setLastModified(LocalDate.of(1970, 1, 1).atStartOfDay());
        rule2.setLastModifiedBy(1);
        rule2.setName("Name");
        rule2.setRuleAttributes(new ArrayList<>());
        rule2.setRuleModifications(new ArrayList<>());
        rule2.setStatus("Status");
        RuleRepository ruleRepository = mock(RuleRepository.class);
        when(ruleRepository.save(Mockito.<Rule>any())).thenReturn(rule2);
        when(ruleRepository.findById(Mockito.<Integer>any())).thenReturn(ofResult);
        RuleController ruleController = new RuleController(new RuleServiceImpl(ruleRepository,
                mock(AttributeRepository.class), mock(CategoryRepository.class), mock(RuleModificationRepository.class),
                mock(RuleAttributeRepository.class), mock(RuleUsageRepository.class)));
        RuleUpdateDto.RuleUpdateDtoBuilder builderResult = RuleUpdateDto.builder();
        RuleUpdateDto.RuleUpdateDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
        CategoryDto category3 = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
        RuleUpdateDto.RuleUpdateDtoBuilder categoryResult = attributeDtosResult.category(category3);
        RuleUpdateDto.RuleUpdateDtoBuilder imageUrlResult = categoryResult
                .createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
                .createdBy(1)
                .description("The characteristics of someone or something")
                .id(1)
                .imageUrl("https://example.org/example");
        RuleUpdateDto updatedRuleDto = imageUrlResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
                .lastModifiedBy(1)
                .name("Name")
                .status("Status")
                .build();

        ResponseEntity<Void> actualUpdateRuleResult = ruleController.updateRule("Jan 1, 2020 9:00am GMT+0100", 1,
                "Mod Description", updatedRuleDto);

        verify(ruleRepository).findById(eq(1));
        verify(ruleRepository).save(isA(Rule.class));
        assertNull(actualUpdateRuleResult.getBody());
        assertEquals(202, actualUpdateRuleResult.getStatusCodeValue());
        assertTrue(actualUpdateRuleResult.getHeaders().isEmpty());
    }

    @Test
    void testGetModificationsByRuleId() throws Exception {
        when(ruleService.getModificationsByRuleId(Mockito.<Integer>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/rules/{id}/history", 1);

        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testFindAllRules() {
        RuleRepository ruleRepository = mock(RuleRepository.class);
        when(ruleRepository.findAll(Mockito.<Pageable>any())).thenReturn(new PageImpl<>(new ArrayList<>()));

        ResponseEntity<Page<RuleDto>> actualFindAllRulesResult = (new RuleController(new RuleServiceImpl(ruleRepository,
                mock(AttributeRepository.class), mock(CategoryRepository.class), mock(RuleModificationRepository.class),
                mock(RuleAttributeRepository.class), mock(RuleUsageRepository.class)))).findAllRules(1, 3);

        verify(ruleRepository).findAll(isA(Pageable.class));
        assertEquals(200, actualFindAllRulesResult.getStatusCodeValue());
        assertTrue(actualFindAllRulesResult.getBody().toList().isEmpty());
        assertTrue(actualFindAllRulesResult.hasBody());
        assertTrue(actualFindAllRulesResult.getHeaders().isEmpty());
    }

    @Test
    void testSearchRules() {
        RuleRepository ruleRepository = mock(RuleRepository.class);
        when(ruleRepository.search(Mockito.<String>any(), Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        ResponseEntity<Page<RuleDto>> actualSearchRulesResult = (new RuleController(new RuleServiceImpl(ruleRepository,
                mock(AttributeRepository.class), mock(CategoryRepository.class), mock(RuleModificationRepository.class),
                mock(RuleAttributeRepository.class), mock(RuleUsageRepository.class)))).searchRules(1, 3, "Query");

        verify(ruleRepository).search(eq("Query"), isA(Pageable.class));
        assertEquals(200, actualSearchRulesResult.getStatusCodeValue());
        assertTrue(actualSearchRulesResult.getBody().toList().isEmpty());
        assertTrue(actualSearchRulesResult.hasBody());
        assertTrue(actualSearchRulesResult.getHeaders().isEmpty());
    }

    @Test
    void testGetTop5UsedRulesForLast18Days() throws Exception {
        when(ruleService.getTop5UsedRulesForLast18Days()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/rules/top-used-rules");

        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testCreateRuleUsage() throws Exception {
        doNothing().when(ruleService).createRuleUsage(anyInt());
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/api/rules/rule-usage");
        MockHttpServletRequestBuilder requestBuilder = postResult.param("ruleId", String.valueOf(1));

        MockMvcBuilders.standaloneSetup(ruleController).build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetTotalRulesCount() throws Exception {
        when(ruleService.getTotalRulesCount()).thenReturn(3L);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/rules/total-rules-count");

        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("3"));
    }

    @Test
    void testGetTotalRuleUsages() throws Exception {
        when(ruleService.getTotalRuleUsages()).thenReturn(1L);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/rules/total-rule-usages");

        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    @Test
    void testDeleteRule() throws Exception {
        doNothing().when(ruleService).queueDelete(Mockito.<Integer>any(), Mockito.<String>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.delete("/api/rules/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        DeleteRuleRequest buildResult = DeleteRuleRequest.builder()
                .id(1)
                .imageUrl("https://example.org/example")
                .modifiedBy("Jan 1, 2020 9:00am GMT+0100")
                .build();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(buildResult));

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(ruleController).build().perform(requestBuilder);

        actualPerformResult.andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    void testFindRuleById() throws Exception {
        RuleDto.RuleDtoBuilder builderResult = RuleDto.builder();
        RuleDto.RuleDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
        CategoryDto category = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
        RuleDto.RuleDtoBuilder categoryResult = attributeDtosResult.category(category);
        RuleDto.RuleDtoBuilder idResult = categoryResult.createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
                .createdBy(1)
                .description("The characteristics of someone or something")
                .id(1);
        RuleDto buildResult = idResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
                .lastModifiedBy(1)
                .name("Name")
                .status("Status")
                .build();
        when(ruleService.findById(Mockito.<Integer>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/rules/{id}", 1);

        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":1,\"name\":\"Name\",\"description\":\"The characteristics of someone or something\",\"category\":{\"id\":1"
                                        + ",\"name\":\"Name\",\"ruleCount\":3},\"status\":\"Status\",\"createDate\":[1970,1,1,0,0],\"lastModified\":[1970,1,1"
                                        + ",0,0],\"createdBy\":1,\"lastModifiedBy\":1,\"attributeDtos\":[]}"));
    }

    @Test
    void testGetAllUpdates() throws Exception {
        when(ruleService.getAllModifications()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/rules/updates");

        MockMvcBuilders.standaloneSetup(ruleController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    @Disabled("TODO: Complete this test")
    void testSaveRule2() throws Exception {
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/api/rules")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        RuleDto.RuleDtoBuilder builderResult = RuleDto.builder();
        RuleDto.RuleDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
        CategoryDto category = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
        RuleDto.RuleDtoBuilder categoryResult = attributeDtosResult.category(category);
        RuleDto.RuleDtoBuilder idResult = categoryResult.createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
                .createdBy(1)
                .description("The characteristics of someone or something")
                .id(1);
        RuleDto buildResult = idResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
                .lastModifiedBy(1)
                .name("Name")
                .status("Status")
                .build();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(buildResult));

        MockMvcBuilders.standaloneSetup(ruleController).build().perform(requestBuilder);
    }

    @Test
    @Disabled("TODO: Complete this test")
    void testUpdateRule2() throws Exception {
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                .post("/api/rules/{modDescription}/{id}/{modifiedBy}", "Mod Description", 1, "Jan 1, 2020 9:00am GMT+0100")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        RuleUpdateDto.RuleUpdateDtoBuilder builderResult = RuleUpdateDto.builder();
        RuleUpdateDto.RuleUpdateDtoBuilder attributeDtosResult = builderResult.attributeDtos(new ArrayList<>());
        CategoryDto category = CategoryDto.builder().id(1).name("Name").ruleCount(3).build();
        RuleUpdateDto.RuleUpdateDtoBuilder categoryResult = attributeDtosResult.category(category);
        RuleUpdateDto.RuleUpdateDtoBuilder imageUrlResult = categoryResult
                .createDate(LocalDate.of(1970, 1, 1).atStartOfDay())
                .createdBy(1)
                .description("The characteristics of someone or something")
                .id(1)
                .imageUrl("https://example.org/example");
        RuleUpdateDto buildResult = imageUrlResult.lastModified(LocalDate.of(1970, 1, 1).atStartOfDay())
                .lastModifiedBy(1)
                .name("Name")
                .status("Status")
                .build();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(buildResult));

        MockMvcBuilders.standaloneSetup(ruleController).build().perform(requestBuilder);
    }
}




