package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 仪表板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = dashboardService.getDashboardStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取仪表板统计数据失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取净值走势数据
     */
    @GetMapping("/net-value-trend")
    public Result<Map<String, Object>> getNetValueTrend() {
        try {
            Map<String, Object> data = dashboardService.getNetValueTrend();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取净值走势数据失败", e);
            return Result.error("获取净值走势数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取资产配置数据
     */
    @GetMapping("/asset-allocation")
    public Result<List<Map<String, Object>>> getAssetAllocation() {
        try {
            List<Map<String, Object>> data = dashboardService.getAssetAllocation();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取资产配置数据失败", e);
            return Result.error("获取资产配置数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取收益分析数据
     */
    @GetMapping("/return-analysis")
    public Result<Map<String, Object>> getReturnAnalysis() {
        try {
            Map<String, Object> data = dashboardService.getReturnAnalysis();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取收益分析数据失败", e);
            return Result.error("获取收益分析数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取风险指标数据
     */
    @GetMapping("/risk-metrics")
    public Result<Map<String, Object>> getRiskMetrics() {
        try {
            Map<String, Object> data = dashboardService.getRiskMetrics();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取风险指标数据失败", e);
            return Result.error("获取风险指标数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取最新动态
     */
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> getRecentActivities() {
        try {
            List<Map<String, Object>> activities = dashboardService.getRecentActivities();
            return Result.success(activities);
        } catch (Exception e) {
            log.error("获取最新动态失败", e);
            return Result.error("获取最新动态失败：" + e.getMessage());
        }
    }
} 