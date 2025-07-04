package com.advisor.mapper;

import com.advisor.entity.FundPerformance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 基金业绩Mapper接口
 */
@Mapper
public interface FundPerformanceMapper {
    
    /**
     * 查询基金业绩列表
     */
    List<FundPerformance> findList(@Param("fundCode") String fundCode,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    
    /**
     * 查询基金业绩总数
     */
    Long countList(@Param("fundCode") String fundCode);
    
    /**
     * 根据ID查询基金业绩
     */
    FundPerformance findById(@Param("id") Long id);
    
    /**
     * 根据基金代码查询最新业绩
     */
    FundPerformance findLatestByFundCode(@Param("fundCode") String fundCode);
    
    /**
     * 插入基金业绩
     */
    int insert(FundPerformance fundPerformance);
    
    /**
     * 更新基金业绩
     */
    int update(FundPerformance fundPerformance);
    
    /**
     * 删除基金业绩
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据基金代码删除业绩数据
     */
    int deleteByFundCode(@Param("fundCode") String fundCode);

    /**
     * 获取指定月份的平均收益率
     */
    Double getAvgReturnByMonth(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 获取当前风险指标
     */
    Map<String, Object> getCurrentRiskMetrics();

    // Method for ChatService
    FundPerformance findLatestByFundId(@Param("fundId") Long fundId);
} 