package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金信息实体类
 */
@Data
public class FundInfo {
    
    private Long id;
    private String fundCode;
    private String fundName;
    private String fundType;
    private String fundCompany;
    private String fundManager;
    private LocalDate establishDate;
    private BigDecimal unitNetValue;
    private BigDecimal accumulatedNetValue;
    private Integer riskLevel;
    private BigDecimal minInvestment;
    private BigDecimal managementFee;
    private Integer status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 