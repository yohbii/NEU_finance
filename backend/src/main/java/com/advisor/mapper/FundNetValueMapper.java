package com.advisor.mapper;

import com.advisor.entity.FundNetValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 基金净值Mapper接口
 */
@Mapper
public interface FundNetValueMapper {
    
    /**
     * 查询基金净值列表
     */
    List<FundNetValue> findByFundId(@Param("fundId") Long fundId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);
    
    /**
     * 根据基金ID和日期查询净值
     */
    FundNetValue findByFundIdAndDate(@Param("fundId") Long fundId,
                                    @Param("netValueDate") LocalDate netValueDate);
    
    /**
     * 查询基金最新净值
     */
    FundNetValue findLatestByFundId(@Param("fundId") Long fundId);
    
    /**
     * 插入基金净值
     */
    int insert(FundNetValue fundNetValue);
    
    /**
     * 批量插入基金净值
     */
    int batchInsert(@Param("list") List<FundNetValue> list);
    
    /**
     * 更新基金净值
     */
    int update(FundNetValue fundNetValue);
    
    /**
     * 删除基金净值
     */
    int deleteById(@Param("id") Long id);

    /**
     * 获取指定月份的平均净值
     */
    Double getAvgNetValueByMonth(@Param("year") Integer year, @Param("month") Integer month);
} 