package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TradeOrderError {
    private Long id;
    private Long orderId;         // 关联调仓交易单
    private Long accountId;       // 财富账户
    private String failType;      // 如调仓失败/账户受限
    private String failReason;    // 差错原因
    private String processStatus; // 未处理/已替换/已重下单
    private String newFundCode;   // 替换的新标的(可选)
    private Date createTime;
    private Date updateTime;
}