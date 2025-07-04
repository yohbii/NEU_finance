package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.FundInfo;
import com.advisor.entity.FundPerformance;
import com.advisor.service.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 基金控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    /**
     * 分页查询基金列表
     */
    @GetMapping
    public Result<PageResult<FundInfo>> getFundList(
            @RequestParam(value = "fundType", required = false) String fundType,
            @RequestParam(value = "fundCompany", required = false) String fundCompany,
            @RequestParam(value = "fundManager", required = false) String fundManager,
            @RequestParam(value = "riskLevel", required = false) Integer riskLevel,
            @RequestParam(value = "minInvestment", required = false) java.math.BigDecimal minInvestment,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        
        try {
            PageResult<FundInfo> result = fundService.getFundList(fundType, fundCompany, fundManager, riskLevel, minInvestment, keyword, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询基金列表失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取基金筛选选项
     */
    @GetMapping("/options")
    public Result<Map<String, List<String>>> getFundOptions() {
        try {
            List<String> fundTypes = fundService.getAllFundTypes();
            List<String> fundCompanies = fundService.getAllFundCompanies();
            
            Map<String, List<String>> options = Map.of(
                "fundTypes", fundTypes,
                "fundCompanies", fundCompanies
            );
            
            return Result.success(options);
        } catch (Exception e) {
            log.error("获取基金筛选选项失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据基金代码获取基金详情
     */
    @GetMapping("/code/{fundCode}")
    public Result<FundInfo> getFundByCode(@PathVariable String fundCode) {
        try {
            FundInfo fundInfo = fundService.getFundByCode(fundCode);
            return Result.success(fundInfo);
        } catch (Exception e) {
            log.error("获取基金详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有基金公司
     */
    @GetMapping("/companies")
    public Result<List<String>> getFundCompanies() {
        try {
            List<String> companies = fundService.getAllFundCompanies();
            return Result.success(companies);
        } catch (Exception e) {
            log.error("获取基金公司失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有基金经理
     */
    @GetMapping("/managers")
    public Result<List<String>> getFundManagers() {
        try {
            List<String> managers = fundService.getAllFundManagers();
            return Result.success(managers);
        } catch (Exception e) {
            log.error("获取基金经理失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取基金详情
     */
    @GetMapping("/{id}")
    public Result<FundInfo> getFundById(@PathVariable Long id) {
        try {
            FundInfo fundInfo = fundService.getFundById(id);
            return Result.success(fundInfo);
        } catch (Exception e) {
            log.error("获取基金详情失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取基金业绩数据
     */
    @GetMapping("/{id}/performance")
    public Result<FundPerformance> getFundPerformance(@PathVariable Long id) {
        try {
            FundPerformance performance = fundService.getFundPerformance(id);
            return Result.success(performance);
        } catch (Exception e) {
            log.error("获取基金业绩失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取基金详细信息（包含业绩）
     */
    @GetMapping("/{id}/detail")
    public Result<Map<String, Object>> getFundDetail(@PathVariable Long id) {
        try {
            Map<String, Object> detail = fundService.getFundDetail(id);
            return Result.success(detail);
        } catch (Exception e) {
            log.error("获取基金详细信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取基金净值数据
     */
    @GetMapping("/{id}/net-value")
    public Result<List<Map<String, Object>>> getFundNetValue(@PathVariable Long id,
                                                            @RequestParam(value = "period", defaultValue = "1Y") String period) {
        try {
            List<Map<String, Object>> netValues = fundService.getFundNetValue(id, period);
            return Result.success(netValues);
        } catch (Exception e) {
            log.error("获取基金净值数据失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建基金
     */
    @PostMapping
    public Result<Long> createFund(@RequestBody FundInfo fundInfo) {
        try {
            Long fundId = fundService.createFund(fundInfo);
            return Result.success("创建基金成功", fundId);
        } catch (Exception e) {
            log.error("创建基金失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新基金
     */
    @PutMapping("/{id}")
    public Result<Void> updateFund(@PathVariable Long id, @RequestBody FundInfo fundInfo) {
        try {
            fundInfo.setId(id);
            fundService.updateFund(fundInfo);
            return Result.success("更新基金成功");
        } catch (Exception e) {
            log.error("更新基金失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除基金
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFund(@PathVariable Long id) {
        try {
            fundService.deleteFund(id);
            return Result.success("删除基金成功");
        } catch (Exception e) {
            log.error("删除基金失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
} 