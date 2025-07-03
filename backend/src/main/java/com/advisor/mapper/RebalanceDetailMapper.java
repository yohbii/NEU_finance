package com.advisor.mapper;

import com.advisor.entity.RebalanceDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 调仓明细Mapper接口
 */
@Mapper
public interface RebalanceDetailMapper {
    
    /**
     * 查询调仓明细列表
     */
    List<RebalanceDetail> findList(@Param("planId") Long planId,
                                  @Param("assetCode") String assetCode,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    
    /**
     * 查询调仓明细总数
     */
    Long countList(@Param("planId") Long planId,
                   @Param("assetCode") String assetCode);
    
    /**
     * 根据ID查询调仓明细
     */
    RebalanceDetail findById(@Param("id") Long id);
    
    /**
     * 根据计划ID查询所有明细
     */
    List<RebalanceDetail> findByPlanId(@Param("planId") Long planId);
    
    /**
     * 插入调仓明细
     */
    int insert(RebalanceDetail rebalanceDetail);
    
    /**
     * 更新调仓明细
     */
    int update(RebalanceDetail rebalanceDetail);
    
    /**
     * 删除调仓明细
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据计划ID删除所有明细
     */
    int deleteByPlanId(@Param("planId") Long planId);
    
    /**
     * 批量插入调仓明细
     */
    int batchInsert(@Param("list") List<RebalanceDetail> rebalanceDetails);
} 