@RestController
@RequestMapping("/api/factor-management")
public class FactorManagementController {

    // 2.1 因子接入
    @PostMapping("/factors/import")
    public ResponseEntity<Boolean> importFactors(
            @RequestBody FactorImportRequest request) {
        // 实现因子数据接入
    }

    // 2.2 获取因子树
    @GetMapping("/factor-trees/{treeType}")
    public ResponseEntity<FactorTreeVO> getFactorTree(
            @PathVariable String treeType) {
        // 获取指定业务场景的因子树
    }

    // 2.3 创建衍生因子
    @PostMapping("/derived-factors/create")
    public ResponseEntity<Long> createDerivedFactor(
            @RequestBody DerivedFactorRequest request) {
        // 创建衍生因子
    }

    // 2.4 创建风格投资因子
    @PostMapping("/style-factors/create")
    public ResponseEntity<Long> createStyleFactor(
            @RequestBody StyleFactorRequest request) {
        // 创建风格投资因子
    }
}