package com.advisor.mapper;

import com.advisor.entity.FundInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 基金信息Mapper接口
 */
@Mapper
public interface FundInfoMapper {
    
    /**
     * 查询基金列表
     */
    List<FundInfo> findList(@Param("fundType") String fundType,
                           @Param("fundCompany") String fundCompany,
                           @Param("fundManager") String fundManager,
                           @Param("riskLevel") Integer riskLevel,
                           @Param("minInvestment") java.math.BigDecimal minInvestment,
                           @Param("keyword") String keyword,
                           @Param("offset") Integer offset,
                           @Param("limit") Integer limit);
    
    /**
     * 查询基金总数
     */
    Long countList(@Param("fundType") String fundType,
                   @Param("fundCompany") String fundCompany,
                   @Param("fundManager") String fundManager,
                   @Param("riskLevel") Integer riskLevel,
                   @Param("minInvestment") java.math.BigDecimal minInvestment,
                   @Param("keyword") String keyword);
    
    /**
     * 根据ID查询基金
     */
    FundInfo findById(@Param("id") Long id);
    
    /**
     * 根据基金代码查询基金
     */
    FundInfo findByFundCode(@Param("fundCode") String fundCode);
    
    /**
     * 插入基金
     */
    int insert(FundInfo fundInfo);
    
    /**
     * 更新基金
     */
    int update(FundInfo fundInfo);
    
    /**
     * 删除基金
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询所有基金类型
     */
    List<String> findAllFundTypes();
    
    /**
     * 查询所有基金公司
     */
    List<String> findAllFundCompanies();
    
    /**
     * 查询所有基金经理
     */
    List<String> findAllFundManagers();

    /**
     * 统计基金总数
     */
    Long countAll();

    /**
     * 获取基金类型统计
     */
    List<Map<String, Object>> getFundTypeStats();

    // Methods for ChatService
    List<FundInfo> selectAll();
    FundInfo findByCode(@Param("fundCode") String fundCode);
    List<FundInfo> findByType(@Param("fundType") String fundType);
    List<FundInfo> searchFunds(Map<String, Object> params);
} 