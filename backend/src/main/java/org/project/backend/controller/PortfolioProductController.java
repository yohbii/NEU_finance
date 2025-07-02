package org.project.backend.controller;

@RestController
@RequestMapping("/api/portfolio-products")
@Tag(name = "组合产品管理子系统", description = "提供组合产品创建、审核、上架和详情管理功能")
@Slf4j
@RequiredArgsConstructor
public class PortfolioProductController {

    private final PortfolioProductService productService;
    private final ProductApprovalService approvalService;

    @PostMapping
    @Operation(summary = "创建组合产品", description = "创建新的基金组合产品")
    @PreAuthorize("hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ProductCreationResponse> createProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "组合产品创建请求",
                required = true,
                content = @Content(schema = @Schema(implementation = ProductCreateRequest.class)))
            @Valid @RequestBody ProductCreateRequest request) {
        
        log.info("创建组合产品: {}", request.getProductName());
        ProductCreationResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "查询组合产品", description = "分页查询组合产品列表")
    @PreAuthorize("hasAnyRole('PRODUCT_MANAGER', 'INVESTMENT_COMMITTEE', 'ADMIN')")
    public ResponseEntity<PageResult<ProductSummaryVO>> queryProducts(
            @Parameter(description = "产品名称") @RequestParam(required = false) String name,
            @Parameter(description = "风险等级") @RequestParam(required = false) RiskLevel riskLevel,
            @Parameter(description = "产品状态") @RequestParam(required = false) ProductStatus status,
            @Parameter(description = "策略类型") @RequestParam(required = false) StrategyType strategyType,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        ProductQueryRequest queryRequest = ProductQueryRequest.builder()
            .name(name)
            .riskLevel(riskLevel)
            .status(status)
            .strategyType(strategyType)
            .build();
            
        PageResult<ProductSummaryVO> result = productService.queryProducts(
            queryRequest, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "获取组合产品详情", description = "查看组合产品详细信息")
    @PreAuthorize("hasAnyRole('PRODUCT_MANAGER', 'INVESTMENT_COMMITTEE', 'ADMIN')")
    public ResponseEntity<ProductDetailVO> getProductDetail(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId,
            @Parameter(description = "包含策略详情", example = "true") 
            @RequestParam(defaultValue = "true") boolean includeStrategy,
            @Parameter(description = "包含历史表现", example = "true") 
            @RequestParam(defaultValue = "true") boolean includePerformance) {
        
        ProductDetailVO detail = productService.getProductDetail(
            productId, includeStrategy, includePerformance);
        return ResponseEntity.ok(detail);
    }

    @PostMapping("/{productId}/submit-approval")
    @Operation(summary = "提交产品审核", description = "提交组合产品到投资决策委员会审核")
    @PreAuthorize("hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ApprovalSubmissionResponse> submitForApproval(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId) {
        
        log.info("提交产品审核: 产品ID={}", productId);
        ApprovalSubmissionResponse response = approvalService.submitForApproval(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/approval-history")
    @Operation(summary = "获取审核历史", description = "查看产品的审核历史记录")
    @PreAuthorize("@productPermissionService.hasAccess(#productId)")
    public ResponseEntity<List<ApprovalHistoryVO>> getApprovalHistory(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId) {
        
        List<ApprovalHistoryVO> history = approvalService.getApprovalHistory(productId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{productId}/approval")
    @Operation(summary = "审核组合产品", description = "投资决策委员会审核组合产品")
    @PreAuthorize("hasRole('INVESTMENT_COMMITTEE')")
    public ResponseEntity<ProductApprovalResponse> approveProduct(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "审核请求",
                required = true,
                content = @Content(schema = @Schema(implementation = ApprovalRequest.class)))
            @Valid @RequestBody ApprovalRequest request) {
        
        log.info("审核组合产品: 产品ID={}, 决定={}", productId, request.getDecision());
        ProductApprovalResponse response = approvalService.approveProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/publish")
    @Operation(summary = "发布产品", description = "将审核通过的产品发布到前端专区")
    @PreAuthorize("hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ProductPublishResponse> publishProduct(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "发布配置",
                required = true,
                content = @Content(schema = @Schema(implementation = PublishConfigRequest.class)))
            @Valid @RequestBody PublishConfigRequest request) {
        
        log.info("发布组合产品: 产品ID={}", productId);
        ProductPublishResponse response = productService.publishProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/performance")
    @Operation(summary = "获取产品表现", description = "查看组合产品的历史表现数据")
    @PreAuthorize("@productPermissionService.hasAccess(#productId)")
    public ResponseEntity<ProductPerformanceVO> getProductPerformance(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId,
            @Parameter(description = "时间范围", example = "YTD") 
            @RequestParam(defaultValue = "YTD") PerformanceRange range) {
        
        ProductPerformanceVO performance = productService.getProductPerformance(productId, range);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/{productId}/subscriptions")
    @Operation(summary = "获取产品签约情况", description = "查看组合产品的客户签约情况")
    @PreAuthorize("hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<PageResult<SubscriptionVO>> getProductSubscriptions(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId,
            @Parameter(description = "状态") @RequestParam(required = false) SubscriptionStatus status,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        SubscriptionQueryRequest queryRequest = SubscriptionQueryRequest.builder()
            .productId(productId)
            .status(status)
            .build();
            
        PageResult<SubscriptionVO> result = productService.getProductSubscriptions(
            queryRequest, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{productId}/update")
    @Operation(summary = "更新产品信息", description = "更新组合产品的基本信息")
    @PreAuthorize("hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ProductUpdateResponse> updateProduct(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "产品更新请求",
                required = true,
                content = @Content(schema = @Schema(implementation = ProductUpdateRequest.class)))
            @Valid @RequestBody ProductUpdateRequest request) {
        
        log.info("更新组合产品: 产品ID={}", productId);
        ProductUpdateResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/archive")
    @Operation(summary = "归档产品", description = "将产品归档并停止新签约")
    @PreAuthorize("hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ProductStatusResponse> archiveProduct(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId) {
        
        log.info("归档组合产品: 产品ID={}", productId);
        ProductStatusResponse response = productService.updateProductStatus(
            productId, ProductStatus.ARCHIVED);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/documents")
    @Operation(summary = "获取产品文档", description = "获取组合产品的相关文档")
    @PreAuthorize("@productPermissionService.hasAccess(#productId)")
    public ResponseEntity<List<ProductDocumentVO>> getProductDocuments(
            @Parameter(description = "产品ID", required = true) 
            @PathVariable Long productId) {
        
        List<ProductDocumentVO> documents = productService.getProductDocuments(productId);
        return ResponseEntity.ok(documents);
    }

    @ExceptionHandler(ProductCreationException.class)
    public ResponseEntity<ErrorResponse> handleProductCreationError(ProductCreationException ex) {
        log.error("产品创建失败: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("PRODUCT_CREATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}