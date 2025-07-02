package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TradeRebalanceTask {
    private Long id;
    private Long comboId;           // 关联的组合产品ID
    private String type;            // 调仓任务类型（批量/单户/差错）: BATCH/ACCOUNT/ERROR
    private String status;          // 状态（INIT/IN_PROGRESS/COMPLETED/FAILED）
    private Date createTime;
    private Date finishTime;
    private String remark;
}