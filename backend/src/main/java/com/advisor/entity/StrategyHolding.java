package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 策略持仓实体类
 */
@Data
public class StrategyHolding {
    
    private Long id;
    private Long strategyId;
    private String assetCode;
    private String assetName;
    private String assetType;
    private BigDecimal targetWeight;
    private BigDecimal currentWeight;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 