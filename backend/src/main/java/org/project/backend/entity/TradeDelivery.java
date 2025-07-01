package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TradeDelivery {
    private Long id;
    private Long orderId;         // 关联TradeOrder
    private Long accountId;       // 财富账户
    private String fundCode;      // 基金代码
    private String tradeType;     // BUY/SELL
    private Double tradeAmount;
    private Double confirmAmount;
    private String confirmStatus; // 成功/失败
    private Date confirmTime;
    private String message;       // 平台返回的结果信息
}