package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 策略回测实体类
 */
@Data
public class StrategyBacktest {
    
    private Long id;
    private Long strategyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalReturn;
    private BigDecimal annualReturn;
    private BigDecimal maxDrawdown;
    private BigDecimal sharpeRatio;
    private BigDecimal volatility;
    private String benchmarkCode;
    private String backtestResult;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 