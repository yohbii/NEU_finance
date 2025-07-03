package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 策略信息实体类
 */
@Data
public class StrategyInfo {
    
    private Long id;
    private String strategyCode;
    private String strategyName;
    private String strategyType;
    private Integer riskLevel;
    private BigDecimal targetReturn;
    private BigDecimal maxDrawdown;
    private String rebalanceFrequency;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 