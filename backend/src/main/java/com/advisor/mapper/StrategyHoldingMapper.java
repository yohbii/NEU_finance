package com.advisor.mapper;

import com.advisor.entity.StrategyHolding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 策略持仓Mapper接口
 */
@Mapper
public interface StrategyHoldingMapper {
    
    /**
     * 根据策略ID查询持仓列表
     */
    List<StrategyHolding> findByStrategyId(@Param("strategyId") Long strategyId);
    
    /**
     * 根据ID查询持仓
     */
    StrategyHolding findById(@Param("id") Long id);
    
    /**
     * 根据策略ID和资产代码查询持仓
     */
    StrategyHolding findByStrategyIdAndAssetCode(@Param("strategyId") Long strategyId,
                                               @Param("assetCode") String assetCode);
    
    /**
     * 插入持仓
     */
    int insert(StrategyHolding strategyHolding);
    
    /**
     * 批量插入持仓
     */
    int batchInsert(@Param("list") List<StrategyHolding> list);
    
    /**
     * 更新持仓
     */
    int update(StrategyHolding strategyHolding);
    
    /**
     * 删除持仓
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据策略ID删除所有持仓
     */
    int deleteByStrategyId(@Param("strategyId") Long strategyId);
} 