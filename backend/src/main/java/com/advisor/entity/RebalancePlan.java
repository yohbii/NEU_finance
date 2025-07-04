package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 调仓计划实体类
 */
@Data
public class RebalancePlan {
    
    private Long id;
    private String planNo;
    private Long portfolioId;
    private Long strategyId;
    private LocalDate planDate;
    private LocalDate executeDate;
    private BigDecimal totalAmount;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 