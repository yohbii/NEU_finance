package org.project.backend.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Manager {
    private Integer managerId;         // 基金经理ID
    private String managerName;        // 基金经理姓名
    private Integer birthYear;         // 出生年份
    private Integer experienceYears;   // 从业年限
    private BigDecimal totalAum;       // 管理总资产(亿元)
}