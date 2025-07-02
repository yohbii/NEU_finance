package org.project.backend.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PortfolioDtos {

    @Data
    @Schema(description = "组合调仓请求")
    public static class PortfolioAdjustmentRequest {
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

    @Data
    @Schema(description = "滑点控制参数")
    public static class SlippageControl {
        @Schema(description = "最大允许滑点", example = "0.5")
        @DecimalMin(value = "0.0", message = "滑点不能为负")
        private BigDecimal maxSlippage;
        
        @Schema(description = "滑点控制算法", example = "VWAP")
        private SlippageAlgorithm algorithm;
        
        @Schema(description = "市场参与率", example = "20.0")
        @DecimalMin(value = "0.0", message = "参与率不能为负")
        @DecimalMax(value = "100.0", message = "参与率不能超过100%")
        private BigDecimal marketParticipationRate;
    }

    @Data
    @Schema(description = "冲击成本限制")
    public static class ImpactCostLimit {
        @Schema(description = "最大冲击成本", example = "0.3")
        @DecimalMin(value = "0.0", message = "冲击成本不能为负")
        private BigDecimal maxImpactCost;
        
        @Schema(description = "流动性阈值", example = "0.2")
        @DecimalMin(value = "0.0", message = "流动性阈值不能为负")
        private BigDecimal liquidityThreshold;
    }

    @Data
    @Schema(description = "交易执行响应")
    public static class TradeExecutionResponse {
        @Schema(description = "调仓ID", example = "ADJ-20230501-001")
        private String adjustmentId;
        
        @Schema(description = "生成订单数", example = "15")
        private int generatedOrderCount;
        
        @Schema(description = "预计交易成本", example = "1250.00")
        private BigDecimal estimatedTransactionCost;
        
        @Schema(description = "账户列表")
        private List<String> accountIds;
        
        @Schema(description = "执行状态", example = "PENDING")
        private ExecutionStatus status;
        
        @Schema(description = "创建时间")
        private LocalDateTime createdAt;
        
        @Schema(description = "生成的订单ID列表")
        private List<String> orderIds;
        
        @Schema(description = "预估执行时间(分钟)", example = "45")
        private Integer estimatedExecutionMinutes;
        
        @Schema(description = "风险检查结果")
        private RiskCheckResult riskCheckResult;
    }

    @Data
    @Schema(description = "风险检查结果")
    public static class RiskCheckResult {
        @Schema(description = "是否通过", example = "true")
        private boolean passed;
        
        @Schema(description = "警告列表")
        private List<String> warnings;
        
        @Schema(description = "违反的规则")
        private List<ComplianceRuleViolation> violations;
    }

    @Data
    @Schema(description = "差错处理请求")
    public static class ErrorHandlingRequest {
        @Schema(description = "原始订单ID", required = true, example = "TO-20230501-001")
        @NotBlank
        private String originalOrderId;
        
        @Schema(description = "错误类型", required = true, example = "INSUFFICIENT_BALANCE")
        @NotNull
        private TradeErrorType errorType;
        
        @Schema(description = "错误详情", example = "账户余额不足")
        private String errorDetails;
        
        @Schema(description = "处理方式", required = true, example = "REPLACE_FUND")
        @NotNull
        private ErrorHandlingMethod handlingMethod;
        
        @Schema(description = "替代基金代码", example = "F00002")
        private String alternativeFundCode;
        
        @Schema(description = "是否重试", example = "false", defaultValue = "false")
        private boolean retryOriginal = false;
        
        @Schema(description = "重试次数限制", example = "1", defaultValue = "1")
        @Min(0) @Max(3)
        private int retryLimit = 1;
        
        @Schema(description = "替代基金选择逻辑", example = "SAME_CATEGORY")
        private FundReplacementLogic replacementLogic;
        
        @Schema(description = "最大替代基金偏差", example = "0.2")
        @DecimalMin(value = "0.0", message = "偏差不能为负")
        private BigDecimal maxDeviation;
    }

    @Data
    @Schema(description = "差错处理响应")
    public static class ErrorHandlingResponse {
        @Schema(description = "处理ID", example = "ERR-20230501-001")
        private String handlingId;
        
        @Schema(description = "原始订单ID", example = "TO-20230501-001")
        private String originalOrderId;
        
        @Schema(description = "处理结果", example = "REPLACED")
        private HandlingResult result;
        
        @Schema(description = "新订单ID", example = "TO-20230501-002")
        private String newOrderId;
        
        @Schema(description = "处理时间")
        private LocalDateTime handledAt;
        
        @Schema(description = "处理详情", example = "使用基金F00002替代原基金F00001")
        private String handlingDetails;
        
        @Schema(description = "替代基金信息")
        private FundVO alternativeFund;
        
        @Schema(description = "预计影响分析")
        private ImpactAnalysis impactAnalysis;
    }

    @Data
    @Schema(description = "影响分析")
    public static class ImpactAnalysis {
        @Schema(description = "预期收益偏差", example = "0.15")
        private BigDecimal expectedReturnDeviation;
        
        @Schema(description = "风险特征变化", example = "波动率增加0.2%")
        private String riskCharacteristicChange;
        
        @Schema(description = "相关性差异", example = "0.05")
        private BigDecimal correlationDifference;
    }

    @Data
    @Schema(description = "账户调仓请求")
    public static class AccountRebalanceRequest {
        @Schema(description = "账户ID", required = true, example = "ACCT-1001")
        @NotBlank
        private String accountId;
        
        @Schema(description = "调仓原因", required = true, example = "SPECIAL_ADJUSTMENT")
        @NotNull
        private RebalanceReason reason;
        
        @Schema(description = "基金调整列表", required = true)
        @Size(min = 1, message = "至少需要一个基金调整")
        private List<FundAdjustment> funds;
        
        @Schema(description = "现金处理方式", example = "REINVEST")
        private CashHandling cashHandling = CashHandling.REINVEST;
        
        @Schema(description = "是否保留现金余额", example = "true")
        private boolean preserveCashBalance = true;
        
        @Schema(description = "目标现金比例", example = "5.0")
        @DecimalMin(value = "0.0", message = "现金比例不能为负")
        @DecimalMax(value = "100.0", message = "现金比例不能超过100%")
        private BigDecimal targetCashPercentage;
        
        @Schema(description = "执行模式", example = "IMMEDIATE")
        private ExecutionMode executionMode = ExecutionMode.IMMEDIATE;
    }

    @Data
    @Schema(description = "基金调整")
    public static class FundAdjustment {
        @Schema(description = "基金代码", required = true, example = "F00001")
        @NotBlank
        private String fundCode;
        
        @Schema(description = "目标权重", example = "10.0")
        @DecimalMin(value = "0.0", message = "权重不能为负")
        @DecimalMax(value = "100.0", message = "权重不能超过100%")
        private BigDecimal targetWeight;
        
        @Schema(description = "目标份额", example = "1000.00")
        @DecimalMin(value = "0.0", message = "份额不能为负")
        private BigDecimal targetUnits;
        
        @Schema(description = "操作类型", example = "ADD")
        private AdjustmentOperation operation;
        
        @Schema(description = "是否允许偏差", example = "true")
        private boolean allowDeviation = true;
        
        @Schema(description = "最大偏差", example = "0.5")
        @DecimalMin(value = "0.0", message = "偏差不能为负")
        private BigDecimal maxDeviation;
    }

    @Data
    @Schema(description = "账户调仓响应")
    public static class AccountRebalanceResponse {
        @Schema(description = "调仓ID", example = "AR-20230501-001")
        private String rebalanceId;
        
        @Schema(description = "账户ID", example = "ACCT-1001")
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
        
        @Schema(description = "现金调整详情")
        private CashAdjustment cashAdjustment;
        
        @Schema(description = "风险分析摘要")
        private RiskAnalysisSummary riskAnalysis;
    }

    @Data
    @Schema(description = "调仓详情")
    public static class RebalanceDetail {
        @Schema(description = "基金代码", example = "F00001")
        private String fundCode;
        
        @Schema(description = "基金名称", example = "华夏成长混合")
        private String fundName;
        
        @Schema(description = "当前持仓", example = "5000.00")
        private BigDecimal currentUnits;
        
        @Schema(description = "目标持仓", example = "6000.00")
        private BigDecimal targetUnits;
        
        @Schema(description = "调整量", example = "1000.00")
        private BigDecimal adjustmentUnits;
        
        @Schema(description = "操作", example = "BUY")
        private TradeDirection direction;
        
        @Schema(description = "预计成交额", example = "12345.00")
        private BigDecimal estimatedAmount;
    }
}