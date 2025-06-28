package org.project.backend.entity;

import lombok.Data;

/**
 * 对应 factor_style_map 表
 * 因子与风格的映射关系表
 */
@Data
public class FactorStyleMap {

    private Long styleId;

    private Long factorId;
}