package org.project.backend.entity;

import lombok.Data;

@Data
public class StrategyAssetAllocation {
    private Long id;
    private Long strategyId;
    private String assetClass;  // 资产类别，如股票、债券等
    private Double weight;      // 分配权重
}