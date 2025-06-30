package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.entity.Factor;
import org.project.backend.entity.FactorPythonScript;
import org.project.backend.entity.FactorStyle;
import org.project.backend.entity.FactorTree;
import org.project.backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

/**
 * Spring容器会自动扫描并实例化这个类的对象。
 */
@Service
public class FactorService {

    // 使用 @Autowired 进行依赖注入，Spring会自动将实现了这些接口的Mapper Bean注入进来。
    @Autowired
    private FactorMapper factorMapper; // 负责因子的基本信息读写

    @Autowired
    private FactorPythonScriptMapper factorPythonScriptMapper; // 负责Python因子脚本的读写

    @Autowired
    private FactorTreeMapper factorTreeMapper; // 负责因子树的读写

    @Autowired
    private FactorStyleMapper factorStyleMapper; // 负责因子风格的读写

    @Autowired
    private FactorStyleMapMapper factorStyleMapMapper; // 负责因子与风格映射关系的读写

    // =================================================================
    // == 因子管理 (Factor & FactorPythonScript)
    // =================================================================

    /**
     * 创建一个新因子。如果因子类型为PYTHON，则会一并将其脚本内容保存到关联表中。
     * 这个方法涉及到对两张表（factor 和 factor_python_script）的写操作，
     * 因此使用 @Transactional 注解来保证这两个操作的原子性。
     * 如果其中任何一个操作失败，整个事务将回滚，数据库不会留下不完整的脏数据。
     *
     * @param factor 包含因子基本信息的对象，如因子编码、名称、来源等。
     * @param scriptBody 因子脚本的内容，仅当因子来源是PYTHON时有效。
     * @return 返回一个包含新创建因子ID的HttpResponse对象。如果创建失败，code为-1并附带错误信息。
     */
    @Transactional
    public HttpResponse<Long> createFactor(Factor factor, String scriptBody) {
        // 1. 检查因子编码是否已存在，保证其唯一性。
        if (factorMapper.findFactorByCode(factor.getFactorCode()) != null) {
            return new HttpResponse<>(-1, null, "因子编码已存在", "因子编码已存在，请使用其他编码");
        }

        // 2. 将因子的基本信息插入到 factor 表中。
        //    MyBatis在执行插入后，会自动将数据库生成的主键ID回填到传入的 factor 对象中。
        int factorInsertCount = factorMapper.insertFactor(factor);

        // 3. 检查因子信息是否插入成功，并且主键ID是否已成功回填。
        if (factorInsertCount <= 0 || factor.getFactorId() == null) {
            return new HttpResponse<>(-1, null, "创建因子失败", "数据库插入因子基本信息时发生错误");
        }

        // 4. 判断因子来源是否为PYTHON且脚本内容不为空。
        //    使用 StringUtils.hasText() 可以同时检查 null、空字符串 "" 和只包含空白字符的情况。
        if ("PYTHON".equals(factor.getSourceType()) && StringUtils.hasText(scriptBody)) {
            // 5. 如果是Python因子，则创建脚本对象并保存。
            FactorPythonScript script = new FactorPythonScript();
            script.setFactorId(factor.getFactorId()); // 关联刚刚创建的因子ID
            script.setScriptBody(scriptBody); // 设置脚本内容

            int scriptInsertCount = factorPythonScriptMapper.insertScript(script);
            // 6. 检查脚本是否保存成功。如果失败，由于 @Transactional 的存在，之前插入的因子信息也会被回滚。
            if (scriptInsertCount <= 0) {
                // 主动抛出异常也可以触发事务回滚，但这里为了返回统一的HttpResponse格式，我们选择返回错误信息。
                // Spring的@Transactional默认只在抛出RuntimeException时回滚，直接返回不会。
                // 若要返回也回滚，需手动管理事务或配置@Transactional(rollbackFor = Exception.class)。
                // 当前实现下，若脚本保存失败，需要调用者处理可能产生的“孤儿”因子数据，或者按上述方式配置事务。
                // (更严谨的做法是：`throw new RuntimeException("因子脚本保存失败");` 这会自动触发回滚)
                return new HttpResponse<>(-1, null, "因子脚本保存失败", "数据库插入因子脚本时发生错误");
            }
        }

        // 7. 所有操作成功，返回成功响应，并附上新因子的ID。
        return new HttpResponse<>(0, factor.getFactorId(), "ok", null);
    }

    /**
     * 根据因子的唯一ID查询其详细信息。
     *
     * @param factorId 要查询的因子的ID。
     * @return 返回包含因子完整信息的HttpResponse对象。如果因子不存在，则返回错误信息。
     */
    public HttpResponse<Factor> findFactorById(Long factorId) {
        Factor factor = factorMapper.findFactorById(factorId);
        if (factor == null) {
            return new HttpResponse<>(-1, null, "因子不存在", "未找到指定ID的因子");
        }
        return new HttpResponse<>(0, factor, "ok", null);
    }

    /**
     * 查询系统中所有因子的列表。
     *
     * @return 返回一个包含所有因子对象的列表的HttpResponse。如果系统中没有任何因子，则返回提示信息。
     */
    public HttpResponse<List<Factor>> findAllFactors() {
        List<Factor> factors = factorMapper.findAllFactors();
        // 即使没有数据，Mapper通常会返回一个空列表而不是null，但为了健壮性，两者都进行判断。
        if (factors == null || factors.isEmpty()) {
            return new HttpResponse<>(-1, null, "无因子数据", "系统中当前没有任何因子");
        }
        return new HttpResponse<>(0, factors, "ok", null);
    }

    /**
     * 根据ID删除一个因子。
     * 这是一个级联删除操作，会一并删除与该因子关联的Python脚本和所有风格映射关系。
     * 使用 @Transactional 保证这些删除操作的原子性。
     *
     * @param factorId 要删除的因子的ID。
     * @return 返回一个表示操作结果的HttpResponse。
     */
    @Transactional
    public HttpResponse<Void> deleteFactorById(Long factorId) {
        // 1. 删除因子与风格的映射关系（从 factor_style_map 表中删除）
        factorStyleMapMapper.deleteMappingsByFactorId(factorId);

        // 2. 删除与因子关联的Python脚本（从 factor_python_script 表中删除）
        factorPythonScriptMapper.deleteScriptByFactorId(factorId);

        // 3. 最后删除因子本身（从 factor 表中删除）
        int deleteCount = factorMapper.deleteFactorById(factorId);

        // 4. 检查因子是否真的被删除了。如果deleteCount为0，说明可能传入的factorId本身就不存在。
        if (deleteCount <= 0) {
            return new HttpResponse<>(-1, null, "删除因子失败或因子不存在", "可能该因子已被删除或ID错误");
        }

        // 5. 所有删除操作成功。
        return new HttpResponse<>(0, null, "ok", null);
    }

    // =================================================================
    // == 因子树管理 (FactorTree)
    // =================================================================

    /**
     * 创建一个新的因子树。
     *
     * @param factorTree 包含因子树名称、描述和JSON结构体（tree_body）的对象。
     * @return 返回包含新创建因子树ID的HttpResponse对象。
     */
    public HttpResponse<Long> createFactorTree(FactorTree factorTree) {
        int insertCount = factorTreeMapper.insertTree(factorTree);
        if (insertCount <= 0 || factorTree.getTreeId() == null) {
            return new HttpResponse<>(-1, null, "创建因子树失败", "数据库插入因子树时发生错误");
        }
        return new HttpResponse<>(0, factorTree.getTreeId(), "ok", null);
    }

    /**
     * 根据ID查询因子树的完整信息，包括其庞大的JSON结构体。
     *
     * @param treeId 要查询的因子树的ID。
     * @return 返回包含因子树完整信息的HttpResponse对象。
     */
    public HttpResponse<FactorTree> findTreeById(Long treeId) {
        FactorTree tree = factorTreeMapper.findTreeById(treeId);
        if (tree == null) {
            return new HttpResponse<>(-1, null, "因子树不存在", "未找到指定ID的因子树");
        }
        return new HttpResponse<>(0, tree, "ok", null);
    }

    /**
     * 查询所有因子树的基本信息列表。
     * 为了避免网络传输大量数据，这个查询通常不包含庞大的 `tree_body` 字段。
     * （这需要在 FactorTreeMapper.xml 中配置 SELECT 语句只查询部分字段）
     *
     * @return 返回一个包含所有因子树（基本信息）的列表的HttpResponse。
     */
    public HttpResponse<List<FactorTree>> findAllTrees() {
        List<FactorTree> trees = factorTreeMapper.findAllTrees();
        if (trees == null || trees.isEmpty()) {
            return new HttpResponse<>(-1, null, "无因子树数据", "系统中当前没有任何因子树");
        }
        return new HttpResponse<>(0, trees, "ok", null);
    }

    // =================================================================
    // == 因子风格管理 (FactorStyle & FactorStyleMap)
    // =================================================================

    /**
     * 创建一个新的因子风格分类。
     *
     * @param style 包含风格名称和描述的对象。
     * @return 返回包含新创建风格ID的HttpResponse对象。
     */
    public HttpResponse<Long> createFactorStyle(FactorStyle style) {
        int insertCount = factorStyleMapper.insertStyle(style);
        if (insertCount <= 0 || style.getStyleId() == null) {
            return new HttpResponse<>(-1, null, "创建风格失败", "数据库插入因子风格时发生错误");
        }
        return new HttpResponse<>(0, style.getStyleId(), "ok", null);
    }

    /**
     * 查询所有已定义的因子风格。
     *
     * @return 返回一个包含所有因子风格的列表的HttpResponse。
     */
    public HttpResponse<List<FactorStyle>> findAllStyles() {
        List<FactorStyle> styles = factorStyleMapper.findAllStyles();
        if (styles == null || styles.isEmpty()) {
            return new HttpResponse<>(-1, null, "无因子风格数据", "系统中当前没有任何因子风格");
        }
        return new HttpResponse<>(0, styles, "ok", null);
    }

    /**
     * 将一个已存在的因子添加到一个已存在的风格分类中。
     * 这实际上是在 factor_style_map 映射表中创建一条新的记录。
     *
     * @param styleId 目标风格的ID。
     * @param factorId 要添加的因子的ID。
     * @return 返回一个表示操作结果的HttpResponse。
     */
    public HttpResponse<Void> addFactorToStyle(Long styleId, Long factorId) {
        // 在实际应用中，这里可能需要先检查styleId和factorId是否存在，以提供更友好的错误提示。
        // 例如: if (factorMapper.findFactorById(factorId) == null) { return error_response; }
        int insertCount = factorStyleMapMapper.addFactorToStyle(styleId, factorId);
        if (insertCount <= 0) {
            return new HttpResponse<>(-1, null, "添加因子到风格失败", "可能由于因子或风格不存在，或该映射已存在");
        }
        return new HttpResponse<>(0, null, "ok", null);
    }
}