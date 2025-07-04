package com.advisor.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果类
 */
@Data
public class PageResult<T> {
    
    private List<T> records;
    private Long total;
    private Integer current;
    private Integer size;
    private Integer pages;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer current, Integer size) {
        return new PageResult<>(records, total, current, size);
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty(Integer current, Integer size) {
        return new PageResult<>(List.of(), 0L, current, size);
    }
} 