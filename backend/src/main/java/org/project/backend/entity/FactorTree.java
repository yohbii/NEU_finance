package org.project.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 对应 factor_tree 表
 * 存储因子树结构，treeBody字段为JSON字符串
 */
@Data
public class FactorTree {

    private Long treeId;

    private String treeName;

    private String description;

    /**
     * 存储树形结构的JSON字符串
     */
    private String treeBody;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}