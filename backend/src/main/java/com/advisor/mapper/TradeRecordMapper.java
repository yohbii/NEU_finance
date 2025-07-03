package com.advisor.mapper;

import com.advisor.entity.TradeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 交易记录Mapper接口
 */
@Mapper
public interface TradeRecordMapper {
    
    /**
     * 查询交易记录列表
     */
    List<TradeRecord> findList(@Param("portfolioId") Long portfolioId,
                              @Param("assetCode") String assetCode,
                              @Param("tradeType") Integer tradeType,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);
    
    /**
     * 查询交易记录总数
     */
    Long countList(@Param("portfolioId") Long portfolioId,
                   @Param("assetCode") String assetCode,
                   @Param("tradeType") Integer tradeType,
                   @Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);
    
    /**
     * 根据ID查询交易记录
     */
    TradeRecord findById(@Param("id") Long id);
    
    /**
     * 根据交易编号查询交易记录
     */
    TradeRecord findByTradeNo(@Param("tradeNo") String tradeNo);
    
    /**
     * 插入交易记录
     */
    int insert(TradeRecord tradeRecord);
    
    /**
     * 更新交易记录
     */
    int update(TradeRecord tradeRecord);
    
    /**
     * 删除交易记录
     */
    int deleteById(@Param("id") Long id);

    /**
     * 按日期统计交易数量
     */
    Long countByDate(@Param("date") LocalDate date);

    /**
     * 获取最近的交易记录
     */
    List<Map<String, Object>> getRecentTrades(@Param("limit") Integer limit);
} 