package org.project.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FundNav {
    private String fundCode;            // 基金代码
    private LocalDate navDate;          // 净值日期
    private BigDecimal navValue;        // 单位净值
}