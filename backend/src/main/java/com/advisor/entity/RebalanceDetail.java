package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 调仓明细实体类
 */
@Data
public class RebalanceDetail {
    
    private Long id;
    private Long planId;
    private String assetCode;
    private String assetName;
    private BigDecimal currentWeight;
    private BigDecimal targetWeight;
    private BigDecimal currentAmount;
    private BigDecimal targetAmount;
    private BigDecimal adjustAmount;
    private Integer adjustType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 