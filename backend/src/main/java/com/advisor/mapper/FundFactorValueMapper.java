package com.advisor.mapper;

import com.advisor.entity.FundFactorValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 基金因子值Mapper接口
 */
@Mapper
public interface FundFactorValueMapper {
    
    /**
     * 查询基金因子值列表
     */
    List<FundFactorValue> findList(@Param("fundCode") String fundCode,
                                  @Param("factorCode") String factorCode,
                                  @Param("offset") Integer offset,
                                  @Param("limit") Integer limit);
    
    /**
     * 查询基金因子值总数
     */
    Long countList(@Param("fundCode") String fundCode,
                   @Param("factorCode") String factorCode);
    
    /**
     * 根据ID查询基金因子值
     */
    FundFactorValue findById(@Param("id") Long id);
    
    /**
     * 根据基金代码和因子代码查询因子值
     */
    FundFactorValue findByFundAndFactor(@Param("fundCode") String fundCode,
                                       @Param("factorCode") String factorCode);
    
    /**
     * 插入基金因子值
     */
    int insert(FundFactorValue fundFactorValue);
    
    /**
     * 更新基金因子值
     */
    int update(FundFactorValue fundFactorValue);
    
    /**
     * 删除基金因子值
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据基金代码查询所有因子值
     */
    List<FundFactorValue> findByFundCode(@Param("fundCode") String fundCode);
    
    /**
     * 批量插入基金因子值
     */
    int batchInsert(@Param("list") List<FundFactorValue> fundFactorValues);
} 