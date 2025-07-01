package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.ProductComboAudit;
import java.util.List;

@Mapper
public interface ProductComboAuditMapper {

    @Insert("INSERT INTO product_combo_audit " +
            "(combo_id, audit_time, auditor_id, audit_status, audit_comment) " +
            "VALUES (#{comboId}, #{auditTime}, #{auditorId}, #{auditStatus}, #{auditComment})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductComboAudit audit);

    @Update("UPDATE product_combo_audit SET audit_status=#{auditStatus}, audit_comment=#{auditComment} WHERE id=#{id}")
    int update(ProductComboAudit audit);

    @Select("SELECT * FROM product_combo_audit WHERE combo_id=#{comboId} ORDER BY audit_time DESC")
    List<ProductComboAudit> selectByComboId(Long comboId);

    @Select("SELECT * FROM product_combo_audit WHERE id=#{id}")
    ProductComboAudit selectById(Long id);

    @Delete("DELETE FROM product_combo_audit WHERE id=#{id}")
    int deleteById(Long id);
}