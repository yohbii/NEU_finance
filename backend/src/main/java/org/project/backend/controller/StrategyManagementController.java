@RestController
@RequestMapping("/api/strategy-management")
@Tag(name = "策略管理子系统", description = "提供策略创建、回测、再平衡和监控功能")
@Slf4j
@RequiredArgsConstructor
public class StrategyManagementController {

    private final StrategyService strategyService;
    private final BacktestService backtestService;
    private final RebalanceService rebalanceService;
    private final StrategyMonitorService monitorService;

    @PostMapping("/asset-allocation")
    @Operation(summary = "创建大类资产配置策略", description = "通过四步流程创建大类资产配置策略")
    @PreAuthorize("hasRole('STRATEGIST')")
    public ResponseEntity<StrategyCreationResponse> createAssetAllocationStrategy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "资产配置策略请求",
                required = true,
                content = @Content(schema = @Schema(implementation = AssetAllocationRequest.class)))
            @Valid @RequestBody AssetAllocationRequest request) {
        
        log.info("创建大类资产配置策略: {}", request.getStrategyName());
        StrategyCreationResponse response = strategyService.createAssetAllocationStrategy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/fof-portfolios")
    @Operation(summary = "创建FOF组合", description = "创建基金中的基金投资组合")
    @PreAuthorize("hasRole('STRATEGIST')")
    public ResponseEntity<StrategyCreationResponse> createFOFPortfolio(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "FOF组合请求",
                required = true,
                content = @Content(schema = @Schema(implementation = FOFPortfolioRequest.class)))
            @Valid @RequestBody FOFPortfolioRequest request) {
        
        log.info("创建FOF组合: {}", request.getPortfolioName());
        StrategyCreationResponse response = strategyService.createFOFPortfolio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/fund-indices")
    @Operation(summary = "创建基金指数组合", description = "通过四步流程创建基金指数组合")
    @PreAuthorize("hasRole('STRATEGIST')")
    public ResponseEntity<StrategyCreationResponse> createFundIndex(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "基金指数组合请求",
                required = true,
                content = @Content(schema = @Schema(implementation = FundIndexRequest.class)))
            @Valid @RequestBody FundIndexRequest request) {
        
        log.info("创建基金指数组合: {}", request.getIndexName());
        StrategyCreationResponse response = strategyService.createFundIndex(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/timing-strategies")
    @Operation(summary = "创建择时组合", description = "通过三步流程创建择时投资组合")
    @PreAuthorize("hasRole('STRATEGIST')")
    public ResponseEntity<StrategyCreationResponse> createTimingStrategy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "择时策略请求",
                required = true,
                content = @Content(schema = @Schema(implementation = TimingStrategyRequest.class)))
            @Valid @RequestBody TimingStrategyRequest request) {
        
        log.info("创建择时策略: {}", request.getStrategyName());
        StrategyCreationResponse response = strategyService.createTimingStrategy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/strategies/{strategyId}")
    @Operation(summary = "获取策略详情", description = "查看策略持仓、调仓历史和收益情况")
    @PreAuthorize("@strategyPermissionService.hasAccess(#strategyId)")
    public ResponseEntity<StrategyDetailVO> getStrategyDetail(
            @Parameter(description = "策略ID", required = true) 
            @PathVariable Long strategyId,
            @Parameter(description = "包含持仓", example = "true") 
            @RequestParam(defaultValue = "true") boolean includeHoldings,
            @Parameter(description = "包含历史调仓", example = "true") 
            @RequestParam(defaultValue = "true") boolean includeHistory) {
        
        StrategyDetailVO detail = strategyService.getStrategyDetail(strategyId, includeHoldings, includeHistory);
        return ResponseEntity.ok(detail);
    }

    @PostMapping("/strategies/backtest")
    @Operation(summary = "策略回测", description = "在历史数据上测试策略表现")
    @PreAuthorize("hasRole('STRATEGIST')")
    public ResponseEntity<BacktestResultVO> backtestStrategy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "回测请求",
                required = true,
                content = @Content(schema = @Schema(implementation = BacktestRequest.class)))
            @Valid @RequestBody BacktestRequest request) {
        
        log.info("策略回测: 策略ID={}, 时间范围={} 至 {}", 
                 request.getStrategyId(), request.getStartDate(), request.getEndDate());
        BacktestResultVO result = backtestService.executeBacktest(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/strategies/{strategyId}/rebalance")
    @Operation(summary = "策略再平衡", description = "执行策略资产再平衡")
    @PreAuthorize("@strategyPermissionService.canRebalance(#strategyId)")
    public ResponseEntity<RebalanceResultVO> rebalanceStrategy(
            @Parameter(description = "策略ID", required = true) 
            @PathVariable Long strategyId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "再平衡请求",
                required = true,
                content = @Content(schema = @Schema(implementation = RebalanceRequest.class)))
            @Valid @RequestBody RebalanceRequest request) {
        
        log.info("策略再平衡: 策略ID={}, 类型={}", strategyId, request.getRebalanceType());
        RebalanceResultVO result = rebalanceService.rebalanceStrategy(strategyId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/strategies/monitoring")
    @Operation(summary = "获取策略监控状态", description = "获取所有策略的实时监控和预警信息")
    @PreAuthorize("hasAnyRole('STRATEGIST', 'TRADER', 'RISK_MANAGER')")
    public ResponseEntity<StrategyMonitoringDashboard> getStrategyMonitoring(
            @Parameter(description = "风险等级") @RequestParam(required = false) RiskLevel riskLevel,
            @Parameter(description = "状态") @RequestParam(required = false) StrategyStatus status,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        StrategyMonitorFilter filter = StrategyMonitorFilter.builder()
            .riskLevel(riskLevel)
            .status(status)
            .build();
            
        StrategyMonitoringDashboard dashboard = monitorService.getMonitoringDashboard(filter, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/strategies/{strategyId}/performance")
    @Operation(summary = "获取策略表现", description = "查看策略历史表现数据")
    @PreAuthorize("@strategyPermissionService.hasAccess(#strategyId)")
    public ResponseEntity<StrategyPerformanceVO> getStrategyPerformance(
            @Parameter(description = "策略ID", required = true) 
            @PathVariable Long strategyId,
            @Parameter(description = "时间范围", example = "YTD") 
            @RequestParam(defaultValue = "YTD") PerformanceRange range) {
        
        StrategyPerformanceVO performance = strategyService.getStrategyPerformance(strategyId, range);
        return ResponseEntity.ok(performance);
    }

    @PostMapping("/strategies/{strategyId}/simulate")
    @Operation(summary = "策略模拟交易", description = "在模拟环境中测试策略表现")
    @PreAuthorize("hasRole('STRATEGIST')")
    public ResponseEntity<SimulationResultVO> simulateStrategy(
            @Parameter(description = "策略ID", required = true) 
            @PathVariable Long strategyId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "模拟参数",
                required = true,
                content = @Content(schema = @Schema(implementation = SimulationRequest.class)))
            @Valid @RequestBody SimulationRequest request) {
        
        log.info("策略模拟: 策略ID={}, 起始资金={}", strategyId, request.getInitialCapital());
        SimulationResultVO result = backtestService.simulateStrategy(strategyId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/strategies/{strategyId}/execute")
    @Operation(summary = "执行策略调仓", description = "将策略调仓计划转化为实际交易")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity<TradeExecutionResult> executeStrategyRebalance(
            @Parameter(description = "策略ID", required = true) 
            @PathVariable Long strategyId) {
        
        log.info("执行策略调仓: 策略ID={}", strategyId);
        TradeExecutionResult result = rebalanceService.executeRebalance(strategyId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/strategies/{strategyId}/risk-analysis")
    @Operation(summary = "策略风险分析", description = "分析策略的风险指标")
    @PreAuthorize("@strategyPermissionService.hasAccess(#strategyId)")
    public ResponseEntity<StrategyRiskAnalysisVO> analyzeStrategyRisk(
            @Parameter(description = "策略ID", required = true) 
            @PathVariable Long strategyId,
            @Parameter(description = "时间范围", example = "MONTH_12") 
            @RequestParam(defaultValue = "MONTH_12") TimeWindow window) {
        
        StrategyRiskAnalysisVO analysis = strategyService.analyzeStrategyRisk(strategyId, window);
        return ResponseEntity.ok(analysis);
    }

    @ExceptionHandler(StrategyExecutionException.class)
    public ResponseEntity<ErrorResponse> handleStrategyError(StrategyExecutionException ex) {
        log.error("策略执行错误: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("