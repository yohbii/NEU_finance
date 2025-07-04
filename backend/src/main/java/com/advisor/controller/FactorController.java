package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.FactorInfo;
import com.advisor.service.FactorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 因子控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/factors")
@RequiredArgsConstructor
public class FactorController {

    private final FactorService factorService;

    /**
     * 分页查询因子列表
     */
    @GetMapping
    public Result<PageResult<FactorInfo>> getFactorList(
            @RequestParam(value = "factorType", required = false) String factorType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        
        try {
            PageResult<FactorInfo> result = factorService.getFactorList(factorType, category, keyword, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询因子列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取因子详情
     */
    @GetMapping("/{id}")
    public Result<FactorInfo> getFactorById(@PathVariable Long id) {
        try {
            FactorInfo factorInfo = factorService.getFactorById(id);
            return Result.success(factorInfo);
        } catch (Exception e) {
            log.error("获取因子详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据因子代码获取因子详情
     */
    @GetMapping("/code/{factorCode}")
    public Result<FactorInfo> getFactorByCode(@PathVariable String factorCode) {
        try {
            FactorInfo factorInfo = factorService.getFactorByCode(factorCode);
            return Result.success(factorInfo);
        } catch (Exception e) {
            log.error("获取因子详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建因子
     */
    @PostMapping
    public Result<Long> createFactor(@RequestBody FactorInfo factorInfo) {
        try {
            Long factorId = factorService.createFactor(factorInfo);
            return Result.success("创建因子成功", factorId);
        } catch (Exception e) {
            log.error("创建因子失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新因子
     */
    @PutMapping("/{id}")
    public Result<Void> updateFactor(@PathVariable Long id, @RequestBody FactorInfo factorInfo) {
        try {
            factorInfo.setId(id);
            factorService.updateFactor(factorInfo);
            return Result.success("更新因子成功");
        } catch (Exception e) {
            log.error("更新因子失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除因子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFactor(@PathVariable Long id) {
        try {
            factorService.deleteFactor(id);
            return Result.success("删除因子成功");
        } catch (Exception e) {
            log.error("删除因子失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取因子筛选选项
     */
    @GetMapping("/options")
    public Result<Map<String, List<String>>> getFactorOptions() {
        try {
            List<String> factorTypes = factorService.getAllFactorTypes();
            List<String> categories = factorService.getAllCategories();
            
            Map<String, List<String>> options = Map.of(
                "factorTypes", factorTypes,
                "categories", categories
            );
            
            return Result.success(options);
        } catch (Exception e) {
            log.error("获取因子筛选选项失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 执行因子分析
     */
    @PostMapping("/analysis/run")
    public Result<Map<String, Object>> runFactorAnalysis(@RequestBody Map<String, Object> analysisParams) {
        try {
            Map<String, Object> result = factorService.runFactorAnalysis(analysisParams);
            return Result.success("因子分析执行成功", result);
        } catch (Exception e) {
            log.error("执行因子分析失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取因子分析结果
     */
    @GetMapping("/analysis/result/{analysisId}")
    public Result<Map<String, Object>> getAnalysisResult(@PathVariable String analysisId) {
        try {
            Map<String, Object> result = factorService.getAnalysisResult(analysisId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取因子分析结果失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}