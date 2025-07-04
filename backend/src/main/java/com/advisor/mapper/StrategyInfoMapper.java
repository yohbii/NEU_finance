package com.advisor.mapper;

import com.advisor.entity.StrategyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 策略信息Mapper接口
 */
@Mapper
public interface StrategyInfoMapper {
    
    /**
     * 查询策略列表
     */
    List<StrategyInfo> findList(@Param("strategyType") String strategyType,
                               @Param("keyword") String keyword,
                               @Param("offset") Integer offset,
                               @Param("limit") Integer limit);
    
    /**
     * 查询策略总数
     */
    Long countList(@Param("strategyType") String strategyType,
                   @Param("keyword") String keyword);
    
    /**
     * 根据ID查询策略
     */
    StrategyInfo findById(@Param("id") Long id);
    
    /**
     * 根据策略代码查询策略
     */
    StrategyInfo findByStrategyCode(@Param("strategyCode") String strategyCode);
    
    /**
     * 插入策略
     */
    int insert(StrategyInfo strategyInfo);
    
    /**
     * 更新策略
     */
    int update(StrategyInfo strategyInfo);
    
    /**
     * 删除策略
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询所有策略类型
     */
    List<String> findAllStrategyTypes();

    /**
     * 统计策略总数
     */
    Long countAll();

    /**
     * 获取最近创建的策略
     */
    List<Map<String, Object>> getRecentStrategies(@Param("limit") Integer limit);

    // Methods for ChatService
    StrategyInfo findByName(@Param("strategyName") String strategyName);
    List<StrategyInfo> findAll();
} 