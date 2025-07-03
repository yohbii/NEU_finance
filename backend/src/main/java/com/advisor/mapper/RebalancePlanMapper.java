package com.advisor.mapper;

import com.advisor.entity.RebalancePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 调仓计划Mapper接口
 */
@Mapper
public interface RebalancePlanMapper {
    
    /**
     * 查询调仓计划列表
     */
    List<RebalancePlan> findList(@Param("portfolioId") Long portfolioId,
                                @Param("status") Integer status,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);
    
    /**
     * 查询调仓计划总数
     */
    Long countList(@Param("portfolioId") Long portfolioId,
                   @Param("status") Integer status,
                   @Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);
    
    /**
     * 根据ID查询调仓计划
     */
    RebalancePlan findById(@Param("id") Long id);
    
    /**
     * 插入调仓计划
     */
    int insert(RebalancePlan rebalancePlan);
    
    /**
     * 更新调仓计划
     */
    int update(RebalancePlan rebalancePlan);
    
    /**
     * 删除调仓计划
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据组合ID查询最新调仓计划
     */
    RebalancePlan findLatestByPortfolioId(@Param("portfolioId") Long portfolioId);
} 