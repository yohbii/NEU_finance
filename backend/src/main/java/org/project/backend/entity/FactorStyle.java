package org.project.backend.entity;

import lombok.Data;

/**
 * 对应 factor_style 表
 * 因子风格分类
 */
@Data
public class FactorStyle {

    private Long styleId;

    private String styleCode;

    private String styleName;

    private String description;
}