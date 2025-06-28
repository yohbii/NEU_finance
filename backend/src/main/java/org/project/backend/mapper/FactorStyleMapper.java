package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.FactorStyle;

import java.util.List;

@Mapper
public interface FactorStyleMapper {

    @Insert("INSERT INTO factor_style(style_code, style_name, description) " +
            "VALUES(#{styleCode}, #{styleName}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "styleId")
    int insertStyle(FactorStyle style);

    @Select("SELECT * FROM factor_style WHERE style_id = #{styleId}")
    FactorStyle findStyleById(@Param("styleId") Long styleId);

    @Select("SELECT * FROM factor_style ORDER BY style_id")
    List<FactorStyle> findAllStyles();

    @Update("UPDATE factor_style SET " +
            "style_code = #{styleCode}, " +
            "style_name = #{styleName}, " +
            "description = #{description} " +
            "WHERE style_id = #{styleId}")
    int updateStyle(FactorStyle style);

    @Delete("DELETE FROM factor_style WHERE style_id = #{styleId}")
    int deleteStyleById(@Param("styleId") Long styleId);
}