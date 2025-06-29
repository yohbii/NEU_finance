package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.entity.*;
import org.project.backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StrategyService {

    @Autowired
    private StrategyMapper strategyMapper;
    @Autowired
    private StrategyAssetAllocationMapper assetAllocationMapper;
    @Autowired
    private StrategyFundHoldingMapper fundHoldingMapper;
    @Autowired
    private StrategyRebalanceMapper rebalanceMapper;
    @Autowired
    private StrategyRebalanceDetailMapper rebalanceDetailMapper;
    @Autowired
    private StrategyPerformanceMapper performanceMapper;
    @Autowired
    private StrategyBacktestMapper backtestMapper;
    @Autowired
    private StrategyMonitorMapper monitorMapper;

    // =================================================================
    // == 策略管理 (Strategy)
    // =================================================================

    /**
     * 创建一个新策略
     */
    @Transactional
    public HttpResponse<Long> createStrategy(Strategy strategy) {
        int res = strategyMapper.insert(strategy);
        if (res <= 0 || strategy.getId() == null) {
            return new HttpResponse<>(-1, null, "创建策略失败", "创建策略失败");
        }
        return new HttpResponse<>(0, strategy.getId(), "ok", null);
    }

    /**
     * 查询策略
     */
    public HttpResponse<Strategy> findStrategyById(Long id) {
        Strategy strategy = strategyMapper.selectById(id);
        if (strategy == null) {
            return new HttpResponse<>(-1, null, "策略不存在", "策略不存在");
        }
        return new HttpResponse<>(0, strategy, "ok", null);
    }

    /**
     * 查询所有策略
     */
    public HttpResponse<List<Strategy>> findAllStrategies() {
        List<Strategy> list = strategyMapper.selectAll();
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无策略数据", "无策略数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    /**
     * 删除策略及所有关联资产、持仓、调仓等
     */
    @Transactional
    public HttpResponse<Void> deleteStrategyById(Long strategyId) {
        // 依次删除所有关联项
        assetAllocationMapper.deleteByStrategyId(strategyId);
        fundHoldingMapper.deleteByStrategyId(strategyId);
        // 删除调仓明细及调仓主表
        List<StrategyRebalance> rebalances = rebalanceMapper.selectByStrategyId(strategyId);
        if (rebalances != null) {
            for (StrategyRebalance rebalance : rebalances) {
                rebalanceDetailMapper.deleteByRebalanceId(rebalance.getId());
            }
        }
        rebalanceMapper.deleteByStrategyId(strategyId);
        performanceMapper.deleteByStrategyId(strategyId);
        backtestMapper.deleteByStrategyId(strategyId);
        monitorMapper.deleteByStrategyId(strategyId);
        // 删除策略本身
        int count = strategyMapper.deleteById(strategyId);
        if (count <= 0) {
            return new HttpResponse<>(-1, null, "删除失败或不存在", "删除失败或不存在");
        }
        return new HttpResponse<>(0, null, "ok", null);
    }


    // =================================================================
    // == 策略资产分配 子模块 (StrategyAssetAllocation)
    // =================================================================

    public HttpResponse<Long> addAssetAllocation(StrategyAssetAllocation allocation) {
        int res = assetAllocationMapper.insert(allocation);
        if (res <= 0 || allocation.getId() == null) {
            return new HttpResponse<>(-1, null, "新增资产分配失败", "新增资产分配失败");
        }
        return new HttpResponse<>(0, allocation.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyAssetAllocation>> findAssetAllocations(Long strategyId) {
        List<StrategyAssetAllocation> list = assetAllocationMapper.selectByStrategyId(strategyId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无资产分配数据", "无资产分配数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }


    // =================================================================
    // == 策略持仓 子模块 (StrategyFundHolding)
    // =================================================================

    public HttpResponse<Long> addFundHolding(StrategyFundHolding holding) {
        int res = fundHoldingMapper.insert(holding);
        if (res <= 0 || holding.getId() == null) {
            return new HttpResponse<>(-1, null, "新增持仓失败", "新增持仓失败");
        }
        return new HttpResponse<>(0, holding.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyFundHolding>> findFundHoldings(Long strategyId) {
        List<StrategyFundHolding> list = fundHoldingMapper.selectByStrategyId(strategyId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无持仓数据", "无持仓数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }


    // =================================================================
    // == 调仓与调仓明细子模块
    // =================================================================

    public HttpResponse<Long> addRebalance(StrategyRebalance rebalance) {
        int res = rebalanceMapper.insert(rebalance);
        if (res <= 0 || rebalance.getId() == null) {
            return new HttpResponse<>(-1, null, "新增调仓记录失败", "新增调仓记录失败");
        }
        return new HttpResponse<>(0, rebalance.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyRebalance>> findRebalances(Long strategyId) {
        List<StrategyRebalance> list = rebalanceMapper.selectByStrategyId(strategyId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无调仓数据", "无调仓数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    public HttpResponse<Long> addRebalanceDetail(StrategyRebalanceDetail detail) {
        int res = rebalanceDetailMapper.insert(detail);
        if (res <= 0 || detail.getId() == null) {
            return new HttpResponse<>(-1, null, "新增调仓明细失败", "新增调仓明细失败");
        }
        return new HttpResponse<>(0, detail.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyRebalanceDetail>> findRebalanceDetails(Long rebalanceId) {
        List<StrategyRebalanceDetail> list = rebalanceDetailMapper.selectByRebalanceId(rebalanceId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无调仓明细数据", "无调仓明细数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    // =================================================================
    // == 策略回测、绩效、监控等同样可以写类似方法
    // =================================================================

    public HttpResponse<Long> addPerformance(StrategyPerformance performance) {
        int res = performanceMapper.insert(performance);
        if (res <= 0 || performance.getId() == null) {
            return new HttpResponse<>(-1, null, "新增绩效失败", "新增绩效失败");
        }
        return new HttpResponse<>(0, performance.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyPerformance>> findPerformances(Long strategyId) {
        List<StrategyPerformance> list = performanceMapper.selectByStrategyId(strategyId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无绩效数据", "无绩效数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    public HttpResponse<Long> addBacktest(StrategyBacktest backtest) {
        int res = backtestMapper.insert(backtest);
        if (res <= 0 || backtest.getId() == null) {
            return new HttpResponse<>(-1, null, "新增回测记录失败", "新增回测记录失败");
        }
        return new HttpResponse<>(0, backtest.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyBacktest>> findBacktests(Long strategyId) {
        List<StrategyBacktest> list = backtestMapper.selectByStrategyId(strategyId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无回测数据", "无回测数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    public HttpResponse<Long> addMonitor(StrategyMonitor monitor) {
        int res = monitorMapper.insert(monitor);
        if (res <= 0 || monitor.getId() == null) {
            return new HttpResponse<>(-1, null, "新增策略监控失败", "新增策略监控失败");
        }
        return new HttpResponse<>(0, monitor.getId(), "ok", null);
    }

    public HttpResponse<List<StrategyMonitor>> findMonitors(Long strategyId) {
        List<StrategyMonitor> list = monitorMapper.selectByStrategyId(strategyId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无策略监控数据", "无策略监控数据");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

}