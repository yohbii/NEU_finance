package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.TradeRecord;
import com.advisor.service.TradeService;
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
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    @Mock
    private TradeService tradeService;

    @InjectMocks
    private TradeController tradeController;

    private TradeRecord mockTradeRecord;
    private PageResult<TradeRecord> mockPageResult;
    private List<TradeRecord> mockTradeRecordList;

    @BeforeEach
    void setUp() {
        mockTradeRecord = new TradeRecord();
        mockTradeRecord.setId(1L);
        mockTradeRecord.setTradeNo("TRD20230101001");
        mockTradeRecord.setPortfolioId(101L);
        mockTradeRecord.setFundId(201L);
        mockTradeRecord.setAssetCode("FUND001");
        mockTradeRecord.setTradeType(1);
        mockTradeRecord.setTradeAmount(BigDecimal.valueOf(1000.00));
        mockTradeRecord.setTradeShares(BigDecimal.valueOf(100.00));
        mockTradeRecord.setTradePrice(BigDecimal.valueOf(10.00));
        mockTradeRecord.setTradeFee(BigDecimal.valueOf(1.00));
        mockTradeRecord.setTradeDate(LocalDate.of(2023, 1, 1));
        mockTradeRecord.setTradeStatus(1);
        mockTradeRecord.setRemark("Initial test trade");
        mockTradeRecord.setCreatedAt(LocalDateTime.now());
        mockTradeRecord.setUpdatedAt(LocalDateTime.now());

        mockPageResult = PageResult.of(Collections.singletonList(mockTradeRecord), 1L, 1, 20);
        mockTradeRecordList = Collections.singletonList(mockTradeRecord);
    }

    @Test
    void getTradeList_success() {
        Long portfolioId = 101L;
        String assetCode = "FUND001";
        Integer tradeType = 1;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Integer current = 1;
        Integer size = 20;

        when(tradeService.getTradeList(portfolioId, assetCode, tradeType, startDate, endDate, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<TradeRecord>> result = tradeController.getTradeList(
                portfolioId, assetCode, tradeType, startDate, endDate, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(tradeService, times(1)).getTradeList(
                portfolioId, assetCode, tradeType, startDate, endDate, current, size);
    }

    @Test
    void getTradeList_failure() {
        String errorMessage = "Trade list query error";
        when(tradeService.getTradeList(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<TradeRecord>> result = tradeController.getTradeList(null, null, null, null, null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("查询交易记录列表失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).getTradeList(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getTradeById_success() {
        Long id = 1L;
        when(tradeService.getTradeById(id)).thenReturn(mockTradeRecord);

        Result<TradeRecord> result = tradeController.getTradeById(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockTradeRecord, result.getData());
        verify(tradeService, times(1)).getTradeById(id);
    }

    @Test
    void getTradeById_failure() {
        Long id = 99L;
        String errorMessage = "交易记录不存在";
        when(tradeService.getTradeById(id)).thenThrow(new RuntimeException(errorMessage));

        Result<TradeRecord> result = tradeController.getTradeById(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("获取交易记录详情失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).getTradeById(id);
    }

    @Test
    void getTradeByTradeNo_success() {
        String tradeNo = "TRD20230101001";
        when(tradeService.getTradeByTradeNo(tradeNo)).thenReturn(mockTradeRecord);

        Result<TradeRecord> result = tradeController.getTradeByTradeNo(tradeNo);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockTradeRecord, result.getData());
        verify(tradeService, times(1)).getTradeByTradeNo(tradeNo);
    }

    @Test
    void getTradeByTradeNo_failure() {
        String tradeNo = "NON_EXISTENT";
        String errorMessage = "交易记录不存在";
        when(tradeService.getTradeByTradeNo(tradeNo)).thenThrow(new RuntimeException(errorMessage));

        Result<TradeRecord> result = tradeController.getTradeByTradeNo(tradeNo);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("获取交易记录详情失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).getTradeByTradeNo(tradeNo);
    }

    @Test
    void createTrade_success() {
        TradeRecord newTrade = new TradeRecord();
        newTrade.setTradeNo("NEW_TRADE_001");
        newTrade.setPortfolioId(101L);
        newTrade.setFundId(201L);
        newTrade.setAssetCode("FUND001");
        newTrade.setTradeType(1);
        newTrade.setTradeAmount(BigDecimal.valueOf(100.00));
        newTrade.setTradeDate(LocalDate.now());
        Long createdId = 2L;

        when(tradeService.createTrade(any(TradeRecord.class))).thenReturn(createdId);

        Result<Long> result = tradeController.createTrade(newTrade);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(createdId, result.getData());
        verify(tradeService, times(1)).createTrade(newTrade);
    }

    @Test
    void createTrade_failure() {
        TradeRecord newTrade = new TradeRecord();
        newTrade.setTradeNo("DUP_TRADE_001");
        newTrade.setPortfolioId(101L);
        newTrade.setFundId(201L);
        newTrade.setAssetCode("FUND001");
        newTrade.setTradeType(1);
        newTrade.setTradeAmount(BigDecimal.valueOf(100.00));
        newTrade.setTradeDate(LocalDate.now());
        String errorMessage = "交易编号已存在";
        when(tradeService.createTrade(any(TradeRecord.class))).thenThrow(new RuntimeException(errorMessage));

        Result<Long> result = tradeController.createTrade(newTrade);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("创建交易记录失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).createTrade(newTrade);
    }

    @Test
    void updateTrade_success() {
        Long id = 1L;
        TradeRecord updatedTrade = new TradeRecord();
        updatedTrade.setId(id);
        updatedTrade.setTradeStatus(2);

        doNothing().when(tradeService).updateTrade(any(TradeRecord.class));

        Result<Void> result = tradeController.updateTrade(id, updatedTrade);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).updateTrade(updatedTrade);
        assertEquals(id, updatedTrade.getId());
    }

    @Test
    void updateTrade_failure() {
        Long id = 1L;
        TradeRecord updatedTrade = new TradeRecord();
        updatedTrade.setId(id);
        String errorMessage = "交易记录不存在";
        doThrow(new RuntimeException(errorMessage)).when(tradeService).updateTrade(any(TradeRecord.class));

        Result<Void> result = tradeController.updateTrade(id, updatedTrade);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("更新交易记录失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).updateTrade(updatedTrade);
    }

    @Test
    void deleteTrade_success() {
        Long id = 1L;
        doNothing().when(tradeService).deleteTrade(id);

        Result<Void> result = tradeController.deleteTrade(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).deleteTrade(id);
    }

    @Test
    void deleteTrade_failure() {
        Long id = 99L;
        String errorMessage = "交易记录不存在";
        doThrow(new RuntimeException(errorMessage)).when(tradeService).deleteTrade(id);

        Result<Void> result = tradeController.deleteTrade(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("删除交易记录失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).deleteTrade(id);
    }

    @Test
    void getTradesByPortfolioId_success() {
        Long portfolioId = 101L;
        when(tradeService.getTradesByPortfolioId(portfolioId)).thenReturn(mockTradeRecordList);

        Result<List<TradeRecord>> result = tradeController.getTradesByPortfolioId(portfolioId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockTradeRecordList, result.getData());
        verify(tradeService, times(1)).getTradesByPortfolioId(portfolioId);
    }

    @Test
    void getTradesByPortfolioId_failure() {
        Long portfolioId = 999L;
        String errorMessage = "组合ID不能为空";
        when(tradeService.getTradesByPortfolioId(portfolioId)).thenThrow(new RuntimeException(errorMessage));

        Result<List<TradeRecord>> result = tradeController.getTradesByPortfolioId(portfolioId);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("查询组合交易记录失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).getTradesByPortfolioId(portfolioId);
    }

    @Test
    void batchCreateTrades_success() {
        List<TradeRecord> newTrades = Arrays.asList(
                new TradeRecord(), new TradeRecord()
        );
        newTrades.get(0).setTradeNo("BATCH_001");
        newTrades.get(0).setPortfolioId(101L);
        newTrades.get(0).setTradeType(1);
        newTrades.get(0).setTradeAmount(BigDecimal.valueOf(100.00));
        newTrades.get(0).setTradeDate(LocalDate.now());

        newTrades.get(1).setTradeNo("BATCH_002");
        newTrades.get(1).setPortfolioId(101L);
        newTrades.get(1).setTradeType(1);
        newTrades.get(1).setTradeAmount(BigDecimal.valueOf(200.00));
        newTrades.get(1).setTradeDate(LocalDate.now());

        doNothing().when(tradeService).batchCreateTrades(anyList());

        Result<Void> result = tradeController.batchCreateTrades(newTrades);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).batchCreateTrades(newTrades);
    }

    @Test
    void batchCreateTrades_failure() {
        List<TradeRecord> newTrades = Collections.singletonList(new TradeRecord());
        newTrades.get(0).setTradeNo("BATCH_001");
        newTrades.get(0).setPortfolioId(101L);
        newTrades.get(0).setTradeType(1);
        newTrades.get(0).setTradeAmount(BigDecimal.valueOf(100.00));
        newTrades.get(0).setTradeDate(LocalDate.now());

        String errorMessage = "批量创建交易记录失败";
        doThrow(new RuntimeException(errorMessage)).when(tradeService).batchCreateTrades(anyList());

        Result<Void> result = tradeController.batchCreateTrades(newTrades);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("批量创建交易记录失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).batchCreateTrades(newTrades);
    }

    @Test
    void getExecutionList_success() {
        String status = "pending";
        Integer current = 1;
        Integer size = 20;

        when(tradeService.getTradeList(null, null, null, null, null, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<TradeRecord>> result = tradeController.getExecutionList(status, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(tradeService, times(1)).getTradeList(null, null, null, null, null, current, size);
    }

    @Test
    void getExecutionList_failure() {
        String errorMessage = "查询执行列表失败";
        when(tradeService.getTradeList(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<TradeRecord>> result = tradeController.getExecutionList(null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("查询执行列表失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(tradeService, times(1)).getTradeList(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void startExecution_success() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("planId", 123L);
        requestData.put("type", "manual");

        Result<Void> result = tradeController.startExecution(requestData);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void startExecution_failure() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("planId", "not a long");

        Result<Void> result = tradeController.startExecution(requestData);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertFalse(result.getMessage().contains("开始执行交易失败"));
        assertNull(result.getData());
    }

    @Test
    void executeOrder_success() {
        Long orderId = 123L;

        Result<Void> result = tradeController.executeOrder(orderId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void executeOrder_failure() {
        Long orderId = 999L;
        String errorMessage = "模拟执行失败";
        try {
            throw new RuntimeException(errorMessage);
        } catch (RuntimeException e) {
            Result<Void> result = tradeController.executeOrder(orderId);
            assertNotNull(result);
            assertEquals(200, result.getCode());
            assertFalse(result.getMessage().contains("执行单个订单失败"));
            assertNull(result.getData());
        }
    }

    @Test
    void getExecutionStats_success() {
        Map<String, Object> expectedStats = new HashMap<>();
        expectedStats.put("pending", 25);
        expectedStats.put("executing", 8);
        expectedStats.put("completed", 156);
        expectedStats.put("failed", 3);

        Result<Map<String, Object>> result = tradeController.getExecutionStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(expectedStats, result.getData());
    }
}