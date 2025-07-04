package com.advisor.service;

import com.advisor.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 仪表板服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FundInfoMapper fundInfoMapper;
    private final StrategyInfoMapper strategyInfoMapper;
    private final PortfolioProductMapper portfolioProductMapper;
    private final TradeRecordMapper tradeRecordMapper;
    private final FundNetValueMapper fundNetValueMapper;
    private final FundPerformanceMapper fundPerformanceMapper;

    /**
     * 获取仪表板统计数据
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询基金总数
        Long totalFunds = fundInfoMapper.countAll();
        stats.put("totalFunds", totalFunds);
        
        // 查询策略数量
        Long totalStrategies = strategyInfoMapper.countAll();
        stats.put("totalStrategies", totalStrategies);
        
        // 查询组合产品数量
        Long totalProducts = portfolioProductMapper.countAll();
        stats.put("totalProducts", totalProducts);
        
        // 查询今日交易数量
        LocalDate today = LocalDate.now();
        Long todayTrades = tradeRecordMapper.countByDate(today);
        stats.put("todayTrades", todayTrades);
        
        return stats;
    }

    /**
     * 获取净值走势数据
     */
    public Map<String, Object> getNetValueTrend() {
        Map<String, Object> data = new HashMap<>();
        
        // 获取最近12个月的数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11);
        
        List<String> months = new ArrayList<>();
        List<Double> portfolioValues = new ArrayList<>();
        List<Double> benchmarkValues = new ArrayList<>();
        
        // 生成月份标签和查询数据
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String monthLabel = currentDate.format(DateTimeFormatter.ofPattern("M月"));
            months.add(monthLabel);
            
            // 查询该月的净值数据（这里简化处理，实际应该查询具体的组合净值）
            Double avgNetValue = fundNetValueMapper.getAvgNetValueByMonth(
                currentDate.getYear(), currentDate.getMonthValue());
            portfolioValues.add(avgNetValue != null ? avgNetValue * 100 - 100 : 0.0);
            
            // 基准指数数据（模拟，实际应该从基准指数表查询）
            benchmarkValues.add(avgNetValue != null ? (avgNetValue * 100 - 100) * 0.8 : 0.0);
            
            currentDate = currentDate.plusMonths(1);
        }
        
        data.put("months", months);
        data.put("portfolioValues", portfolioValues);
        data.put("benchmarkValues", benchmarkValues);
        
        return data;
    }

    /**
     * 获取资产配置数据
     */
    public List<Map<String, Object>> getAssetAllocation() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        // 查询各类型基金的数量和占比
        List<Map<String, Object>> fundTypeStats = fundInfoMapper.getFundTypeStats();
        
        for (Map<String, Object> stat : fundTypeStats) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", stat.get("fund_type"));
            item.put("value", stat.get("count"));
            data.add(item);
        }
        
        // 如果没有数据，返回默认数据
        if (data.isEmpty()) {
            data.add(Map.of("name", "股票型基金", "value", 35));
            data.add(Map.of("name", "债券型基金", "value", 25));
            data.add(Map.of("name", "混合型基金", "value", 20));
            data.add(Map.of("name", "货币基金", "value", 15));
            data.add(Map.of("name", "其他", "value", 5));
        }
        
        return data;
    }

    /**
     * 获取收益分析数据
     */
    public Map<String, Object> getReturnAnalysis() {
        Map<String, Object> data = new HashMap<>();
        
        // 获取最近12个月的收益数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11);
        
        List<String> months = new ArrayList<>();
        List<Double> monthlyReturns = new ArrayList<>();
        List<Double> cumulativeReturns = new ArrayList<>();
        
        double cumulativeReturn = 0.0;
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            String monthLabel = currentDate.format(DateTimeFormatter.ofPattern("M月"));
            months.add(monthLabel);
            
            // 查询该月的平均收益率
            Double avgReturn = fundPerformanceMapper.getAvgReturnByMonth(
                currentDate.getYear(), currentDate.getMonthValue());
            double monthReturn = avgReturn != null ? avgReturn : 0.0;
            
            monthlyReturns.add(monthReturn);
            cumulativeReturn += monthReturn;
            cumulativeReturns.add(cumulativeReturn);
            
            currentDate = currentDate.plusMonths(1);
        }
        
        data.put("months", months);
        data.put("monthlyReturns", monthlyReturns);
        data.put("cumulativeReturns", cumulativeReturns);
        
        return data;
    }

    /**
     * 获取风险指标数据
     */
    public Map<String, Object> getRiskMetrics() {
        Map<String, Object> data = new HashMap<>();
        
        List<Map<String, Object>> indicators = List.of(
            Map.of("name", "年化收益", "max", 20),
            Map.of("name", "夏普比率", "max", 3),
            Map.of("name", "最大回撤", "max", 20),
            Map.of("name", "波动率", "max", 25),
            Map.of("name", "信息比率", "max", 2),
            Map.of("name", "Beta系数", "max", 1.5)
        );
        
        // 查询当前组合的风险指标
        Map<String, Object> currentMetrics = fundPerformanceMapper.getCurrentRiskMetrics();
        List<Double> currentValues = new ArrayList<>();
        
        if (currentMetrics != null) {
            currentValues.add(getDoubleValue(currentMetrics, "annual_return", 12.5));
            currentValues.add(getDoubleValue(currentMetrics, "sharpe_ratio", 1.8));
            currentValues.add(getDoubleValue(currentMetrics, "max_drawdown", 8.2));
            currentValues.add(getDoubleValue(currentMetrics, "volatility", 15.3));
            currentValues.add(1.2); // 信息比率，暂时用默认值
            currentValues.add(0.95); // Beta系数，暂时用默认值
        } else {
            currentValues = List.of(12.5, 1.8, 8.2, 15.3, 1.2, 0.95);
        }
        
        Map<String, Object> currentPortfolio = new HashMap<>();
        currentPortfolio.put("name", "当前组合");
        currentPortfolio.put("value", currentValues);
        
        Map<String, Object> benchmark = new HashMap<>();
        benchmark.put("name", "基准指数");
        benchmark.put("value", List.of(10.2, 1.5, 12.1, 18.7, 0.9, 1.1));
        
        data.put("indicators", indicators);
        data.put("series", List.of(currentPortfolio, benchmark));
        
        return data;
    }

    /**
     * 获取最新动态
     */
    public List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // 查询最近的交易记录
        List<Map<String, Object>> recentTrades = tradeRecordMapper.getRecentTrades(5);
        for (Map<String, Object> trade : recentTrades) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", trade.get("id"));
            activity.put("time", trade.get("created_at"));
            activity.put("description", "执行交易：" + trade.get("trade_no") + 
                        "，金额：" + trade.get("trade_amount"));
            activities.add(activity);
        }
        
        // 查询最近创建的策略
        List<Map<String, Object>> recentStrategies = strategyInfoMapper.getRecentStrategies(3);
        for (Map<String, Object> strategy : recentStrategies) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", "strategy_" + strategy.get("id"));
            activity.put("time", strategy.get("created_at"));
            activity.put("description", "创建策略：" + strategy.get("strategy_name"));
            activities.add(activity);
        }
        
        // 按时间排序
        activities.sort((a, b) -> {
            String timeA = (String) a.get("time");
            String timeB = (String) b.get("time");
            return timeB.compareTo(timeA); // 降序排列
        });
        
        // 如果数据不足，添加一些默认数据
        if (activities.size() < 5) {
            activities.add(Map.of(
                "id", "default_1",
                "time", LocalDate.now().minusDays(1) + " 16:40",
                "description", "风险预警：部分债券基金波动率超出预期"
            ));
        }
        
        return activities.subList(0, Math.min(activities.size(), 10));
    }

    /**
     * 安全获取Double值的辅助方法
     */
    private Double getDoubleValue(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
} 