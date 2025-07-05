package com.advisor.service;

import com.advisor.entity.*;
import com.advisor.mapper.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class FunctionExecutorServiceTest {

    @Mock
    private FundInfoMapper fundInfoMapper;
    @Mock
    private FundPerformanceMapper fundPerformanceMapper;
    @Mock
    private StrategyInfoMapper strategyInfoMapper;
    @Mock
    private FactorInfoMapper factorInfoMapper;
    @Mock
    private FundNetValueMapper fundNetValueMapper;

    @InjectMocks
    private FunctionExecutorService functionExecutorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("get_fund_info - 正常返回基金信息")
    void testGetFundInfo() throws Exception {
        FundInfo fund = new FundInfo();
        fund.setId(1L);
        fund.setFundCode("000001");
        fund.setFundName("测试基金");
        fund.setFundType("股票型");

        when(fundInfoMapper.findByCode("000001")).thenReturn(fund);

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("fund_code", "000001");

        Object result = functionExecutorService.execute("get_fund_info", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof FundInfo);
        assertEquals("测试基金", ((FundInfo) result).getFundName());
    }

    @Test
    @DisplayName("get_fund_historical_nav - 查询基金净值")
    void testGetFundHistoricalNav() throws Exception {
        FundInfo fund = new FundInfo();
        fund.setId(1L);
        fund.setFundCode("000002");

        List<FundNetValue> netValues = Arrays.asList(
                createNetValue(LocalDate.of(2023, 1, 1), BigDecimal.valueOf(1.11)),
                createNetValue(LocalDate.of(2023, 1, 2), BigDecimal.valueOf(1.12))
        );

        when(fundInfoMapper.findByCode("000002")).thenReturn(fund);
        when(fundNetValueMapper.findByFundIdAndDateRange(1L, "2023-01-01", "2023-01-02")).thenReturn(netValues);

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("fund_code", "000002");
        args.put("start_date", "2023-01-01");
        args.put("end_date", "2023-01-02");

        Object result = functionExecutorService.execute("get_fund_historical_nav", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof List<?>);
        assertEquals(2, ((List<?>) result).size());
    }

    @Test
    @DisplayName("get_fund_performance - 查询基金业绩")
    void testGetFundPerformance() throws Exception {
        FundInfo fund = new FundInfo();
        fund.setId(1L);
        fund.setFundCode("000003");

        FundPerformance performance = new FundPerformance();
        performance.setFundId(1L);
        performance.setYtdReturn(BigDecimal.valueOf(0.12));

        when(fundInfoMapper.findByCode("000003")).thenReturn(fund);
        when(fundPerformanceMapper.findLatestByFundId(1L)).thenReturn(performance);

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("fund_code", "000003");

        Object result = functionExecutorService.execute("get_fund_performance", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof FundPerformance);
        assertEquals(BigDecimal.valueOf(0.12), ((FundPerformance) result).getYtdReturn());
    }

    @Test
    @DisplayName("list_funds - 按类型列出基金")
    void testListFunds() throws Exception {
        FundInfo f1 = new FundInfo();
        f1.setFundCode("000001");
        f1.setFundType("混合型");
        f1.setFundName("混合A");

        when(fundInfoMapper.findByType("混合型")).thenReturn(List.of(f1));

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("fund_type", "混合型");

        Object result = functionExecutorService.execute("list_funds", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof List<?>);
        assertEquals("混合A", ((FundInfo) ((List<?>) result).get(0)).getFundName());
    }

    @Test
    @DisplayName("get_strategy_info - 获取策略信息")
    void testGetStrategyInfo() throws Exception {
        StrategyInfo s = new StrategyInfo();
        s.setStrategyName("稳健收益");
        s.setStrategyType("均衡");

        when(strategyInfoMapper.findByName("稳健收益")).thenReturn(s);

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("strategy_name", "稳健收益");

        Object result = functionExecutorService.execute("get_strategy_info", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof StrategyInfo);
        assertEquals("稳健收益", ((StrategyInfo) result).getStrategyName());
    }

    @Test
    @DisplayName("list_strategies - 获取所有策略")
    void testListStrategies() throws Exception {
        StrategyInfo s = new StrategyInfo();
        s.setStrategyName("策略A");

        when(strategyInfoMapper.findAll()).thenReturn(List.of(s));

        Object result = functionExecutorService.execute("list_strategies", "{}");
        assertNotNull(result);
        assertTrue(result instanceof List<?>);
        assertEquals("策略A", ((StrategyInfo) ((List<?>) result).get(0)).getStrategyName());
    }

    @Test
    @DisplayName("get_factor_info - 获取因子信息")
    void testGetFactorInfo() throws Exception {
        FactorInfo f = new FactorInfo();
        f.setFactorName("波动率");
        f.setFactorCode("VOL");

        when(factorInfoMapper.findByName("波动率")).thenReturn(f);

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("factor_name", "波动率");

        Object result = functionExecutorService.execute("get_factor_info", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof FactorInfo);
        assertEquals("VOL", ((FactorInfo) result).getFactorCode());
    }

    @Test
    @DisplayName("search_funds - 多条件搜索基金")
    void testSearchFunds() throws Exception {
        FundInfo fund = new FundInfo();
        fund.setFundCode("000001");
        fund.setFundName("稳健混合");

        Map<String, Object> query = new HashMap<>();
        query.put("fund_type", "混合型");
        query.put("risk_level", 3);
        query.put("fund_company", "华夏基金");

        when(fundInfoMapper.searchFunds(query)).thenReturn(List.of(fund));

        ObjectNode args = JsonNodeFactory.instance.objectNode();
        args.put("fund_type", "混合型");
        args.put("risk_level", 3);
        args.put("fund_company", "华夏基金");

        Object result = functionExecutorService.execute("search_funds", args.toString());
        assertNotNull(result);
        assertTrue(result instanceof List<?>);
        assertEquals("稳健混合", ((FundInfo) ((List<?>) result).get(0)).getFundName());
    }

    private FundNetValue createNetValue(LocalDate date, BigDecimal value) {
        FundNetValue v = new FundNetValue();
        v.setTradeDate(date);
        v.setUnitNetValue(value);
        return v;
    }
}
