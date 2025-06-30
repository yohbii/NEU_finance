package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.FactorPythonScript;

@Mapper
public interface FactorPythonScriptMapper {

    @Insert("INSERT INTO factor_python_script(factor_id, script_body) VALUES(#{factorId}, #{scriptBody})")
    int insertScript(FactorPythonScript script);

    @Select("SELECT * FROM factor_python_script WHERE factor_id = #{factorId}")
    FactorPythonScript findScriptByFactorId(@Param("factorId") Long factorId);

    @Update("UPDATE factor_python_script SET script_body = #{scriptBody} WHERE factor_id = #{factorId}")
    int updateScript(FactorPythonScript script);

    @Delete("DELETE FROM factor_python_script WHERE factor_id = #{factorId}")
    int deleteScriptByFactorId(@Param("factorId") Long factorId);
}