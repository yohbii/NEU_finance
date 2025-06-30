package org.project.backend.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FactorStyleMapMapper {

    @Insert("INSERT INTO factor_style_map(style_id, factor_id) VALUES(#{styleId}, #{factorId})")
    int addFactorToStyle(@Param("styleId") Long styleId, @Param("factorId") Long factorId);

    @Delete("DELETE FROM factor_style_map WHERE style_id = #{styleId} AND factor_id = #{factorId}")
    int removeFactorFromStyle(@Param("styleId") Long styleId, @Param("factorId") Long factorId);

    @Select("SELECT factor_id FROM factor_style_map WHERE style_id = #{styleId}")
    List<Long> findFactorIdsByStyleId(@Param("styleId") Long styleId);

    @Select("SELECT style_id FROM factor_style_map WHERE factor_id = #{factorId}")
    List<Long> findStyleIdsByFactorId(@Param("factorId") Long factorId);

    @Delete("DELETE FROM factor_style_map WHERE factor_id = #{factorId}")
    int deleteMappingsByFactorId(@Param("factorId") Long factorId);
}