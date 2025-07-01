@RestController
@RequestMapping("/api/factor-management")
@Tag(name = "因子管理子系统", description = "提供因子接入、因子树管理、衍生因子创建和风格因子管理功能")
@Slf4j
@RequiredArgsConstructor
public class FactorManagementController {

    private final FactorService factorService;
    private final FactorTreeService factorTreeService;
    private final FactorDerivationService factorDerivationService;

    @PostMapping("/factors/import")
    @Operation(summary = "因子数据接入", description = "支持配置化数据源接入和Python脚本接入")
    @PreAuthorize("hasRole('QUANT_DEVELOPER')")
    public ResponseEntity<FactorImportResponse> importFactors(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "因子导入请求",
                required = true,
                content = @Content(schema = @Schema(implementation = FactorImportRequest.class)))
            @Valid @RequestBody FactorImportRequest request) {
        
        log.info("因子导入请求: 类型={}, 源={}", request.getImportType(), request.getSource());
        FactorImportResponse response = factorService.importFactors(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/factor-trees")
    @Operation(summary = "获取因子树类型", description = "获取所有可用的因子树分类")
    @PreAuthorize("hasAnyRole('QUANT_DEVELOPER', 'RESEARCHER')")
    public ResponseEntity<List<FactorTreeTypeVO>> getFactorTreeTypes() {
        List<FactorTreeTypeVO> types = factorTreeService.getFactorTreeTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/factor-trees/{treeType}")
    @Operation(summary = "获取因子树结构", description = "获取指定类型的因子树层级结构")
    @PreAuthorize("hasAnyRole('QUANT_DEVELOPER', 'RESEARCHER')")
    public ResponseEntity<FactorTreeVO> getFactorTree(
            @Parameter(description = "因子树类型", required = true, example = "QUANT_RESEARCH") 
            @PathVariable String treeType) {
        
        FactorTreeVO tree = factorTreeService.getFactorTree(treeType);
        return ResponseEntity.ok(tree);
    }

    @PostMapping("/derived-factors")
    @Operation(summary = "创建衍生因子", description = "通过多因子加权创建新的复合因子")
    @PreAuthorize("hasRole('QUANT_DEVELOPER')")
    public ResponseEntity<DerivedFactorResponse> createDerivedFactor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "衍生因子创建请求",
                required = true,
                content = @Content(schema = @Schema(implementation = DerivedFactorRequest.class)))
            @Valid @RequestBody DerivedFactorRequest request) {
        
        log.info("创建衍生因子: {}", request.getFactorName());
        DerivedFactorResponse response = factorDerivationService.createDerivedFactor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/derived-factors/{factorId}")
    @Operation(summary = "获取衍生因子详情", description = "获取衍生因子的计算公式和组成因子")
    @PreAuthorize("hasAnyRole('QUANT_DEVELOPER', 'RESEARCHER')")
    public ResponseEntity<DerivedFactorDetailVO> getDerivedFactorDetail(
            @Parameter(description = "因子ID", required = true) 
            @PathVariable Long factorId) {
        
        DerivedFactorDetailVO detail = factorDerivationService.getDerivedFactorDetail(factorId);
        return ResponseEntity.ok(detail);
    }

    @PostMapping("/style-factors")
    @Operation(summary = "创建风格投资因子", description = "创建用于风格分类的投资因子")
    @PreAuthorize("hasRole('QUANT_DEVELOPER')")
    public ResponseEntity<StyleFactorResponse> createStyleFactor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "风格因子创建请求",
                required = true,
                content = @Content(schema = @Schema(implementation = StyleFactorRequest.class)))
            @Valid @RequestBody StyleFactorRequest request) {
        
        log.info("创建风格因子: {}", request.getStyleName());
        StyleFactorResponse response = factorDerivationService.createStyleFactor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/factors")
    @Operation(summary = "因子查询", description = "支持按名称、类型和标签查询因子")
    @PreAuthorize("hasAnyRole('QUANT_DEVELOPER', 'RESEARCHER')")
    public ResponseEntity<PageResult<FactorVO>> queryFactors(
            @Parameter(description = "因子名称") @RequestParam(required = false) String name,
            @Parameter(description = "因子类型") @RequestParam(required = false) FactorType type,
            @Parameter(description = "标签") @RequestParam(required = false) String tag,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "20") @RequestParam(defaultValue = "20") int size) {
        
        FactorQueryRequest request = FactorQueryRequest.builder()
            .name(name)
            .type(type)
            .tag(tag)
            .build();
            
        PageResult<FactorVO> result = factorService.queryFactors(request, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/factors/{factorId}/impact-analysis")
    @Operation(summary = "因子影响力分析", description = "分析因子对基金收益的影响程度")
    @PreAuthorize("hasRole('QUANT_DEVELOPER')")
    public ResponseEntity<FactorImpactAnalysisVO> analyzeFactorImpact(
            @Parameter(description = "因子ID", required = true) 
            @PathVariable Long factorId,
            @Parameter(description = "时间窗口", example = "MONTH_12") 
            @RequestParam(defaultValue = "MONTH_12") TimeWindow window) {
        
        FactorImpactAnalysisVO analysis = factorService.analyzeFactorImpact(factorId, window);
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/factors/validation")
    @Operation(summary = "因子有效性验证", description = "验证因子在历史数据中的预测能力")
    @PreAuthorize("hasRole('QUANT_DEVELOPER')")
    public ResponseEntity<FactorValidationResult> validateFactor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "因子验证请求",
                required = true,
                content = @Content(schema = @Schema(implementation = FactorValidationRequest.class)))
            @Valid @RequestBody FactorValidationRequest request) {
        
        log.info("因子验证: {}", request.getFactorIds());
        FactorValidationResult result = factorService.validateFactors(request);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(FactorCalculationException.class)
    public ResponseEntity<ErrorResponse> handleFactorCalculationError(FactorCalculationException ex) {
        log.error("因子计算错误: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("FACTOR_CALCULATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}