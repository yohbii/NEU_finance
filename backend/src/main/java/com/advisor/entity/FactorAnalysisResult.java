package com.advisor.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 因子分析结果实体类
 */
@Data
public class FactorAnalysisResult {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 分析ID
     */
    private String analysisId;
    
    /**
     * 分析类型 correlation/effectiveness/contribution/stability
     */
    private String analysisType;
    
    /**
     * 分析名称
     */
    private String analysisName;
    
    /**
     * 分析参数(JSON字符串)
     */
    private String analysisParams;
    
    /**
     * 分析结果数据(JSON字符串)
     */
    private String resultData;
    
    /**
     * 状态 1完成 0进行中 -1失败
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 