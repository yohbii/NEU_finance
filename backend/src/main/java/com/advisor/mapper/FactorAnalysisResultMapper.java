package com.advisor.mapper;

import com.advisor.entity.FactorAnalysisResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 因子分析结果Mapper接口
 */
@Mapper
public interface FactorAnalysisResultMapper {
    
    /**
     * 插入分析结果
     */
    int insert(FactorAnalysisResult analysisResult);
    
    /**
     * 根据分析ID查询结果
     */
    FactorAnalysisResult findByAnalysisId(@Param("analysisId") String analysisId);
    
    /**
     * 更新分析结果
     */
    int updateByAnalysisId(FactorAnalysisResult analysisResult);
    
    /**
     * 根据分析ID删除结果
     */
    int deleteByAnalysisId(@Param("analysisId") String analysisId);
} 