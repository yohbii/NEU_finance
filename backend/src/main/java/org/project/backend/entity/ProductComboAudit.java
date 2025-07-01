package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ProductComboAudit {
    private Long id;
    private Long comboId;            // 审核的组合id
    private Date auditTime;          // 审核时间
    private Long auditorId;          // 审核人用户ID
    private String auditStatus;      // 审核状态: PENDING/APPROVED/REJECTED
    private String auditComment;     // 审核意见或备注
}