package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.ProductComboFund;
import java.util.List;

@Mapper
public interface ProductComboFundMapper {

    @Delete("DELETE FROM product_combo_fund WHERE combo_id=#{comboId}")
    int deleteByComboId(Long comboId);

    @Insert("INSERT INTO product_combo_fund " +
            "(combo_id, fund_code, fund_name, weight, position) " +
            "VALUES (#{comboId}, #{fundCode}, #{fundName}, #{weight}, #{position})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductComboFund comboFund);

    @Update("UPDATE product_combo_fund SET fund_code=#{fundCode}, fund_name=#{fundName}, " +
            "weight=#{weight}, position=#{position} WHERE id=#{id}")
    int update(ProductComboFund comboFund);

    @Select("SELECT * FROM product_combo_fund WHERE combo_id=#{comboId}")
    List<ProductComboFund> selectByComboId(Long comboId);

    @Delete("DELETE FROM product_combo_fund WHERE id=#{id}")
    int deleteById(Long id);
}