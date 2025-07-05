package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.FactorInfo;
import com.advisor.service.FactorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FactorControllerTest {

    @Mock
    private FactorService factorService;

    @InjectMocks
    private FactorController factorController;

    private FactorInfo mockFactor;
    private PageResult<FactorInfo> mockPageResult;
    private Map<String, Object> mockAnalysisResult;

    @BeforeEach
    void setUp() {
        mockFactor = new FactorInfo();
        mockFactor.setId(1L);
        mockFactor.setFactorCode("MOMENTUM_1M");
        mockFactor.setFactorName("1个月动量");
        mockFactor.setFactorType("风格因子");
        mockFactor.setCategory("动量");
        mockFactor.setDescription("过去1个月的收益率");
        mockFactor.setStatus(1);
        mockFactor.setCreatedAt(LocalDateTime.now());
        mockFactor.setUpdatedAt(LocalDateTime.now());

        mockPageResult = PageResult.of(
                Collections.singletonList(mockFactor),
                1L, 1, 20
        );

        mockAnalysisResult = new HashMap<>();
        mockAnalysisResult.put("status", "completed");
        mockAnalysisResult.put("data", "some analysis data");
    }

    @Test
    void getFactorList_success() {
        String factorType = "风格因子";
        String category = "动量";
        String keyword = "动量";
        Integer current = 1;
        Integer size = 20;

        when(factorService.getFactorList(factorType, category, keyword, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<FactorInfo>> result = factorController.getFactorList(factorType, category, keyword, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(factorService, times(1)).getFactorList(factorType, category, keyword, current, size);
    }

    @Test
    void getFactorList_failure() {
        String errorMessage = "Database query failed";
        when(factorService.getFactorList(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<FactorInfo>> result = factorController.getFactorList(null, null, null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).getFactorList(any(), any(), any(), any(), any());
    }

    @Test
    void getFactorById_success() {
        Long id = 1L;
        when(factorService.getFactorById(id)).thenReturn(mockFactor);

        Result<FactorInfo> result = factorController.getFactorById(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockFactor, result.getData());
        verify(factorService, times(1)).getFactorById(id);
    }

    @Test
    void getFactorById_failure() {
        Long id = 2L;
        String errorMessage = "Factor not found";
        when(factorService.getFactorById(id)).thenThrow(new RuntimeException(errorMessage));

        Result<FactorInfo> result = factorController.getFactorById(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).getFactorById(id);
    }

    @Test
    void getFactorByCode_success() {
        String factorCode = "MOMENTUM_1M";
        when(factorService.getFactorByCode(factorCode)).thenReturn(mockFactor);

        Result<FactorInfo> result = factorController.getFactorByCode(factorCode);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockFactor, result.getData());
        verify(factorService, times(1)).getFactorByCode(factorCode);
    }

    @Test
    void getFactorByCode_failure() {
        String factorCode = "NON_EXISTENT";
        String errorMessage = "Factor not found by code";
        when(factorService.getFactorByCode(factorCode)).thenThrow(new RuntimeException(errorMessage));

        Result<FactorInfo> result = factorController.getFactorByCode(factorCode);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).getFactorByCode(factorCode);
    }

    @Test
    void createFactor_success() {
        FactorInfo newFactor = new FactorInfo();
        newFactor.setFactorCode("NEW_FACTOR");
        newFactor.setFactorName("新因子");
        Long createdId = 2L;

        when(factorService.createFactor(newFactor)).thenReturn(createdId);

        Result<Long> result = factorController.createFactor(newFactor);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("创建因子成功", result.getMessage());
        assertEquals(createdId, result.getData());
        verify(factorService, times(1)).createFactor(newFactor);
    }

    @Test
    void createFactor_failure() {
        FactorInfo newFactor = new FactorInfo();
        newFactor.setFactorCode("EXISTING_FACTOR");
        String errorMessage = "Factor code already exists";
        when(factorService.createFactor(newFactor)).thenThrow(new RuntimeException(errorMessage));

        Result<Long> result = factorController.createFactor(newFactor);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).createFactor(newFactor);
    }

    @Test
    void updateFactor_success() {
        Long id = 1L;
        FactorInfo updatedFactor = new FactorInfo();
        updatedFactor.setId(id);
        updatedFactor.setFactorName("Updated Name");

        doNothing().when(factorService).updateFactor(any(FactorInfo.class));

        Result<Void> result = factorController.updateFactor(id, updatedFactor);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("更新因子成功", result.getMessage());
        assertNull(result.getData()); // Void result should have null data
        verify(factorService, times(1)).updateFactor(updatedFactor);
        assertEquals(id, updatedFactor.getId()); // Ensure ID is set correctly
    }

    @Test
    void updateFactor_failure() {
        Long id = 1L;
        FactorInfo updatedFactor = new FactorInfo();
        updatedFactor.setId(id);
        String errorMessage = "Update failed due to validation";
        doThrow(new RuntimeException(errorMessage)).when(factorService).updateFactor(any(FactorInfo.class));

        Result<Void> result = factorController.updateFactor(id, updatedFactor);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).updateFactor(updatedFactor);
    }

    @Test
    void deleteFactor_success() {
        Long id = 1L;
        doNothing().when(factorService).deleteFactor(id);

        Result<Void> result = factorController.deleteFactor(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("删除因子成功", result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).deleteFactor(id);
    }

    @Test
    void deleteFactor_failure() {
        Long id = 99L;
        String errorMessage = "Factor does not exist";
        doThrow(new RuntimeException(errorMessage)).when(factorService).deleteFactor(id);

        Result<Void> result = factorController.deleteFactor(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).deleteFactor(id);
    }

    @Test
    void getFactorOptions_success() {
        List<String> factorTypes = Arrays.asList("风格因子", "基本面因子");
        List<String> categories = Arrays.asList("动量", "价值");
        Map<String, List<String>> expectedOptions = Map.of(
                "factorTypes", factorTypes,
                "categories", categories
        );

        when(factorService.getAllFactorTypes()).thenReturn(factorTypes);
        when(factorService.getAllCategories()).thenReturn(categories);

        Result<Map<String, List<String>>> result = factorController.getFactorOptions();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(expectedOptions, result.getData());
        verify(factorService, times(1)).getAllFactorTypes();
        verify(factorService, times(1)).getAllCategories();
    }

    @Test
    void getFactorOptions_failure() {
        String errorMessage = "Failed to retrieve options";
        when(factorService.getAllFactorTypes()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, List<String>>> result = factorController.getFactorOptions();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).getAllFactorTypes();
        verify(factorService, never()).getAllCategories(); // Ensure the second service call is not made if the first fails
    }

    @Test
    void runFactorAnalysis_success() {
        Map<String, Object> analysisParams = new HashMap<>();
        analysisParams.put("type", "correlation");
        analysisParams.put("factors", Arrays.asList("MOMENTUM_1M", "VOLATILITY"));

        when(factorService.runFactorAnalysis(analysisParams)).thenReturn(mockAnalysisResult);

        Result<Map<String, Object>> result = factorController.runFactorAnalysis(analysisParams);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("因子分析执行成功", result.getMessage());
        assertEquals(mockAnalysisResult, result.getData());
        verify(factorService, times(1)).runFactorAnalysis(analysisParams);
    }

    @Test
    void runFactorAnalysis_failure() {
        Map<String, Object> analysisParams = new HashMap<>();
        String errorMessage = "Analysis execution failed";
        when(factorService.runFactorAnalysis(analysisParams)).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = factorController.runFactorAnalysis(analysisParams);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).runFactorAnalysis(analysisParams);
    }

    @Test
    void getAnalysisResult_success() {
        String analysisId = "ANALYSIS_123";
        when(factorService.getAnalysisResult(analysisId)).thenReturn(mockAnalysisResult);

        Result<Map<String, Object>> result = factorController.getAnalysisResult(analysisId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockAnalysisResult, result.getData());
        verify(factorService, times(1)).getAnalysisResult(analysisId);
    }

    @Test
    void getAnalysisResult_failure() {
        String analysisId = "NON_EXISTENT_ID";
        String errorMessage = "Analysis result not found";
        when(factorService.getAnalysisResult(analysisId)).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = factorController.getAnalysisResult(analysisId);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(factorService, times(1)).getAnalysisResult(analysisId);
    }
}