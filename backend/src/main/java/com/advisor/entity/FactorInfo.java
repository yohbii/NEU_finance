package com.advisor.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 因子信息实体类
 */
@Data
public class FactorInfo {
    
    private Long id;
    private String factorCode;
    private String factorName;
    private String factorType;
    private String category;
    private String description;
    private String calculationMethod;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 