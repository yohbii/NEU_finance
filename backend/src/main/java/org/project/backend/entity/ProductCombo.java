package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ProductCombo {
    private Long comboId;
    private String comboName;        // 组合产品名称
    private String riskLevel;        // 风险等级，如 "低", "中", "高"
    private Long strategyId;         // 关联的策略或FOF id
    private Long creatorId;          // 创建人用户ID
    private Date createTime;         // 创建时间
    private String auditStatus;      // 审核状态: PENDING/APPROVED/REJECTED
    private String productStatus;    // 产品状态: OFF_SHELF/ON_SHELF/REMOVED etc.
    private String productParams;    // 产品参数（如json格式字符串，可选）
}