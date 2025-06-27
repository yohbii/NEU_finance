package org.project.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import org.project.backend.entity.Fund;

import java.util.List;

@Mapper
public interface FundMapper {
    // 1. 全部公募基金：按基金代码或标签筛选
    @Select({
            "<script>",
            "SELECT DISTINCT f.* FROM fund f",
            "LEFT JOIN fundTagRelation r ON f.fundCode = r.fundCode",
            "<where>",
            "  <if test='code != null and code != \"\"'>",
            "    f.fundCode = #{code}",
            "  </if>",
            "  <if test='tagIds != null and tagIds.size() > 0'>",
            "    AND r.tagId IN",
            "     <foreach collection='tagIds' item='tid' open='(' separator=',' close=')'>",
            "       #{tid}",
            "     </foreach>",
            "  </if>",
            "</where>",
            "</script>"
    })
    List<Fund> selectByCodeOrTags(@Param("code") String code, @Param("tagIds") List<Integer> tagIds);

    // 2. 基金公司维度：公司名称模糊查+标签筛选
    @Select({
            "<script>",
            "SELECT DISTINCT f.* FROM fund f",
            "JOIN company c ON f.companyId = c.companyId",
            "LEFT JOIN fundTagRelation r ON f.fundCode = r.fundCode",
            "<where>",
            "  <if test='companyName != null and companyName != \"\"'>",
            "    c.companyName LIKE CONCAT('%', #{companyName}, '%')",
            "  </if>",
            "  <if test='tagIds != null and tagIds.size() > 0'>",
            "    AND r.tagId IN",
            "     <foreach collection='tagIds' item='tid' open='(' separator=',' close=')'>",
            "       #{tid}",
            "     </foreach>",
            "  </if>",
            "</where>",
            "</script>"
    })
    List<Fund> selectByCompanyAndTags(@Param("companyName") String companyName, @Param("tagIds") List<Integer> tagIds);

    // 3. 基金经理维度：经理名称模糊查+标签筛选
    @Select({
            "<script>",
            "SELECT DISTINCT f.* FROM fund f",
            "JOIN manager m ON f.managerId = m.managerId",
            "LEFT JOIN fundTagRelation r ON f.fundCode = r.fundCode",
            "<where>",
            "  <if test='managerName != null and managerName != \"\"'>",
            "    m.managerName LIKE CONCAT('%', #{managerName}, '%')",
            "  </if>",
            "  <if test='tagIds != null and tagIds.size() > 0'>",
            "    AND r.tagId IN",
            "     <foreach collection='tagIds' item='tid' open='(' separator=',' close=')'>",
            "       #{tid}",
            "     </foreach>",
            "  </if>",
            "</where>",
            "</script>"
    })
    List<Fund> selectByManagerAndTags(@Param("managerName") String managerName, @Param("tagIds") List<Integer> tagIds);
}