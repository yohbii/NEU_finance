package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.FactorTree;

import java.util.List;

@Mapper
public interface FactorTreeMapper {

    @Insert("INSERT INTO factor_tree(tree_name, description, tree_body) " +
            "VALUES(#{treeName}, #{description}, #{treeBody})")
    @Options(useGeneratedKeys = true, keyProperty = "treeId")
    int insertTree(FactorTree factorTree);

    @Select("SELECT * FROM factor_tree WHERE tree_id = #{treeId}")
    FactorTree findTreeById(@Param("treeId") Long treeId);

    @Select("SELECT tree_id, tree_name, description, create_time, update_time FROM factor_tree ORDER BY create_time DESC")
    List<FactorTree> findAllTrees(); // 通常列表不需要加载庞大的tree_body

    @Update("UPDATE factor_tree SET " +
            "tree_name = #{treeName}, " +
            "description = #{description}, " +
            "tree_body = #{treeBody} " +
            "WHERE tree_id = #{treeId}")
    int updateTree(FactorTree factorTree);

    @Delete("DELETE FROM factor_tree WHERE tree_id = #{treeId}")
    int deleteTreeById(@Param("treeId") Long treeId);
}