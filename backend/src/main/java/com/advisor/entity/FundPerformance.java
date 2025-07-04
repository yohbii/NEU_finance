package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金业绩实体类
 */
@Data
public class FundPerformance {
    
    private Long id;
    private Long fundId;
    private LocalDate performanceDate;
    private BigDecimal ytdReturn;
    private BigDecimal oneMonthReturn;
    private BigDecimal threeMonthReturn;
    private BigDecimal sixMonthReturn;
    private BigDecimal oneYearReturn;
    private BigDecimal threeYearReturn;
    private BigDecimal maxDrawdown;
    private BigDecimal sharpeRatio;
    private BigDecimal volatility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 