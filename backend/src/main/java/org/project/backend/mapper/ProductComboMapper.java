package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.ProductCombo;
import java.util.List;

@Mapper
public interface ProductComboMapper {

    @Insert("INSERT INTO product_combo " +
            "(combo_name, risk_level, strategy_id, creator_id, create_time, audit_status, product_status, product_params) " +
            "VALUES (#{comboName}, #{riskLevel}, #{strategyId}, #{creatorId}, #{createTime}, #{auditStatus}, #{productStatus}, #{productParams})")
    @Options(useGeneratedKeys = true, keyProperty = "comboId")
    int insert(ProductCombo combo);

    @Update("UPDATE product_combo SET combo_name=#{comboName}, risk_level=#{riskLevel}, " +
            "strategy_id=#{strategyId}, product_status=#{productStatus}, product_params=#{productParams} WHERE combo_id=#{comboId}")
    int update(ProductCombo combo);

    @Select("SELECT * FROM product_combo WHERE combo_id=#{comboId}")
    ProductCombo selectById(Long comboId);

    @Select("SELECT * FROM product_combo")
    List<ProductCombo> selectAll();

    @Delete("DELETE FROM product_combo WHERE combo_id=#{comboId}")
    int deleteById(Long comboId);
}