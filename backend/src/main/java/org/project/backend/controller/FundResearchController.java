@RestController
@RequestMapping("/api/fund-research")
public class FundResearchController {

    // 1.1 全部公募基金查询
    @PostMapping("/funds/query")
    public ResponseEntity<PageResult<FundVO>> queryFunds(
            @RequestBody FundQueryRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 实现基金筛选逻辑
    }

    // 1.1 保存基金组合
    @PostMapping("/portfolio/save")
    public ResponseEntity<Long> savePortfolio(
            @RequestBody PortfolioSaveRequest request) {
        // 实现保存基金组合逻辑
    }

    // 1.2 基金公司查询
    @PostMapping("/companies/query")
    public ResponseEntity<PageResult<FundCompanyVO>> queryCompanies(
            @RequestBody CompanyQueryRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 实现基金公司筛选逻辑
    }

    // 1.3 基金经理查询
    @PostMapping("/managers/query")
    public ResponseEntity<PageResult<FundManagerVO>> queryManagers(
            @RequestBody ManagerQueryRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        // 实现基金经理筛选逻辑
    }

    // 1.4 基金画像详情
    @GetMapping("/funds/{fundId}/profile")
    public ResponseEntity<FundProfileVO> getFundProfile(
            @PathVariable String fundId) {
        // 获取基金完整画像信息
    }
}