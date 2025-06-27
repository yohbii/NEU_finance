package org.project.backend.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Portfolio {
    private Integer portfolioId;        // 组合ID
    private String portfolioName;       // 组合名称
    private String createdBy;           // 创建人
    private Timestamp createdAt;        // 创建时间
    private String note;                // 备注信息
}