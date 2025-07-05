package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.StrategyInfo;
import com.advisor.entity.StrategyHolding;
import com.advisor.service.StrategyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class StrategyControllerTest {

    @Mock
    private StrategyService strategyService;

    @InjectMocks
    private StrategyController strategyController;

    private StrategyInfo mockStrategyInfo;
    private StrategyHolding mockStrategyHolding;
    private PageResult<StrategyInfo> mockPageResult;
    private List<StrategyHolding> mockHoldingsList;

    @BeforeEach
    void setUp() {
        mockStrategyInfo = new StrategyInfo();
        mockStrategyInfo.setId(1L);
        mockStrategyInfo.setStrategyCode("ALPHA001");
        mockStrategyInfo.setStrategyName("阿尔法策略");
        mockStrategyInfo.setStrategyType("趋势跟踪");
        mockStrategyInfo.setRiskLevel(3);
        mockStrategyInfo.setTargetReturn(BigDecimal.valueOf(0.15));
        mockStrategyInfo.setMaxDrawdown(BigDecimal.valueOf(0.10));
        mockStrategyInfo.setRebalanceFrequency("月度");
        mockStrategyInfo.setDescription("一个用于测试的阿尔法策略");
        mockStrategyInfo.setStatus(1);
        mockStrategyInfo.setCreatedAt(LocalDateTime.now());
        mockStrategyInfo.setUpdatedAt(LocalDateTime.now());

        mockStrategyHolding = new StrategyHolding();
        mockStrategyHolding.setId(10L);
        mockStrategyHolding.setStrategyId(1L);
        mockStrategyHolding.setAssetCode("FUND001");
        mockStrategyHolding.setAssetName("基金A");
        mockStrategyHolding.setAssetType("基金");
        mockStrategyHolding.setTargetWeight(BigDecimal.valueOf(0.50));
        mockStrategyHolding.setCurrentWeight(BigDecimal.valueOf(0.48));
        mockStrategyHolding.setCreatedAt(LocalDateTime.now());
        mockStrategyHolding.setUpdatedAt(LocalDateTime.now());

        mockPageResult = PageResult.of(Collections.singletonList(mockStrategyInfo), 1L, 1, 20);
        mockHoldingsList = Collections.singletonList(mockStrategyHolding);
    }

    @Test
    void getStrategyList_success() {
        String strategyType = "趋势跟踪";
        String keyword = "阿尔法";
        Integer current = 1;
        Integer size = 20;

        when(strategyService.getStrategyList(strategyType, keyword, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<StrategyInfo>> result = strategyController.getStrategyList(strategyType, keyword, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(strategyService, times(1)).getStrategyList(strategyType, keyword, current, size);
    }

    @Test
    void getStrategyList_failure() {
        String errorMessage = "Strategy list query failed";
        when(strategyService.getStrategyList(any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<StrategyInfo>> result = strategyController.getStrategyList(null, null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).getStrategyList(any(), any(), any(), any());
    }

    @Test
    void getStrategyById_success() {
        Long id = 1L;
        when(strategyService.getStrategyById(id)).thenReturn(mockStrategyInfo);

        Result<StrategyInfo> result = strategyController.getStrategyById(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockStrategyInfo, result.getData());
        verify(strategyService, times(1)).getStrategyById(id);
    }

    @Test
    void getStrategyById_failure() {
        Long id = 99L;
        String errorMessage = "Strategy not found";
        when(strategyService.getStrategyById(id)).thenThrow(new RuntimeException(errorMessage));

        Result<StrategyInfo> result = strategyController.getStrategyById(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).getStrategyById(id);
    }

    @Test
    void createStrategy_success() {
        StrategyInfo newStrategy = new StrategyInfo();
        newStrategy.setStrategyCode("NEW_STRAT");
        newStrategy.setStrategyName("新策略");
        Long createdId = 2L;

        when(strategyService.createStrategy(any(StrategyInfo.class))).thenReturn(createdId);

        Result<Long> result = strategyController.createStrategy(newStrategy);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("创建策略成功", result.getMessage());
        assertEquals(createdId, result.getData());
        verify(strategyService, times(1)).createStrategy(newStrategy);
    }

    @Test
    void createStrategy_failure() {
        StrategyInfo newStrategy = new StrategyInfo();
        newStrategy.setStrategyCode("EXISTING_STRAT");
        String errorMessage = "Strategy code already exists";
        when(strategyService.createStrategy(any(StrategyInfo.class))).thenThrow(new RuntimeException(errorMessage));

        Result<Long> result = strategyController.createStrategy(newStrategy);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).createStrategy(newStrategy);
    }

    @Test
    void updateStrategy_success() {
        Long id = 1L;
        StrategyInfo updatedStrategy = new StrategyInfo();
        updatedStrategy.setId(id);
        updatedStrategy.setStrategyName("更新后的策略名");

        doNothing().when(strategyService).updateStrategy(any(StrategyInfo.class));

        Result<Void> result = strategyController.updateStrategy(id, updatedStrategy);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("更新策略成功", result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).updateStrategy(updatedStrategy);
        assertEquals(id, updatedStrategy.getId());
    }

    @Test
    void updateStrategy_failure() {
        Long id = 1L;
        StrategyInfo updatedStrategy = new StrategyInfo();
        updatedStrategy.setId(id);
        String errorMessage = "Update validation failed";
        doThrow(new RuntimeException(errorMessage)).when(strategyService).updateStrategy(any(StrategyInfo.class));

        Result<Void> result = strategyController.updateStrategy(id, updatedStrategy);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).updateStrategy(updatedStrategy);
    }

    @Test
    void deleteStrategy_success() {
        Long id = 1L;
        doNothing().when(strategyService).deleteStrategy(id);

        Result<Void> result = strategyController.deleteStrategy(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("删除策略成功", result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).deleteStrategy(id);
    }

    @Test
    void deleteStrategy_failure() {
        Long id = 99L;
        String errorMessage = "Strategy not found for deletion";
        doThrow(new RuntimeException(errorMessage)).when(strategyService).deleteStrategy(id);

        Result<Void> result = strategyController.deleteStrategy(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).deleteStrategy(id);
    }

    @Test
    void getStrategyHoldings_success() {
        Long id = 1L;
        when(strategyService.getStrategyHoldings(id)).thenReturn(mockHoldingsList);

        Result<List<StrategyHolding>> result = strategyController.getStrategyHoldings(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockHoldingsList, result.getData());
        verify(strategyService, times(1)).getStrategyHoldings(id);
    }

    @Test
    void getStrategyHoldings_failure() {
        Long id = 99L;
        String errorMessage = "Holdings not found";
        when(strategyService.getStrategyHoldings(id)).thenThrow(new RuntimeException(errorMessage));

        Result<List<StrategyHolding>> result = strategyController.getStrategyHoldings(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).getStrategyHoldings(id);
    }

    @Test
    void updateStrategyHoldings_success() {
        Long id = 1L;
        List<StrategyHolding> updatedHoldings = Arrays.asList(
                new StrategyHolding(), new StrategyHolding()
        );
        updatedHoldings.get(0).setStrategyId(id);
        updatedHoldings.get(1).setStrategyId(id);

        doNothing().when(strategyService).updateStrategyHoldings(eq(id), anyList());

        Result<Void> result = strategyController.updateStrategyHoldings(id, updatedHoldings);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("更新策略持仓成功", result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).updateStrategyHoldings(eq(id), eq(updatedHoldings));
    }

    @Test
    void updateStrategyHoldings_failure() {
        Long id = 1L;
        List<StrategyHolding> updatedHoldings = Arrays.asList(
                new StrategyHolding()
        );
        String errorMessage = "Failed to save holdings";
        doThrow(new RuntimeException(errorMessage)).when(strategyService).updateStrategyHoldings(eq(id), anyList());

        Result<Void> result = strategyController.updateStrategyHoldings(id, updatedHoldings);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).updateStrategyHoldings(eq(id), eq(updatedHoldings));
    }

    @Test
    void getStrategyOptions_success() {
        List<String> strategyTypes = Arrays.asList("趋势跟踪", "价值投资");
        Map<String, List<String>> expectedOptions = Map.of(
                "strategyTypes", strategyTypes
        );

        when(strategyService.getAllStrategyTypes()).thenReturn(strategyTypes);

        Result<Map<String, List<String>>> result = strategyController.getStrategyOptions();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(expectedOptions, result.getData());
        verify(strategyService, times(1)).getAllStrategyTypes();
    }

    @Test
    void getStrategyOptions_failure() {
        String errorMessage = "Error fetching strategy options";
        when(strategyService.getAllStrategyTypes()).thenThrow(new RuntimeException(errorMessage));

        Result<Map<String, List<String>>> result = strategyController.getStrategyOptions();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(strategyService, times(1)).getAllStrategyTypes();
    }
}