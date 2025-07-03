package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.PortfolioProduct;
import com.advisor.mapper.PortfolioProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 组合产品服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioProductService {

    private final PortfolioProductMapper portfolioProductMapper;

    /**
     * 分页查询产品列表
     */
    public PageResult<PortfolioProduct> getProductList(String productType, Integer riskLevel, String keyword, 
                                                      Integer current, Integer size) {
        current = current == null || current < 1 ? 1 : current;
        size = size == null || size < 1 ? 20 : size;
        
        Integer offset = (current - 1) * size;
        
        List<PortfolioProduct> records = portfolioProductMapper.findList(productType, riskLevel, keyword, offset, size);
        Long total = portfolioProductMapper.countList(productType, riskLevel, keyword);
        
        return PageResult.of(records, total, current, size);
    }

    /**
     * 根据ID获取产品详情
     */
    public PortfolioProduct getProductById(Long id) {
        if (id == null) {
            throw new RuntimeException("产品ID不能为空");
        }
        
        PortfolioProduct product = portfolioProductMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }
        
        return product;
    }

    /**
     * 根据产品代码获取产品详情
     */
    public PortfolioProduct getProductByCode(String productCode) {
        if (!StringUtils.hasText(productCode)) {
            throw new RuntimeException("产品代码不能为空");
        }
        
        PortfolioProduct product = portfolioProductMapper.findByProductCode(productCode);
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }
        
        return product;
    }

    /**
     * 创建产品
     */
    public Long createProduct(PortfolioProduct portfolioProduct) {
        if (portfolioProduct == null) {
            throw new RuntimeException("产品信息不能为空");
        }
        if (!StringUtils.hasText(portfolioProduct.getProductCode())) {
            throw new RuntimeException("产品代码不能为空");
        }
        if (!StringUtils.hasText(portfolioProduct.getProductName())) {
            throw new RuntimeException("产品名称不能为空");
        }
        
        // 检查产品代码是否已存在
        PortfolioProduct existProduct = portfolioProductMapper.findByProductCode(portfolioProduct.getProductCode());
        if (existProduct != null) {
            throw new RuntimeException("产品代码已存在");
        }
        
        portfolioProduct.setStatus(1);
        
        int result = portfolioProductMapper.insert(portfolioProduct);
        if (result <= 0) {
            throw new RuntimeException("创建产品失败");
        }
        
        return portfolioProduct.getId();
    }

    /**
     * 更新产品
     */
    public void updateProduct(PortfolioProduct portfolioProduct) {
        if (portfolioProduct == null || portfolioProduct.getId() == null) {
            throw new RuntimeException("产品ID不能为空");
        }
        
        PortfolioProduct existProduct = portfolioProductMapper.findById(portfolioProduct.getId());
        if (existProduct == null) {
            throw new RuntimeException("产品不存在");
        }
        
        int result = portfolioProductMapper.update(portfolioProduct);
        if (result <= 0) {
            throw new RuntimeException("更新产品失败");
        }
    }

    /**
     * 删除产品
     */
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new RuntimeException("产品ID不能为空");
        }
        
        PortfolioProduct existProduct = portfolioProductMapper.findById(id);
        if (existProduct == null) {
            throw new RuntimeException("产品不存在");
        }
        
        int result = portfolioProductMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除产品失败");
        }
    }

    /**
     * 获取所有产品类型
     */
    public List<String> getAllProductTypes() {
        return portfolioProductMapper.findAllProductTypes();
    }
} 