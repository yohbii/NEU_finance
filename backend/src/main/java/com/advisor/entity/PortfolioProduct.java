package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 组合产品实体类
 */
@Data
public class PortfolioProduct {
    
    private Long id;
    private String productCode;
    private String productName;
    private String productType;
    private Long strategyId;
    private Integer riskLevel;
    private BigDecimal minInvestment;
    private BigDecimal managementFee;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 