package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TradeOrder {
    private Long id;
    private Long taskId;            // 关联调仓任务
    private Long accountId;         // 财富账户ID
    private String fundCode;        // 基金代码
    private String tradeType;       // BUY/SELL
    private Double tradeAmount;     // 金额或份额
    private String status;          // 待下单/已下单/已完成/失败/回撤 ROLLBACK
    private String failReason;      // 差错描述（如失败/退单等）
    private Date createTime;
    private Date updateTime;
}