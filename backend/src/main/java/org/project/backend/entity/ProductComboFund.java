package org.project.backend.entity;

import lombok.Data;

@Data
public class ProductComboFund {
    private Long id;
    private Long comboId;        // 所属组合ID
    private String fundCode;     // 基金代码
    private String fundName;     // 基金名称
    private Double weight;       // 权重
    private Double position;     // 持仓金额或份数
}