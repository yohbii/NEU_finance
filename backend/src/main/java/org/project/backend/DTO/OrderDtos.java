package org.project.backend.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OrderDtos {

    @Data
    @Schema(description = "交易订单查询条件")
    public static class TradeOrderQuery {
        @Schema(description = "订单状态", example = "PENDING")
        private TradeOrderStatus status;
        
        @Schema(description = "订单类型", example = "BUY")
        private TradeOrderType type;
        
        @Schema(description = "账户ID", example = "ACCT-1001")
        private String accountId;
        
        @Schema(description = "策略ID", example = "12345")
        private Long strategyId;
        
        @Schema(description = "开始日期", example = "2023-01-01")
        private LocalDate startDate;
        
        @Schema(description = "结束日期", example = "2023-12-31")
        private LocalDate endDate;
        
        @Schema(description = "基金代码", example = "F00001")
        private String fundCode;
        
        @Schema(description = "最小金额", example = "1000.00")
        private BigDecimal minAmount;
        
        @Schema(description = "最大金额", example = "100000.00")
        private BigDecimal maxAmount;
    }

    @Data
    @Schema(description = "交易订单视图")
    public static class TradeOrderVO {
        @Schema(description = "订单ID", example = "TO-20230501-001")
        private String orderId;
        
        @Schema(description = "账户ID", example = "ACCT-10001")
        private String accountId;
        
        @Schema(description = "基金代码", example = "F00001")
        private String fundCode;
        
        @Schema(description = "基金名称", example = "华夏成长混合")
        private String fundName;
        
        @Schema(description = "交易方向", example = "BUY")
        private TradeDirection direction;
        
        @Schema(description = "订单类型", example = "LIMIT")
        private TradeOrderType orderType;
        
        @Schema(description = "订单状态", example = "PENDING")
        private TradeOrderStatus status;
        
        @Schema(description = "下单金额", example = "50000.00")
        private BigDecimal orderAmount;
        
        @Schema(description = "下单份额", example = "1000.00")
        private BigDecimal orderUnits;
        
        @Schema(description = "下单价格", example = "1.2345")
        private BigDecimal orderPrice;
        
        @Schema(description = "已执行份额", example = "500.00")
        private BigDecimal executedUnits;
        
        @Schema(description = "执行进度", example = "50.0")
        private BigDecimal executionProgress;
        
        @Schema(description = "下单时间")
        private LocalDateTime orderTime;
        
        @Schema(description = "最后更新时间")
        private LocalDateTime lastUpdated;
        
        @Schema(description = "关联策略ID", example = "12345")
        private Long strategyId;
        
        @Schema(description = "策略名称", example = "稳健增值策略")
        private String strategyName;
        
        @Schema(description = "错误代码", example = "INSUFFICIENT_BALANCE")
        private String errorCode;
    }

    @Data
    @Schema(description = "交易订单详情视图")
    public static class TradeOrderDetailVO extends TradeOrderVO {
        @Schema(description = "客户姓名", example = "张三")
        private String clientName;
        
        @Schema(description = "客户风险等级", example = "MODERATE")
        private RiskLevel clientRiskLevel;
        
        @Schema(description = "执行详情")
        private List<ExecutionDetail> executionDetails;
        
        @Schema(description = "手续费详情")
        private FeeBreakdown feeBreakdown;
        
        @Schema(description = "关联的交割单ID", example = "SETTLE-20230501-001")
        private String settlementId;
        
        @Schema(description = "订单历史记录")
        private List<OrderStatusHistory> statusHistory;
        
        @Schema(description = "备注")
        private String remarks;
    }

    @Data
    @Schema(description = "执行详情")
    public static class ExecutionDetail {
        @Schema(description = "执行序号", example = "1")
        private int sequence;
        
        @Schema(description = "执行时间")
        private LocalDateTime executionTime;
        
        @Schema(description = "执行价格", example = "1.2350")
        private BigDecimal executedPrice;
        
        @Schema(description = "执行份额", example = "200.00")
        private BigDecimal executedUnits;
        
        @Schema(description = "执行金额", example = "247.00")
        private BigDecimal executedAmount;
        
        @Schema(description = "交易所", example = "SSE")
        private String exchange;
        
        @Schema(description = "交易通道", example = "PRIMARY")
        private String tradingChannel;
    }

    @Data
    @Schema(description = "撤销原因请求")
    public static class RevocationReasonRequest {
        @Schema(description = "撤销原因", required = true, example = "MARKET_CONDITION_CHANGE")
        @NotBlank
        private String reason;
        
        @Schema(description = "详细说明", example = "市场波动过大，暂缓交易")
        private String details;
        
        @Schema(description = "是否永久撤销", example = "false")
        private boolean permanentRevoke = false;
        
        @Schema(description = "计划重新提交时间")
        private LocalDateTime resubmitTime;
    }

    @Data
    @Schema(description = "订单撤销响应")
    public static class OrderRevocationResponse {
        @Schema(description = "订单ID", example = "TO-20230501-001")
        private String orderId;
        
        @Schema(description = "撤销状态", example = "REVOKED")
        private RevocationStatus status;
        
        @Schema(description = "撤销时间")
        private LocalDateTime revokedAt;
        
        @Schema(description = "撤销原因", example = "MARKET_CONDITION_CHANGE")
        private String reason;
        
        @Schema(description = "是否可重新提交", example = "true")
        private boolean resubmittable;
    }

    @Data
    @Schema(description = "订单拆分请求")
    public static class OrderSplittingRequest {
        @Schema(description = "拆分方式", required = true, example = "EQUAL_AMOUNT")
        @NotNull
        private SplitMethod splitMethod;
        
        @Schema(description = "拆分数量", required = true, example = "3")
        @Min(2) @Max(10)
        private int splitCount;
        
        @Schema(description = "拆分参数")
        private Map<String, String> splitParameters;
        
        @Schema(description = "执行间隔(分钟)", example = "30")
        @Min(5) @Max(240)
        private int executionInterval;
        
        @Schema(description = "价格策略", example = "MARKET")
        private PriceStrategy priceStrategy;
    }

    @Data
    @Schema(description = "订单拆分响应")
    public static class OrderSplittingResponse {
        @Schema(description = "原始订单ID", example = "TO-20230501-001")
        private String originalOrderId;
        
        @Schema(description = "拆分后订单列表")
        private List<SplitOrderDetail> splitOrders;
        
        @Schema(description = "预计总执行时间(分钟)", example = "90")
        private int estimatedTotalExecutionTime;
        
        @Schema(description = "预计成本变化", example = "15.00")
        private BigDecimal estimatedCostChange;
    }

    @Data
    @Schema(description = "拆分订单详情")
    public static class SplitOrderDetail {
        @Schema(description = "新订单ID", example = "TO-20230501-002")
        private String newOrderId;
        
        @Schema(description = "分配份额", example = "333.33")
        private BigDecimal allocatedUnits;
        
        @Schema(description = "分配金额", example = "16666.67")
        private BigDecimal allocatedAmount;
        
        @Schema(description = "计划执行时间")
        private LocalDateTime scheduledExecutionTime;
        
        @Schema(description = "执行优先级", example = "1")
        private int executionPriority;
    }

    @Data
    @Schema(description = "订单执行响应")
    public static class OrderExecutionResponse {
        @Schema(description = "订单ID", example = "TO-20230501-001")
        private String orderId;
        
        @Schema(description = "执行状态", example = "SENT")
        private ExecutionStatus status;
        
        @Schema(description = "交易所确认号", example = "EX-123456789")
        private String exchangeConfirmation;
        
        @Schema(description = "预计结算日期", example = "2023-05-03")
        private LocalDate estimatedSettlementDate;
        
        @Schema(description = "执行详情")
        private List<ExecutionDetail> executionDetails;
        
        @Schema(description = "警告信息")
        private List<String> warnings;
        
        @Schema(description = "实际执行均价", example = "1.2348")
        private BigDecimal actualAveragePrice;
        
        @Schema(description = "总执行金额", example = "1234.80")
        private BigDecimal totalExecutedAmount;
    }

    @Data
    @Schema(description = "交割单查询条件")
    public static class SettlementQuery {
        @Schema(description = "账户ID", example = "ACCT-1001")
        private String accountId;
        
        @Schema(description = "基金代码", example = "F00001")
        private String fundCode;
        
        @Schema(description = "交易方向", example = "BUY")
        private TradeDirection direction;
        
        @Schema(description = "开始日期", example = "2023-01-01")
        private LocalDate startDate;
        
        @Schema(description = "结束日期", example = "2023-12-31")
        private LocalDate endDate;
        
        @Schema(description = "结算状态", example = "CONFIRMED")
        private SettlementStatus status;
        
        @Schema(description = "最小金额", example = "1000.00")
        private BigDecimal minAmount;
        
        @Schema(description = "最大金额", example = "100000.00")
        private BigDecimal maxAmount;
        
        @Schema(description = "是否已对账", example = "true")
        private Boolean reconciled;
    }

    @Data
    @Schema(description = "交割单视图")
    public static class SettlementVO {
        @Schema(description = "交割单ID", example = "SETTLE-20230501-001")
        private String settlementId;
        
        @Schema(description = "交易订单ID", example = "TO-20230501-001")
        private String tradeOrderId;
        
        @Schema(description = "账户ID", example = "ACCT-10001")
        private String accountId;
        
        @Schema(description = "客户姓名", example = "张三")
        private String clientName;
        
        @Schema(description = "基金代码", example = "F00001")
        private String fundCode;
        
        @Schema(description = "基金名称", example = "华夏成长混合")
        private String fundName;
        
        @Schema(description = "交易方向", example = "BUY")
        private TradeDirection direction;
        
        @Schema(description = "成交金额", example = "49500.00")
        private BigDecimal executedAmount;
        
        @Schema(description = "成交份额", example = "1000.00")
        private BigDecimal executedUnits;
        
        @Schema(description = "成交价格", example = "1.2300")
        private BigDecimal executedPrice;
        
        @Schema(description = "成交时间")
        private LocalDateTime executionTime;
        
        @Schema(description = "结算状态", example = "CONFIRMED")
        private SettlementStatus status;
        
        @Schema(description = "结算日期")
        private LocalDate settlementDate;
        
        @Schema(description = "手续费", example = "50.00")
        private BigDecimal commission;
        
        @Schema(description = "印花税", example = "0.00")
        private BigDecimal stampDuty;
        
        @Schema(description = "总费用", example = "50.00")
        private BigDecimal totalFees;
        
        @Schema(description = "净结算金额", example = "49550.00")
        private BigDecimal netAmount;
    }

    @Data
    @Schema(description = "交割单详情视图")
    public static class SettlementDetailVO extends SettlementVO {
        @Schema(description = "交易详情")
        private List<ExecutionDetail> executionDetails;
        
        @Schema(description = "费用明细")
        private FeeBreakdown feeBreakdown;
        
        @Schema(description = "账户信息")
        private AccountInfo accountInfo;
        
        @Schema(description = "资金账户余额变化")
        private CashBalanceChange cashBalanceChange;
        
        @Schema(description = "持仓变化")
        private HoldingChange holdingChange;
        
        @Schema(description = "关联策略信息")
        private StrategyInfo strategyInfo;
        
        @Schema(description = "对账状态")
        private ReconciliationStatus reconciliationStatus;
        
        @Schema(description = "对账差异")
        private List<ReconciliationDiscrepancy> discrepancies;
        
        @Schema(description = "确认信息")
        private ConfirmationInfo confirmationInfo;
    }

    @Data
    @Schema(description = "交割确认信息")
    public static class SettlementConfirmation {
        @Schema(description = "交割单ID", example = "SETTLE-20230501-001")
        private String settlementId;
        
        @Schema(description = "确认状态", example = "CONFIRMED")
        private ConfirmationStatus status;
        
        @Schema(description = "确认时间")
        private LocalDateTime confirmationTime;
        
        @Schema(description = "确认人", example = "user123")
        private String confirmedBy;
        
        @Schema(description = "交易参考号", example = "TRX-987654321")
        private String transactionReference;
        
        @Schema(description = "托管行确认号", example = "CUST-123456")
        private String custodianConfirmation;
    }

    @Data
    @Schema(description = "对账结果")
    public static class ReconciliationResult {
        @Schema(description = "交割单ID", example = "SETTLE-20230501-001")
        private String settlementId;
        
        @Schema(description = "对账日期")
        private LocalDate reconciliationDate;
        
        @Schema(description = "对账状态", example = "MATCHED")
        private ReconciliationStatus status;
        
        @Schema(description = "差异列表")
        private List<ReconciliationDiscrepancy> discrepancies;
        
        @Schema(description = "对账人", example = "recon_user")
        private String reconciledBy;
        
        @Schema(description = "对账时间")
        private LocalDateTime reconciledAt;
        
        @Schema(description = "解决措施")
        private String resolutionAction;
        
        @Schema(description = "是否已解决", example = "true")
        private boolean resolved;
    }

    @Data
    @Schema(description = "对账差异")
    public static class ReconciliationDiscrepancy {
        @Schema(description = "差异类型", example = "AMOUNT_MISMATCH")
        private DiscrepancyType type;
        
        @Schema(description = "差异字段")
        private String field;
        
        @Schema(description = "系统值")
        private String systemValue;
        
        @Schema(description = "外部值")
        private String externalValue;
        
        @Schema(description = "差异描述", example = "金额不一致：系统49500.00 vs 外部49550.00")
        private String description;
        
        @Schema(description = "严重程度", example = "MEDIUM")
        private SeverityLevel severity;
    }

    @Data
    @Schema(description = "交易监控配置")
    public static class TradeMonitorConfig {
        @Schema(description = "监控类型", required = true, example = "REAL_TIME")
        @NotNull
        private MonitorType monitorType;
        
        @Schema(description = "监控频率(秒)", example = "30")
        @Min(5) @Max(300)
        private int refreshInterval = 30;
        
        @Schema(description = "监控账户列表")
        private List<String> accountIds;
        
        @Schema(description = "监控策略列表")
        private List<Long> strategyIds;
        
        @Schema(description = "监控基金列表")
        private List<String> fundCodes;
        
        @Schema(description = "告警阈值")
        private Map<AlertType, BigDecimal> alertThresholds;
        
        @Schema(description = "是否包含历史数据", example = "true")
        private boolean includeHistoricalData = true;
        
        @Schema(description = "历史数据天数", example = "7")
        @Min(1) @Max(30)
        private int historicalDataDays = 7;
    }

    @Data
    @Schema(description = "交易监控仪表盘")
    public static class TradeMonitoringDashboard {
        @Schema(description = "监控开始时间")
        private LocalDateTime startTime;
        
        @Schema(description = "监控结束时间")
        private LocalDateTime endTime;
        
        @Schema(description = "总订单数", example = "1250")
        private int totalOrders;
        
        @Schema(description = "待处理订单", example = "150")
        private int pendingOrders;
        
        @Schema(description = "异常订单", example = "25")
        private int abnormalOrders;
        
        @Schema(description = "执行中订单", example = "75")
        private int executingOrders;
        
        @Schema(description = "实时异常列表")
        private List<RealTimeTradeAlert> tradeAlerts;
        
        @Schema(description = "性能指标")
        private TradeExecutionMetrics executionMetrics;
        
        @Schema(description = "市场冲击分析")
        private MarketImpactAnalysis marketImpact;
        
        @Schema(description = "滑点分析")
        private SlippageAnalysis slippageAnalysis;
        
        @Schema(description = "成本分析")
        private CostAnalysis costAnalysis;
        
        @Schema(description = "风险暴露")
        private RiskExposure riskExposure;
    }

    @Data
    @Schema(description = "实时交易告警")
    public static class RealTimeTradeAlert {
        @Schema(description = "告警ID", example = "ALERT-20230501-001")
        private String alertId;
        
        @Schema(description = "告警类型", example = "SLIPPAGE_EXCEEDED")
        private AlertType alertType;
        
        @Schema(description = "关联订单ID", example = "TO-20230501-001")
        private String relatedOrderId;
        
        @Schema(description = "账户ID", example = "ACCT-1001")
        private String accountId;
        
        @Schema(description = "基金代码", example = "F00001")
        private String fundCode;
        
        @Schema(description = "严重程度", example = "HIGH")
        private SeverityLevel severity;
        
        @Schema(description = "告警时间")
        private LocalDateTime alertTime;
        
        @Schema(description = "当前值", example = "0.65")
        private BigDecimal currentValue;
        
        @Schema(description = "阈值", example = "0.50")
        private BigDecimal threshold;
        
        @Schema(description = "建议操作", example = "PAUSE_EXECUTION")
        private String recommendedAction;
    }
}