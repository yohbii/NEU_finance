package org.project.backend.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Company {
    private Integer companyId;      // 公司ID
    private String companyName;     // 公司名称
    private String licenseNo;       // 公司牌照编号
    private LocalDate establishedDate; // 成立时间
}