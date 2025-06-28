package org.project.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 对应 factor 表
 * 因子主表，存储因子的核心元数据
 */
@Data
public class Factor {

    private Long factorId;

    private String factorCode;

    private String factorName;

    private String description;

    private String dataType;

    private String sourceType;

    private Boolean isDerived;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}