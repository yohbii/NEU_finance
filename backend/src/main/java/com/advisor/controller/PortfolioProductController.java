package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.PortfolioProduct;
import com.advisor.service.PortfolioProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 组合产品控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class PortfolioProductController {

    private final PortfolioProductService portfolioProductService;

    /**
     * 分页查询产品列表
     */
    @GetMapping
    public Result<PageResult<PortfolioProduct>> getProductList(
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) Integer riskLevel,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        
        try {
            PageResult<PortfolioProduct> result = portfolioProductService.getProductList(
                productType, riskLevel, keyword, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询产品列表失败", e);
            return Result.error("查询产品列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取产品详情
     */
    @GetMapping("/{id}")
    public Result<PortfolioProduct> getProductById(@PathVariable Long id) {
        try {
            PortfolioProduct product = portfolioProductService.getProductById(id);
            return Result.success(product);
        } catch (Exception e) {
            log.error("获取产品详情失败", e);
            return Result.error("获取产品详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据产品代码获取产品详情
     */
    @GetMapping("/code/{productCode}")
    public Result<PortfolioProduct> getProductByCode(@PathVariable String productCode) {
        try {
            PortfolioProduct product = portfolioProductService.getProductByCode(productCode);
            return Result.success(product);
        } catch (Exception e) {
            log.error("获取产品详情失败", e);
            return Result.error("获取产品详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建产品
     */
    @PostMapping
    public Result<Long> createProduct(@RequestBody PortfolioProduct portfolioProduct) {
        try {
            Long productId = portfolioProductService.createProduct(portfolioProduct);
            return Result.success(productId);
        } catch (Exception e) {
            log.error("创建产品失败", e);
            return Result.error("创建产品失败：" + e.getMessage());
        }
    }

    /**
     * 更新产品
     */
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody PortfolioProduct portfolioProduct) {
        try {
            portfolioProduct.setId(id);
            portfolioProductService.updateProduct(portfolioProduct);
            return Result.success();
        } catch (Exception e) {
            log.error("更新产品失败", e);
            return Result.error("更新产品失败：" + e.getMessage());
        }
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            portfolioProductService.deleteProduct(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除产品失败", e);
            return Result.error("删除产品失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有产品类型
     */
    @GetMapping("/types")
    public Result<List<String>> getAllProductTypes() {
        try {
            List<String> types = portfolioProductService.getAllProductTypes();
            return Result.success(types);
        } catch (Exception e) {
            log.error("获取产品类型失败", e);
            return Result.error("获取产品类型失败：" + e.getMessage());
        }
    }

    /**
     * 获取产品审核列表
     */
    @GetMapping("/approval/list")
    public Result<PageResult<PortfolioProduct>> getApprovalList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        
        try {
            // 暂时返回普通产品列表，后续可以添加审核状态字段
            PageResult<PortfolioProduct> result = portfolioProductService.getProductList(
                null, null, null, current, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询审核列表失败", e);
            return Result.error("查询审核列表失败：" + e.getMessage());
        }
    }

    /**
     * 提交产品审核
     */
    @PostMapping("/approval/submit")
    public Result<Void> submitApproval(@RequestBody Map<String, Object> requestData) {
        try {
            Long productId = Long.valueOf(requestData.get("productId").toString());
            String action = requestData.get("action").toString();
            String comment = (String) requestData.get("comment");
            String rejectReason = (String) requestData.get("rejectReason");
            
            log.info("提交产品审核: productId={}, action={}, comment={}", productId, action, comment);
            
            // TODO: 实现真实的审核逻辑，更新产品状态
            // 这里暂时只记录日志
            
            return Result.success();
        } catch (Exception e) {
            log.error("提交审核失败", e);
            return Result.error("提交审核失败：" + e.getMessage());
        }
    }

    /**
     * 获取审核统计
     */
    @GetMapping("/approval/stats")
    public Result<Map<String, Object>> getApprovalStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("pending", 12);
            stats.put("approved", 45);
            stats.put("rejected", 8);
            stats.put("total", 65);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取审核统计失败", e);
            return Result.error("获取审核统计失败：" + e.getMessage());
        }
    }
} 