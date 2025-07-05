package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.FundInfo;
import com.advisor.entity.FundPerformance;
import com.advisor.service.FundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundControllerTest {

    @Mock
    private FundService fundService;

    @InjectMocks
    private FundController fundController;

    private FundInfo mockFundInfo;
    private FundPerformance mockFundPerformance;
    private PageResult<FundInfo> mockPageResult;
    private Map<String, Object> mockFundDetail;
    private List<Map<String, Object>> mockNetValues;

    @BeforeEach
    void setUp() {
        mockFundInfo = new FundInfo();
        mockFundInfo.setId(1L);
        mockFundInfo.setFundCode("001234");
        mockFundInfo.setFundName("测试基金");
        mockFundInfo.setFundType("股票型");
        mockFundInfo.setFundCompany("测试基金公司");
        mockFundInfo.setFundManager("张三");
        mockFundInfo.setEstablishDate(LocalDate.of(2010, 1, 1));
        mockFundInfo.setUnitNetValue(BigDecimal.valueOf(1.50));
        mockFundInfo.setAccumulatedNetValue(BigDecimal.valueOf(2.00));
        mockFundInfo.setRiskLevel(3);
        mockFundInfo.setMinInvestment(BigDecimal.valueOf(1000));
        mockFundInfo.setManagementFee(BigDecimal.valueOf(0.01));
        mockFundInfo.setStatus(1);
        mockFundInfo.setDescription("这是一个测试基金");
        mockFundInfo.setCreatedAt(LocalDateTime.now());
        mockFundInfo.setUpdatedAt(LocalDateTime.now());

        mockFundPerformance = new FundPerformance();
        mockFundPerformance.setFundId(1L);
        mockFundPerformance.setOneYearReturn(BigDecimal.valueOf(0.15));
        mockFundPerformance.setSharpeRatio(BigDecimal.valueOf(1.2));

        mockPageResult = PageResult.of(Collections.singletonList(mockFundInfo), 1L, 1, 20);

        mockFundDetail = new HashMap<>();
        mockFundDetail.put("fundInfo", mockFundInfo);
        mockFundDetail.put("performance", mockFundPerformance);

        mockNetValues = Arrays.asList(
                Map.of("tradeDate", "2023-01-01", "unitNetValue", 1.0),
                Map.of("tradeDate", "2023-01-02", "unitNetValue", 1.01)
        );
    }

    @Test
    void getFundList_success() {
        String fundType = "股票型";
        String fundCompany = "测试基金公司";
        String fundManager = "张三";
        Integer riskLevel = 3;
        BigDecimal minInvestment = BigDecimal.valueOf(1000);
        String keyword = "测试";
        Integer current = 1;
        Integer size = 20;

        when(fundService.getFundList(fundType, fundCompany, fundManager, riskLevel, minInvestment, keyword, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<FundInfo>> result = fundController.getFundList(
                fundType, fundCompany, fundManager, riskLevel, minInvestment, keyword, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(fundService, times(1)).getFundList(
                fundType, fundCompany, fundManager, riskLevel, minInvestment, keyword, current, size);
    }

    @Test
    void getFundList_failure() {
        String errorMessage = "Failed to retrieve fund list";
        when(fundService.getFundList(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<FundInfo>> result = fundController.getFundList(null, null, null, null, null, null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getFundList(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getFundOptions_success() {
        List<String> fundTypes = Arrays.asList("股票型", "债券型");
        List<String> fundCompanies = Arrays.asList("公司A", "公司B");
        Map<String, List<String>> expectedOptions = Map.of(
                "fundTypes", fundTypes,
                "fundCompanies", fundCompanies
        );

        when(fundService.getAllFundTypes()).thenReturn(fundTypes);
        when(fundService.getAllFundCompanies()).thenReturn(fundCompanies);

        Result<Map<String, List<String>>> result = fundController.getFundOptions();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(expectedOptions, result.getData());
        verify(fundService, times(1)).getAllFundTypes();
        verify(fundService, times(1)).getAllFundCompanies();
    }

    @Test
    void getFundOptions_failure() {
        String errorMessage = "Failed to get options";
        when(fundService.getAllFundTypes()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, List<String>>> result = fundController.getFundOptions();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getAllFundTypes();
        verify(fundService, never()).getAllFundCompanies();
    }

    @Test
    void getFundByCode_success() {
        String fundCode = "001234";
        when(fundService.getFundByCode(fundCode)).thenReturn(mockFundInfo);

        Result<FundInfo> result = fundController.getFundByCode(fundCode);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockFundInfo, result.getData());
        verify(fundService, times(1)).getFundByCode(fundCode);
    }

    @Test
    void getFundByCode_failure() {
        String fundCode = "NON_EXISTENT";
        String errorMessage = "Fund not found by code";
        when(fundService.getFundByCode(fundCode)).thenThrow(new RuntimeException(errorMessage));

        Result<FundInfo> result = fundController.getFundByCode(fundCode);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getFundByCode(fundCode);
    }

    @Test
    void getFundCompanies_success() {
        List<String> companies = Arrays.asList("公司A", "公司B");
        when(fundService.getAllFundCompanies()).thenReturn(companies);

        Result<List<String>> result = fundController.getFundCompanies();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(companies, result.getData());
        verify(fundService, times(1)).getAllFundCompanies();
    }

    @Test
    void getFundCompanies_failure() {
        String errorMessage = "Error fetching companies";
        when(fundService.getAllFundCompanies()).thenThrow(new RuntimeException(errorMessage));

        Result<List<String>> result = fundController.getFundCompanies();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getAllFundCompanies();
    }

    @Test
    void getFundManagers_success() {
        List<String> managers = Arrays.asList("王五", "赵六");
        when(fundService.getAllFundManagers()).thenReturn(managers);

        Result<List<String>> result = fundController.getFundManagers();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(managers, result.getData());
        verify(fundService, times(1)).getAllFundManagers();
    }

    @Test
    void getFundManagers_failure() {
        String errorMessage = "Error fetching managers";
        when(fundService.getAllFundManagers()).thenThrow(new RuntimeException(errorMessage));

        Result<List<String>> result = fundController.getFundManagers();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getAllFundManagers();
    }

    @Test
    void getFundById_success() {
        Long id = 1L;
        when(fundService.getFundById(id)).thenReturn(mockFundInfo);

        Result<FundInfo> result = fundController.getFundById(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockFundInfo, result.getData());
        verify(fundService, times(1)).getFundById(id);
    }

    @Test
    void getFundById_failure() {
        Long id = 99L;
        String errorMessage = "Fund not found by ID";
        when(fundService.getFundById(id)).thenThrow(new RuntimeException(errorMessage));

        Result<FundInfo> result = fundController.getFundById(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getFundById(id);
    }

    @Test
    void getFundPerformance_success() {
        Long id = 1L;
        when(fundService.getFundPerformance(id)).thenReturn(mockFundPerformance);

        Result<FundPerformance> result = fundController.getFundPerformance(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockFundPerformance, result.getData());
        verify(fundService, times(1)).getFundPerformance(id);
    }

    @Test
    void getFundPerformance_failure() {
        Long id = 99L;
        String errorMessage = "Performance data not available";
        when(fundService.getFundPerformance(id)).thenThrow(new RuntimeException(errorMessage));

        Result<FundPerformance> result = fundController.getFundPerformance(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getFundPerformance(id);
    }

    @Test
    void getFundDetail_success() {
        Long id = 1L;
        when(fundService.getFundDetail(id)).thenReturn(mockFundDetail);

        Result<Map<String, Object>> result = fundController.getFundDetail(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockFundDetail, result.getData());
        verify(fundService, times(1)).getFundDetail(id);
    }

    @Test
    void getFundDetail_failure() {
        Long id = 99L;
        String errorMessage = "Failed to get fund detail";
        when(fundService.getFundDetail(id)).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, Object>> result = fundController.getFundDetail(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getFundDetail(id);
    }

    @Test
    void getFundNetValue_success() {
        Long id = 1L;
        String period = "1Y";
        when(fundService.getFundNetValue(id, period)).thenReturn(mockNetValues);

        Result<List<Map<String, Object>>> result = fundController.getFundNetValue(id, period);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockNetValues, result.getData());
        verify(fundService, times(1)).getFundNetValue(id, period);
    }

    @Test
    void getFundNetValue_failure() {
        Long id = 99L;
        String period = "1Y";
        String errorMessage = "Net value data error";
        when(fundService.getFundNetValue(id, period)).thenThrow(new RuntimeException(errorMessage));

        Result<List<Map<String, Object>>> result = fundController.getFundNetValue(id, period);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).getFundNetValue(id, period);
    }

    @Test
    void createFund_success() {
        FundInfo newFund = new FundInfo();
        newFund.setFundCode("NEW_FUND_CODE");
        newFund.setFundName("新基金");
        Long createdId = 2L;

        when(fundService.createFund(any(FundInfo.class))).thenReturn(createdId);

        Result<Long> result = fundController.createFund(newFund);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("创建基金成功", result.getMessage());
        assertEquals(createdId, result.getData());
        verify(fundService, times(1)).createFund(newFund);
    }

    @Test
    void createFund_failure() {
        FundInfo newFund = new FundInfo();
        newFund.setFundCode("EXISTING_FUND");
        String errorMessage = "Fund already exists";
        when(fundService.createFund(any(FundInfo.class))).thenThrow(new RuntimeException(errorMessage));

        Result<Long> result = fundController.createFund(newFund);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).createFund(newFund);
    }

    @Test
    void updateFund_success() {
        Long id = 1L;
        FundInfo updatedFund = new FundInfo();
        updatedFund.setId(id);
        updatedFund.setFundName("更新后的基金名");

        doNothing().when(fundService).updateFund(any(FundInfo.class));

        Result<Void> result = fundController.updateFund(id, updatedFund);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("更新基金成功", result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).updateFund(updatedFund);
        assertEquals(id, updatedFund.getId()); // Ensure ID is set correctly
    }

    @Test
    void updateFund_failure() {
        Long id = 1L;
        FundInfo updatedFund = new FundInfo();
        updatedFund.setId(id);
        String errorMessage = "Update validation failed";
        doThrow(new RuntimeException(errorMessage)).when(fundService).updateFund(any(FundInfo.class));

        Result<Void> result = fundController.updateFund(id, updatedFund);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).updateFund(updatedFund);
    }

    @Test
    void deleteFund_success() {
        Long id = 1L;
        doNothing().when(fundService).deleteFund(id);

        Result<Void> result = fundController.deleteFund(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("删除基金成功", result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).deleteFund(id);
    }

    @Test
    void deleteFund_failure() {
        Long id = 99L;
        String errorMessage = "Fund not found for deletion";
        doThrow(new RuntimeException(errorMessage)).when(fundService).deleteFund(id);

        Result<Void> result = fundController.deleteFund(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(fundService, times(1)).deleteFund(id);
    }
}