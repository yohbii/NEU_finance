package org.project.backend.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {
    private String id;
    private String realName;
    private String username;
    private String password;
    private List<String> roles;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
