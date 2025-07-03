package com.advisor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能投顾系统主应用类
 */
@SpringBootApplication
@MapperScan("com.advisor.mapper")
public class IntelligentAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentAdvisorApplication.class, args);
        System.out.println("智能投顾系统启动成功！");
    }
} 