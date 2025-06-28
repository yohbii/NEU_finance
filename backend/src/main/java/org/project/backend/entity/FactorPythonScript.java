package org.project.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 对应 factor_python_script 表
 * 存储Python因子的计算脚本
 */
@Data
public class FactorPythonScript {

    private Long id;

    private Long factorId;

    private String scriptBody;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}