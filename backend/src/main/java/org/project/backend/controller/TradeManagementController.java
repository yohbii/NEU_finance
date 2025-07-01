@Data
@Schema(description = "组合调仓请求")
public class PortfolioAdjustmentRequest {
    @Schema(description = "策略ID", required = true)
    @NotNull
    private Long strategyId;
    
    @Schema(description = "目标组合ID", required = true)
    @NotNull
    private Long targetPortfolioId;
    
    @Schema(description = "执行日期", required = true)
    @FutureOrPresent
    private LocalDate executionDate;
    
    @Schema(description = "账户ID列表", required = true)
    @Size(min = 1, message = "至少需要一个账户")
    private List<String> accountIds;
    
    @Schema(description = "交易模式", example = "BATCH")
    private TradeMode tradeMode = TradeMode.BATCH;
    
    @Schema(description = "是否允许部分成交", example = "true")
    private boolean allowPartialExecution = true;
    
    @Schema(description = "滑点控制参数")
    private SlippageControl slippageControl;
    
    @Schema(description = "冲击成本限制")
    private ImpactCostLimit impactCostLimit;
}

@Data
@Schema(description = "差错处理请求")
public class ErrorHandlingRequest {
    @Schema(description = "原始订单ID", required = true)
    @NotBlank
    private String originalOrderId;
    
    @Schema(description = "错误类型", required = true, example = "INSUFFICIENT_BALANCE")
    private TradeErrorType errorType;
    
    @Schema(description = "错误详情")
    private String errorDetails;
    
    @Schema(description = "处理方式", required = true, example = "REPLACE_FUND")
    private ErrorHandlingMethod handlingMethod;
    
    @Schema(description = "替代基金代码")
    private String alternativeFundCode;
    
    @Schema(description = "是否重试", example = "false")
    private boolean retryOriginal;
    
    @Schema(description = "重试次数限制", example = "1")
    @Min(0) @Max(3)
    private int retryLimit;
}

@Data
@Schema(description = "交易订单视图")
public class TradeOrderVO {
    @Schema(description = "订单ID", example = "TO-20230501-001")
    private String orderId;
    
    @Schema(description = "账户ID", example = "ACCT-10001")
    private String accountId;
    
    @Schema(description = "基金代码", example = "F00001")
    private String fundCode;
    
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
    
    @Schema(description = "下单时间")
    private LocalDateTime orderTime;
    
    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdated;
    
    @Schema(description = "关联策略ID", example = "12345")
    private Long strategyId;
}

@Data
@Schema(description = "交割单视图")
public class SettlementVO {
    @Schema(description = "交割单ID", example = "SETTLE-20230501-001")
    private String settlementId;
    
    @Schema(description = "交易订单ID", example = "TO-20230501-001")
    private String tradeOrderId;
    
    @Schema(description = "账户ID", example = "ACCT-10001")
    private String accountId;
    
    @Schema(description = "基金代码", example = "F00001")
    private String fundCode;
    
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
}

@Data
@Schema(description = "交易监控仪表盘")
public class TradeMonitoringDashboard {
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
}

@Data
@Schema(description = "账户调仓响应")
public class AccountRebalanceResponse {
    @Schema(description = "调仓ID", example = "AR-20230501-001")
    private String rebalanceId;
    
    @Schema(description = "账户ID", example = "ACCT-10001")
    private String accountId;
    
    @Schema(description = "原持仓价值", example = "500000.00")
    private BigDecimal originalValue;
    
    @Schema(description = "目标持仓价值", example = "520000.00")
    private BigDecimal targetValue;
    
    @Schema(description = "生成订单数", example = "8")
    private int generatedOrders;
    
    @Schema(description = "预计交易成本", example = "250.00")
    private BigDecimal estimatedCost;
    
    @Schema(description = "预计执行时间")
    private LocalDateTime estimatedExecutionTime;
    
    @Schema(description = "调仓详情")
    private List<RebalanceDetail> details;
}

@Data
@Schema(description = "订单执行响应")
public class OrderExecutionResponse {
    @Schema(description = "订单ID", example = "TO-20230501-001")
    private String orderId;
    
    @Schema(description = "执行状态", example = "SENT")
    private ExecutionStatus status;
    
    @Schema(description = "交易所确认号")
    private String exchangeConfirmation;
    
    @Schema(description = "预计结算日期")
    private LocalDate estimatedSettlementDate;
    
    @Schema(description = "执行详情")
    private List<ExecutionDetail> executionDetails;
    
    @Schema(description = "警告信息")
    private List<String> warnings;
}