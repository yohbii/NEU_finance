package com.advisor.mapper;

import com.advisor.entity.FactorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 因子信息Mapper接口
 */
@Mapper
public interface FactorInfoMapper {
    
    /**
     * 查询因子列表
     */
    List<FactorInfo> findList(@Param("factorType") String factorType,
                             @Param("category") String category,
                             @Param("keyword") String keyword,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);
    
    /**
     * 查询因子总数
     */
    Long countList(@Param("factorType") String factorType,
                   @Param("category") String category,
                   @Param("keyword") String keyword);
    
    /**
     * 根据ID查询因子
     */
    FactorInfo findById(@Param("id") Long id);
    
    /**
     * 根据因子代码查询因子
     */
    FactorInfo findByFactorCode(@Param("factorCode") String factorCode);
    
    /**
     * 插入因子
     */
    int insert(FactorInfo factorInfo);
    
    /**
     * 更新因子
     */
    int update(FactorInfo factorInfo);
    
    /**
     * 删除因子
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 查询所有因子类型
     */
    List<String> findAllFactorTypes();
    
    /**
     * 查询所有因子分类
     */
    List<String> findAllCategories();

    // Method for ChatService
    FactorInfo findByName(@Param("factorName") String factorName);
} 