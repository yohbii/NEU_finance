package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.StrategyInfo;
import com.advisor.entity.StrategyHolding;
import com.advisor.service.StrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 策略控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;

    /**
     * 分页查询策略列表
     */
    @GetMapping
    public Result<PageResult<StrategyInfo>> getStrategyList(
            @RequestParam(value = "strategyType", required = false) String strategyType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        
        try {
            PageResult<StrategyInfo> result = strategyService.getStrategyList(strategyType, keyword, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询策略列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取策略详情
     */
    @GetMapping("/{id}")
    public Result<StrategyInfo> getStrategyById(@PathVariable Long id) {
        try {
            StrategyInfo strategyInfo = strategyService.getStrategyById(id);
            return Result.success(strategyInfo);
        } catch (Exception e) {
            log.error("获取策略详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建策略
     */
    @PostMapping
    public Result<Long> createStrategy(@RequestBody StrategyInfo strategyInfo) {
        try {
            Long strategyId = strategyService.createStrategy(strategyInfo);
            return Result.success("创建策略成功", strategyId);
        } catch (Exception e) {
            log.error("创建策略失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新策略
     */
    @PutMapping("/{id}")
    public Result<Void> updateStrategy(@PathVariable Long id, @RequestBody StrategyInfo strategyInfo) {
        try {
            strategyInfo.setId(id);
            strategyService.updateStrategy(strategyInfo);
            return Result.success("更新策略成功");
        } catch (Exception e) {
            log.error("更新策略失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除策略
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteStrategy(@PathVariable Long id) {
        try {
            strategyService.deleteStrategy(id);
            return Result.success("删除策略成功");
        } catch (Exception e) {
            log.error("删除策略失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取策略持仓列表
     */
    @GetMapping("/{id}/holdings")
    public Result<List<StrategyHolding>> getStrategyHoldings(@PathVariable Long id) {
        try {
            List<StrategyHolding> holdings = strategyService.getStrategyHoldings(id);
            return Result.success(holdings);
        } catch (Exception e) {
            log.error("获取策略持仓失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新策略持仓
     */
    @PutMapping("/{id}/holdings")
    public Result<Void> updateStrategyHoldings(@PathVariable Long id, 
                                              @RequestBody List<StrategyHolding> holdings) {
        try {
            strategyService.updateStrategyHoldings(id, holdings);
            return Result.success("更新策略持仓成功");
        } catch (Exception e) {
            log.error("更新策略持仓失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取策略筛选选项
     */
    @GetMapping("/options")
    public Result<Map<String, List<String>>> getStrategyOptions() {
        try {
            List<String> strategyTypes = strategyService.getAllStrategyTypes();
            
            Map<String, List<String>> options = Map.of(
                "strategyTypes", strategyTypes
            );
            
            return Result.success(options);
        } catch (Exception e) {
            log.error("获取策略筛选选项失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
} 