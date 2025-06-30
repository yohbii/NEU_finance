@RestController
@RequestMapping("/api/portfolio-products")
public class PortfolioProductController {

    // 4.1 策略组合上架审核
    @PostMapping("/{productId}/approval")
    public ResponseEntity<Boolean> approveProduct(
            @PathVariable Long productId,
            @RequestBody ApprovalRequest request) {
        // 审核上架组合产品
    }

    // 4.2 创建组合产品
    @PostMapping("/create")
    public ResponseEntity<Long> createProduct(
            @RequestBody ProductCreateRequest request) {
        // 创建新的组合产品
    }

    // 4.3 获取组合产品详情
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailVO> getProductDetail(
            @PathVariable Long productId) {
        // 获取产品详情
    }
}