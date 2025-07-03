package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 */
@Data
public class TradeRecord {
    
    private Long id;
    private String tradeNo;
    private Long portfolioId;
    private Long fundId;
    private String assetCode; // 基金代码，用于显示
    private Integer tradeType;
    private BigDecimal tradeAmount;
    private BigDecimal tradeShares;
    private BigDecimal tradePrice;
    private BigDecimal tradeFee;
    private LocalDate tradeDate;
    private Integer tradeStatus;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 