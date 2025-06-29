package org.project.backend.entity;

import lombok.Data;

@Data
public class StrategyFundHolding {
    private Long id;
    private Long strategyId;
    private String fundCode;
    private Double weight;      // 占比
    private Double position;    // 持仓金额/数量
}