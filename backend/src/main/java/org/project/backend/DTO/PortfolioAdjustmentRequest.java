@Data
@Schema(description = "组合调仓请求")
public class PortfolioAdjustmentRequest {
    @Schema(description = "策略ID", required = true, example = "12345")
    @NotNull
    private Long strategyId;
    
    @Schema(description = "目标组合ID", required = true, example = "67890")
    @NotNull
    private Long targetPortfolioId;
    
    @Schema(description = "执行日期", required = true, example = "2023-12-31")
    @FutureOrPresent
    @NotNull
    private LocalDate executionDate;
    
    @Schema(description = "账户ID列表", required = true, example = "[\"ACCT-1001\", \"ACCT-1002\"]")
    @Size(min = 1, message = "至少需要一个账户")
    private List<String> accountIds;
    
    @Schema(description = "交易模式", example = "BATCH", defaultValue = "BATCH")
    @NotNull
    private TradeMode tradeMode = TradeMode.BATCH;
    
    @Schema(description = "是否允许部分成交", example = "true", defaultValue = "true")
    private boolean allowPartialExecution = true;
    
    @Schema(description = "滑点控制参数")
    private SlippageControl slippageControl;
    
    @Schema(description = "冲击成本限制")
    private ImpactCostLimit impactCostLimit;
    
    @Schema(description = "基准价格类型", example = "VWAP")
    private BenchmarkPriceType benchmarkPriceType;
    
    @Schema(description = "交易执行时段", example = "{\"start\": \"09:30\", \"end\": \"14:45\"}")
    private TradeTimeWindow tradeTimeWindow;
}