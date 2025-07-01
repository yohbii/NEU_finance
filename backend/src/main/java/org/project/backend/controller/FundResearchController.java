@RestController
@RequestMapping("/api/fund-research")
@Tag(name = "基金研究子系统", description = "提供基金查询、筛选、画像和组合管理功能")
@Slf4j
public class FundResearchController {

    private final FundResearchService fundResearchService;
    private final PortfolioService portfolioService;

    @Autowired
    public FundResearchController(FundResearchService fundResearchService, 
                                  PortfolioService portfolioService) {
        this.fundResearchService = fundResearchService;
        this.portfolioService = portfolioService;
    }

    
    @PostMapping("/funds/query")
    @Operation(summary = "公募基金查询", description = "支持多条件筛选市场全部公募基金")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<PageResult<FundVO>> queryFunds(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "基金查询条件",
                required = true,
                content = @Content(schema = @Schema(implementation = FundQueryRequest.class))
            @Valid @RequestBody FundQueryRequest request,
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") 
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("基金查询请求: {}", request);
        PageResult<FundVO> result = fundResearchService.queryFunds(request, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(result);
    }

    
    @PostMapping("/portfolios")
    @Operation(summary = "保存基金组合", description = "将筛选结果保存为基金组合")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<PortfolioResponse> savePortfolio(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "组合保存请求",
                required = true,
                content = @Content(schema = @Schema(implementation = PortfolioSaveRequest.class)))
            @Valid @RequestBody PortfolioSaveRequest request) {
        
        log.info("保存基金组合: {}", request.getName());
        PortfolioResponse response = portfolioService.savePortfolio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    @GetMapping("/portfolios")
    @Operation(summary = "获取用户基金组合列表")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<PortfolioSimpleVO>> getUserPortfolios() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PortfolioSimpleVO> portfolios = portfolioService.getUserPortfolios(userId);
        return ResponseEntity.ok(portfolios);
    }

    
    @GetMapping("/portfolios/{portfolioId}")
    @Operation(summary = "获取基金组合详情")
    @PreAuthorize("@portfolioPermissionService.hasAccess(#portfolioId)")
    public ResponseEntity<PortfolioDetailVO> getPortfolioDetail(
            @Parameter(description = "组合ID", required = true) 
            @PathVariable Long portfolioId) {
        
        PortfolioDetailVO detail = portfolioService.getPortfolioDetail(portfolioId);
        return ResponseEntity.ok(detail);
    }

    
    @PostMapping("/companies/query")
    @Operation(summary = "基金公司查询", description = "支持多条件筛选基金公司")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<PageResult<FundCompanyVO>> queryCompanies(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "公司查询条件",
                required = true,
                content = @Content(schema = @Schema(implementation = CompanyQueryRequest.class)))
            @Valid @RequestBody CompanyQueryRequest request,
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") 
            @RequestParam(defaultValue = "20") int size) {
        
        PageResult<FundCompanyVO> result = fundResearchService.queryCompanies(request, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(result);
    }

    
    @PostMapping("/managers/query")
    @Operation(summary = "基金经理查询", description = "支持多条件筛选基金经理")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<PageResult<FundManagerVO>> queryManagers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "经理查询条件",
                required = true,
                content = @Content(schema = @Schema(implementation = ManagerQueryRequest.class)))
            @Valid @RequestBody ManagerQueryRequest request,
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") 
            @RequestParam(defaultValue = "20") int size) {
        
        PageResult<FundManagerVO> result = fundResearchService.queryManagers(request, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(result);
    }

    
    @GetMapping("/funds/{fundId}/profile")
    @Operation(summary = "基金画像详情", description = "从多维度展示基金产品信息")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<FundProfileVO> getFundProfile(
            @Parameter(description = "基金ID", required = true, example = "F000001") 
            @PathVariable String fundId) {
        
        FundProfileVO profile = fundResearchService.getFundProfile(fundId);
        return ResponseEntity.ok(profile);
    }

    
    @GetMapping("/funds/{fundId}/holdings")
    @Operation(summary = "基金持仓分析", description = "获取基金最新持仓信息")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<FundHoldingsVO> getFundHoldings(
            @Parameter(description = "基金ID", required = true, example = "F000001") 
            @PathVariable String fundId,
            @Parameter(description = "持仓类型", example = "STOCK") 
            @RequestParam(defaultValue = "STOCK") HoldingType type) {
        
        FundHoldingsVO holdings = fundResearchService.getFundHoldings(fundId, type);
        return ResponseEntity.ok(holdings);
    }

    
    @GetMapping("/funds/{fundId}/performance-attribution")
    @Operation(summary = "基金业绩归因", description = "分析基金业绩来源")
    @PreAuthorize("hasAnyRole('RESEARCHER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<PerformanceAttributionVO> getPerformanceAttribution(
            @Parameter(description = "基金ID", required = true, example = "F000001") 
            @PathVariable String fundId,
            @Parameter(description = "开始日期", example = "2023-01-01") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2023-12-31") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        PerformanceAttributionVO attribution = fundResearchService.getPerformanceAttribution(
            fundId, startDate, endDate);
        return ResponseEntity.ok(attribution);
    }

   
    @ExceptionHandler(FundNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFundNotFound(FundNotFoundException ex) {
        log.error("基金不存在: {}", ex.getFundId());
        ErrorResponse error = new ErrorResponse("FUND_NOT_FOUND", "基金不存在: " + ex.getFundId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}

@Data
@Schema(description = "基金查询请求")
public class FundQueryRequest {
    @Schema(description = "基金代码", example = "F000001")
    private String fundCode;
    
    @Schema(description = "基金名称", example = "华夏成长")
    private String fundName;
    
    @Schema(description = "标签列表", example = "[\"高收益\", \"低波动\"]")
    private List<String> tags;
    
    @Schema(description = "最小成立年限", example = "3")
    private Integer minEstablishYears;
    
    @Schema(description = "最大回撤上限", example = "15.0")
    @DecimalMax(value = "100.0", message = "回撤不能超过100%")
    private Double maxDrawdown;
    
    @Schema(description = "基金类型", example = "STOCK")
    private FundType fundType;
}

@Data
@Schema(description = "基金视图对象")
public class FundVO {
    @Schema(description = "基金ID", example = "F000001")
    private String fundId;
    
    @Schema(description = "基金名称", example = "华夏成长混合")
    private String fundName;
    
    @Schema(description = "基金代码", example = "000001")
    private String fundCode;
    
    @Schema(description = "基金类型", example = "混合型")
    private String fundType;
    
    @Schema(description = "最新净值", example = "2.3456")
    private BigDecimal nav;
    
    @Schema(description = "日涨跌幅", example = "0.56")
    private BigDecimal dailyReturn;
    
    @Schema(description = "标签列表", example = "[\"高收益\", \"低波动\"]")
    private List<String> tags;
    
    @Schema(description = "基金经理", example = "王伟")
    private String manager;
}


@Data
@Schema(description = "分页结果")
public class PageResult<T> {
    @Schema(description = "当前页码", example = "1")
    private int page;
    
    @Schema(description = "每页数量", example = "20")
    private int size;
    
    @Schema(description = "总页数", example = "5")
    private int totalPages;
    
    @Schema(description = "总记录数", example = "100")
    private long totalElements;
    
    @Schema(description = "数据列表")
    private List<T> content;
}


@Data
@AllArgsConstructor
@Schema(description = "错误响应")
public class ErrorResponse {
    @Schema(description = "错误代码", example = "FUND_NOT_FOUND")
    private String code;
    
    @Schema(description = "错误消息", example = "基金不存在")
    private String message;
}