package com.advisor.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基金因子值实体类
 */
@Data
public class FundFactorValue {
    
    private Long id;
    private Long fundId;
    private Long factorId;
    private LocalDate valueDate;
    private BigDecimal factorValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 