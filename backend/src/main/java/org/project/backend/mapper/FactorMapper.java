package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.Factor;

import java.util.List;

@Mapper
public interface FactorMapper {

    @Insert("INSERT INTO factor(factor_code, factor_name, description, data_type, source_type, is_derived) " +
            "VALUES(#{factorCode}, #{factorName}, #{description}, #{dataType}, #{sourceType}, #{isDerived})")
    @Options(useGeneratedKeys = true, keyProperty = "factorId")
    int insertFactor(Factor factor);

    @Select("SELECT * FROM factor WHERE factor_id = #{factorId}")
    Factor findFactorById(@Param("factorId") Long factorId);

    @Select("SELECT * FROM factor WHERE factor_code = #{factorCode}")
    Factor findFactorByCode(@Param("factorCode") String factorCode);

    @Select("SELECT * FROM factor ORDER BY create_time DESC")
    List<Factor> findAllFactors();

    @Update("UPDATE factor SET " +
            "factor_code = #{factorCode}, " +
            "factor_name = #{factorName}, " +
            "description = #{description}, " +
            "data_type = #{dataType}, " +
            "source_type = #{sourceType}, " +
            "is_derived = #{isDerived} " +
            "WHERE factor_id = #{factorId}")
    int updateFactor(Factor factor);

    @Delete("DELETE FROM factor WHERE factor_id = #{factorId}")
    int deleteFactorById(@Param("factorId") Long factorId);
}