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

import java.util.Collections;
import java.util.List;

@Service
public class FactorService {

    @Autowired
    private FactorMapper factorMapper;

    @Autowired
    private FactorPythonScriptMapper factorPythonScriptMapper;

    @Autowired
    private FactorTreeMapper factorTreeMapper;

    @Autowired
    private FactorStyleMapper factorStyleMapper;

    @Autowired
    private FactorStyleMapMapper factorStyleMapMapper;

    // =================================================================
    // == 因子管理 (Factor & FactorPythonScript)
    // =================================================================

    /**
     * 创建一个新因子，如果为Python类型，则同时保存其脚本
     */
    @Transactional
    public HttpResponse<Long> createFactor(Factor factor, String scriptBody) {
        if (factorMapper.findFactorByCode(factor.getFactorCode()) != null) {
            return new HttpResponse<>(-1, null, "因子编码已存在", "因子编码已存在");
        }

        int factorInsertCount = factorMapper.insertFactor(factor);
        if (factorInsertCount <= 0 || factor.getFactorId() == null) {
            return new HttpResponse<>(-1, null, "创建因子失败", "创建因子失败");
        }

        // 这一行现在就正确了，因为 factor.getSourceType() 返回的是 String
        if ("PYTHON".equals(factor.getSourceType()) && StringUtils.hasText(scriptBody)) {
            FactorPythonScript script = new FactorPythonScript();
            script.setFactorId(factor.getFactorId());
            script.setScriptBody(scriptBody);
            int scriptInsertCount = factorPythonScriptMapper.insertScript(script);
            if (scriptInsertCount <= 0) {
                return new HttpResponse<>(-1, null, "因子脚本保存失败", "因子脚本保存失败");
            }
        }
        return new HttpResponse<>(0, factor.getFactorId(), "ok", null);
    }

    /**
     * 根据ID查询因子
     */
    public HttpResponse<Factor> findFactorById(Long factorId) {
        Factor factor = factorMapper.findFactorById(factorId);
        if (factor == null) {
            return new HttpResponse<>(-1, null, "因子不存在", "因子不存在");
        }
        return new HttpResponse<>(0, factor, "ok", null);
    }

    /**
     * 查询所有因子
     */
    public HttpResponse<List<Factor>> findAllFactors() {
        List<Factor> factors = factorMapper.findAllFactors();
        if (factors == null || factors.isEmpty()) {
            return new HttpResponse<>(-1, null, "无因子数据", "无因子数据");
        }
        return new HttpResponse<>(0, factors, "ok", null);
    }

    /**
     * 根据ID删除因子（会一并删除关联的脚本和风格映射）
     */
    @Transactional
    public HttpResponse<Void> deleteFactorById(Long factorId) {
        // 先删除关联的风格映射和脚本
        factorStyleMapMapper.deleteMappingsByFactorId(factorId);
        factorPythonScriptMapper.deleteScriptByFactorId(factorId);

        // 最后删除因子本身
        int deleteCount = factorMapper.deleteFactorById(factorId);
        if (deleteCount <= 0) {
            return new HttpResponse<>(-1, null, "删除因子失败或因子不存在", "删除因子失败或因子不存在");
        }
        return new HttpResponse<>(0, null, "ok", null);
    }

    // =================================================================
    // == 因子树管理 (FactorTree)
    // =================================================================

    /**
     * 创建一个因子树
     */
    public HttpResponse<Long> createFactorTree(FactorTree factorTree) {
        int insertCount = factorTreeMapper.insertTree(factorTree);
        if (insertCount <= 0 || factorTree.getTreeId() == null) {
            return new HttpResponse<>(-1, null, "创建因子树失败", "创建因子树失败");
        }
        return new HttpResponse<>(0, factorTree.getTreeId(), "ok", null);
    }

    /**
     * 根据ID查询因子树（包含完整的JSON结构）
     */
    public HttpResponse<FactorTree> findTreeById(Long treeId) {
        FactorTree tree = factorTreeMapper.findTreeById(treeId);
        if (tree == null) {
            return new HttpResponse<>(-1, null, "因子树不存在", "因子树不存在");
        }
        return new HttpResponse<>(0, tree, "ok", null);
    }

    /**
     * 查询所有因子树（不包含庞大的tree_body）
     */
    public HttpResponse<List<FactorTree>> findAllTrees() {
        List<FactorTree> trees = factorTreeMapper.findAllTrees();
        if (trees == null || trees.isEmpty()) {
            return new HttpResponse<>(-1, null, "无因子树数据", "无因子树数据");
        }
        return new HttpResponse<>(0, trees, "ok", null);
    }

    // =================================================================
    // == 因子风格管理 (FactorStyle & FactorStyleMap)
    // =================================================================

    /**
     * 创建一个因子风格
     */
    public HttpResponse<Long> createFactorStyle(FactorStyle style) {
        int insertCount = factorStyleMapper.insertStyle(style);
        if (insertCount <= 0 || style.getStyleId() == null) {
            return new HttpResponse<>(-1, null, "创建风格失败", "创建风格失败");
        }
        return new HttpResponse<>(0, style.getStyleId(), "ok", null);
    }

    /**
     * 查询所有因子风格
     */
    public HttpResponse<List<FactorStyle>> findAllStyles() {
        List<FactorStyle> styles = factorStyleMapper.findAllStyles();
        if (styles == null || styles.isEmpty()) {
            return new HttpResponse<>(-1, null, "无因子风格数据", "无因子风格数据");
        }
        return new HttpResponse<>(0, styles, "ok", null);
    }

    /**
     * 将一个因子添加到一个风格分类中
     */
    public HttpResponse<Void> addFactorToStyle(Long styleId, Long factorId) {
        int insertCount = factorStyleMapMapper.addFactorToStyle(styleId, factorId);
        if (insertCount <= 0) {
            return new HttpResponse<>(-1, null, "添加因子到风格失败", "添加因子到风格失败");
        }
        return new HttpResponse<>(0, null, "ok", null);
    }
}