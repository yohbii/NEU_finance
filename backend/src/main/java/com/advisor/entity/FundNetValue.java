package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金净值实体类
 */
@Data
public class FundNetValue {
    
    private Long id;
    private Long fundId;
    private LocalDate tradeDate;
    private BigDecimal unitNetValue;
    private BigDecimal accumulatedNetValue;
    private BigDecimal dailyReturn;
    private LocalDateTime createdAt;
} 