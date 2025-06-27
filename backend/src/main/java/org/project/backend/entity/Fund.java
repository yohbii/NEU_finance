package org.project.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Fund {
    private String fundCode;            // 基金代码
    private String fundName;            // 基金名称
    private Integer companyId;          // 所属公司ID
    private Integer managerId;          // 基金经理ID
    private String fundType;            // 基金类型
    private LocalDate inceptionDate;    // 成立日期
    private BigDecimal latestNav;       // 最新单位净值
    private LocalDate navDate;          // 净值对应日期
    private String riskLevel;           // 风险等级
}