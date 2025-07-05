package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private Map<String, Object> mockStats;
    private List<Map<String, Object>> mockList;

    @BeforeEach
    void setUp() {
        mockStats = new HashMap<>();
        mockStats.put("totalFunds", 100);
        mockStats.put("totalStrategies", 50);

        mockList = Arrays.asList(
                new HashMap<String, Object>() {{ put("item1", "value1"); }},
                new HashMap<String, Object>() {{ put("item2", "value2"); }}
        );
    }

    @Test
    void getDashboardStats_success() {
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        Result<Map<String, Object>> result = dashboardController.getDashboardStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockStats, result.getData());
        verify(dashboardService, times(1)).getDashboardStats();
    }

    @Test
    void getDashboardStats_failure() {
        String errorMessage = "Service unavailable";
        when(dashboardService.getDashboardStats()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = dashboardController.getDashboardStats();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("获取统计数据失败：" + errorMessage));
        assertNull(result.getData());
        verify(dashboardService, times(1)).getDashboardStats();
    }

    @Test
    void getNetValueTrend_success() {
        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", Arrays.asList("2023-01-01", "2023-01-02"));
        trendData.put("values", Arrays.asList(1.0, 1.05));
        when(dashboardService.getNetValueTrend()).thenReturn(trendData);

        Result<Map<String, Object>> result = dashboardController.getNetValueTrend();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(trendData, result.getData());
        verify(dashboardService, times(1)).getNetValueTrend();
    }

    @Test
    void getNetValueTrend_failure() {
        String errorMessage = "Database error";
        when(dashboardService.getNetValueTrend()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = dashboardController.getNetValueTrend();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("获取净值走势数据失败：" + errorMessage));
        assertNull(result.getData());
        verify(dashboardService, times(1)).getNetValueTrend();
    }

    @Test
    void getAssetAllocation_success() {
        when(dashboardService.getAssetAllocation()).thenReturn(mockList);

        Result<List<Map<String, Object>>> result = dashboardController.getAssetAllocation();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockList, result.getData());
        verify(dashboardService, times(1)).getAssetAllocation();
    }

    @Test
    void getAssetAllocation_failure() {
        String errorMessage = "Data parsing error";
        when(dashboardService.getAssetAllocation()).thenThrow(new RuntimeException(errorMessage));

        Result<List<Map<String, Object>>> result = dashboardController.getAssetAllocation();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("获取资产配置数据失败：" + errorMessage));
        assertNull(result.getData());
        verify(dashboardService, times(1)).getAssetAllocation();
    }

    @Test
    void getReturnAnalysis_success() {
        Map<String, Object> returnData = new HashMap<>();
        returnData.put("ytd", 0.15);
        returnData.put("oneyear", 0.25);
        when(dashboardService.getReturnAnalysis()).thenReturn(returnData);

        Result<Map<String, Object>> result = dashboardController.getReturnAnalysis();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(returnData, result.getData());
        verify(dashboardService, times(1)).getReturnAnalysis();
    }

    @Test
    void getReturnAnalysis_failure() {
        String errorMessage = "Calculation error";
        when(dashboardService.getReturnAnalysis()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = dashboardController.getReturnAnalysis();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("获取收益分析数据失败：" + errorMessage));
        assertNull(result.getData());
        verify(dashboardService, times(1)).getReturnAnalysis();
    }

    @Test
    void getRiskMetrics_success() {
        Map<String, Object> riskData = new HashMap<>();
        riskData.put("sharpeRatio", 1.2);
        riskData.put("maxDrawdown", 0.08);
        when(dashboardService.getRiskMetrics()).thenReturn(riskData);

        Result<Map<String, Object>> result = dashboardController.getRiskMetrics();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(riskData, result.getData());
        verify(dashboardService, times(1)).getRiskMetrics();
    }

    @Test
    void getRiskMetrics_failure() {
        String errorMessage = "Risk data unavailable";
        when(dashboardService.getRiskMetrics()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = dashboardController.getRiskMetrics();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("获取风险指标数据失败：" + errorMessage));
        assertNull(result.getData());
        verify(dashboardService, times(1)).getRiskMetrics();
    }

    @Test
    void getRecentActivities_success() {
        when(dashboardService.getRecentActivities()).thenReturn(mockList);

        Result<List<Map<String, Object>>> result = dashboardController.getRecentActivities();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockList, result.getData());
        verify(dashboardService, times(1)).getRecentActivities();
    }

    @Test
    void getRecentActivities_failure() {
        String errorMessage = "Log retrieval failed";
        when(dashboardService.getRecentActivities()).thenThrow(new RuntimeException(errorMessage));

        Result<List<Map<String, Object>>> result = dashboardController.getRecentActivities();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("获取最新动态失败：" + errorMessage));
        assertNull(result.getData());
        verify(dashboardService, times(1)).getRecentActivities();
    }
}