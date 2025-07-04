package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.TradeRecord;
import com.advisor.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 交易记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /**
     * 分页查询交易记录列表
     */
    @GetMapping
    public Result<PageResult<TradeRecord>> getTradeList(
            @RequestParam(required = false) Long portfolioId,
            @RequestParam(required = false) String assetCode,
            @RequestParam(required = false) Integer tradeType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        
        try {
            PageResult<TradeRecord> result = tradeService.getTradeList(
                portfolioId, assetCode, tradeType, startDate, endDate, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询交易记录列表失败", e);
            return Result.error("查询交易记录列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取交易记录详情
     */
    @GetMapping("/{id}")
    public Result<TradeRecord> getTradeById(@PathVariable Long id) {
        try {
            TradeRecord trade = tradeService.getTradeById(id);
            return Result.success(trade);
        } catch (Exception e) {
            log.error("获取交易记录详情失败", e);
            return Result.error("获取交易记录详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据交易编号获取交易记录详情
     */
    @GetMapping("/trade-no/{tradeNo}")
    public Result<TradeRecord> getTradeByTradeNo(@PathVariable String tradeNo) {
        try {
            TradeRecord trade = tradeService.getTradeByTradeNo(tradeNo);
            return Result.success(trade);
        } catch (Exception e) {
            log.error("获取交易记录详情失败", e);
            return Result.error("获取交易记录详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建交易记录
     */
    @PostMapping
    public Result<Long> createTrade(@RequestBody TradeRecord tradeRecord) {
        try {
            Long tradeId = tradeService.createTrade(tradeRecord);
            return Result.success(tradeId);
        } catch (Exception e) {
            log.error("创建交易记录失败", e);
            return Result.error("创建交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 更新交易记录
     */
    @PutMapping("/{id}")
    public Result<Void> updateTrade(@PathVariable Long id, @RequestBody TradeRecord tradeRecord) {
        try {
            tradeRecord.setId(id);
            tradeService.updateTrade(tradeRecord);
            return Result.success();
        } catch (Exception e) {
            log.error("更新交易记录失败", e);
            return Result.error("更新交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除交易记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTrade(@PathVariable Long id) {
        try {
            tradeService.deleteTrade(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除交易记录失败", e);
            return Result.error("删除交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 根据组合ID查询交易记录
     */
    @GetMapping("/portfolio/{portfolioId}")
    public Result<List<TradeRecord>> getTradesByPortfolioId(@PathVariable Long portfolioId) {
        try {
            List<TradeRecord> trades = tradeService.getTradesByPortfolioId(portfolioId);
            return Result.success(trades);
        } catch (Exception e) {
            log.error("查询组合交易记录失败", e);
            return Result.error("查询组合交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 批量创建交易记录
     */
    @PostMapping("/batch")
    public Result<Void> batchCreateTrades(@RequestBody List<TradeRecord> tradeRecords) {
        try {
            tradeService.batchCreateTrades(tradeRecords);
            return Result.success();
        } catch (Exception e) {
            log.error("批量创建交易记录失败", e);
            return Result.error("批量创建交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取交易执行列表
     */
    @GetMapping("/execution/list")
    public Result<PageResult<TradeRecord>> getExecutionList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        
        try {
            // 暂时返回普通交易记录列表，后续可以添加执行状态字段
            PageResult<TradeRecord> result = tradeService.getTradeList(
                null, null, null, null, null, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询执行列表失败", e);
            return Result.error("查询执行列表失败：" + e.getMessage());
        }
    }

    /**
     * 开始执行交易
     */
    @PostMapping("/execution/start")
    public Result<Void> startExecution(@RequestBody Map<String, Object> requestData) {
        try {
            log.info("开始执行交易: {}", requestData);
            
            // TODO: 实现真实的交易执行逻辑
            // 这里暂时只记录日志
            
            return Result.success();
        } catch (Exception e) {
            log.error("开始执行交易失败", e);
            return Result.error("开始执行交易失败：" + e.getMessage());
        }
    }

    /**
     * 执行单个订单
     */
    @PostMapping("/execution/execute/{orderId}")
    public Result<Void> executeOrder(@PathVariable Long orderId) {
        try {
            log.info("执行单个订单: orderId={}", orderId);
            
            // TODO: 实现真实的单个订单执行逻辑
            // 这里暂时只记录日志
            
            return Result.success();
        } catch (Exception e) {
            log.error("执行单个订单失败", e);
            return Result.error("执行单个订单失败：" + e.getMessage());
        }
    }

    /**
     * 获取执行统计
     */
    @GetMapping("/execution/stats")
    public Result<Map<String, Object>> getExecutionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("pending", 25);
            stats.put("executing", 8);
            stats.put("completed", 156);
            stats.put("failed", 3);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取执行统计失败", e);
            return Result.error("获取执行统计失败：" + e.getMessage());
        }
    }
} 