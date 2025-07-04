package com.advisor.mapper;

import com.advisor.entity.PortfolioProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 组合产品Mapper接口
 */
@Mapper
public interface PortfolioProductMapper {
    
    /**
     * 查询产品列表
     */
    List<PortfolioProduct> findList(@Param("productType") String productType,
                                   @Param("riskLevel") Integer riskLevel,
                                   @Param("keyword") String keyword,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);
    
    /**
     * 查询产品总数
     */
    Long countList(@Param("productType") String productType,
                   @Param("riskLevel") Integer riskLevel,
                   @Param("keyword") String keyword);
    
    /**
     * 根据ID查询产品
     */
    PortfolioProduct findById(@Param("id") Long id);
    
    /**
     * 根据产品代码查询产品
     */
    PortfolioProduct findByProductCode(@Param("productCode") String productCode);
    
    /**
     * 插入产品
     */
    int insert(PortfolioProduct portfolioProduct);
    
    /**
     * 更新产品
     */
    int update(PortfolioProduct portfolioProduct);
    
    /**
     * 删除产品
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询所有产品类型
     */
    List<String> findAllProductTypes();

    /**
     * 统计产品总数
     */
    Long countAll();
} 