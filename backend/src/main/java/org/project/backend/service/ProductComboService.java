package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.entity.ProductCombo;
import org.project.backend.entity.ProductComboAudit;
import org.project.backend.entity.ProductComboFund;
import org.project.backend.mapper.ProductComboAuditMapper;
import org.project.backend.mapper.ProductComboFundMapper;
import org.project.backend.mapper.ProductComboMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 组合产品管理Service
 */
@Service
public class ProductComboService {

    @Autowired
    private ProductComboMapper productComboMapper;
    @Autowired
    private ProductComboFundMapper productComboFundMapper;
    @Autowired
    private ProductComboAuditMapper productComboAuditMapper;

    // =================================================================
    // 1. 组合产品的创建、查询、删除
    // =================================================================

    /**
     * 创建新的组合产品
     * @param combo 组合产品基本信息
     * @return 返回新建产品id
     */
    @Transactional
    public HttpResponse<Long> createProductCombo(ProductCombo combo, List<ProductComboFund> fundList) {
        int insertCombo = productComboMapper.insert(combo);
        if (insertCombo <= 0 || combo.getComboId() == null) {
            return new HttpResponse<>(-1, null, "创建组合失败", "插入产品信息失败");
        }
        // 批量添加组合成分基金
        if (fundList != null && !fundList.isEmpty()) {
            for (ProductComboFund fund : fundList) {
                fund.setComboId(combo.getComboId());
                int fundInsert = productComboFundMapper.insert(fund);
                if (fundInsert <= 0) {
                    return new HttpResponse<>(-1, null, "创建组合成分失败", "插入基金成分失败");
                }
            }
        }
        return new HttpResponse<>(0, combo.getComboId(), "ok", null);
    }

    /**
     * 查询产品详情（包括基本信息及持仓成分）
     */
    public HttpResponse<ProductCombo> findProductComboById(Long comboId) {
        ProductCombo combo = productComboMapper.selectById(comboId);
        if (combo == null) {
            return new HttpResponse<>(-1, null, "组合不存在", "未找到编号为" + comboId + "的组合");
        }
        return new HttpResponse<>(0, combo, "ok", null);
    }

    /**
     * 返回所有组合产品（可用于产品专区展示)
     */
    public HttpResponse<List<ProductCombo>> findAllProductCombos() {
        List<ProductCombo> combos = productComboMapper.selectAll();
        if (combos == null || combos.isEmpty()) {
            return new HttpResponse<>(-1, null, "无产品数据", "当前没有任何组合产品");
        }
        return new HttpResponse<>(0, combos, "ok", null);
    }

    /**
     * 删除组合产品（级联删除成分和所有审核信息）
     */
    @Transactional
    public HttpResponse<Void> deleteProductComboById(Long comboId) {
        // 先删成分基金
        productComboFundMapper.deleteByComboId(comboId);
        // 再删审核流程记录
        List<ProductComboAudit> audits = productComboAuditMapper.selectByComboId(comboId);
        if (audits != null) {
            for (ProductComboAudit audit : audits) {
                productComboAuditMapper.deleteById(audit.getId());
            }
        }
        // 最后删产品本身
        int count = productComboMapper.deleteById(comboId);
        if (count <= 0) {
            return new HttpResponse<>(-1, null, "删除失败或产品不存在", "无法完成删除，检查ID");
        }
        return new HttpResponse<>(0, null, "ok", null);
    }

    // =================================================================
    // 2. 组合基金成分管理
    // =================================================================

    /**
     * 查询指定组合的成分基金列表
     */
    public HttpResponse<List<ProductComboFund>> findComboFunds(Long comboId) {
        List<ProductComboFund> funds = productComboFundMapper.selectByComboId(comboId);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "无持仓基金", "该组合下没有持仓或成分");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }

    // =================================================================
    // 3. 组合产品上架审核流程管理
    // =================================================================

    /**
     * 新增一条审核记录（如提审/复审）
     */
    @Transactional
    public HttpResponse<Long> submitComboAudit(ProductComboAudit audit) {
        int result = productComboAuditMapper.insert(audit);
        if (result <= 0 || audit.getId() == null) {
            return new HttpResponse<>(-1, null, "提交审核失败", "插入审核记录时失败");
        }
        return new HttpResponse<>(0, audit.getId(), "ok", null);
    }

    /**
     * 查询指定组合的所有审核历史
     */
    public HttpResponse<List<ProductComboAudit>> findComboAuditList(Long comboId) {
        List<ProductComboAudit> audits = productComboAuditMapper.selectByComboId(comboId);
        if (audits == null || audits.isEmpty()) {
            return new HttpResponse<>(-1, null, "无审核历史", "该产品还没有任何审核记录");
        }
        return new HttpResponse<>(0, audits, "ok", null);
    }

    /**
     * 审核审批（如通过/拒绝），只用update状态和意见
     */
    @Transactional
    public HttpResponse<Void> approveComboAudit(ProductComboAudit audit) {
        int result = productComboAuditMapper.update(audit);
        if (result <= 0) {
            return new HttpResponse<>(-1, null, "更新审核失败", "审核未成功，请检查参数");
        }
        // 可选：更新组合主表状态
        ProductCombo combo = productComboMapper.selectById(audit.getComboId());
        if (combo != null) {
            combo.setAuditStatus(audit.getAuditStatus());
            // 如通过审核可设置产品状态为已上架等
            if ("APPROVED".equalsIgnoreCase(audit.getAuditStatus())) {
                combo.setProductStatus("ON_SHELF");
            } else if ("REJECTED".equalsIgnoreCase(audit.getAuditStatus())) {
                combo.setProductStatus("OFF_SHELF");
            }
            productComboMapper.update(combo);
        }
        return new HttpResponse<>(0, null, "ok", null);
    }
}