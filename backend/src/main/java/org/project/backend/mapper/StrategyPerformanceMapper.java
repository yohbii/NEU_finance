package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.StrategyPerformance;
import java.util.List;

@Mapper
public interface StrategyPerformanceMapper {

    @Insert("INSERT INTO strategy_performance (strategy_id, stat_date, nav, return_daily, return_total) " +
            "VALUES (#{strategyId}, #{statDate}, #{nav}, #{returnDaily}, #{returnTotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StrategyPerformance performance);

    @Update("UPDATE strategy_performance SET strategy_id=#{strategyId}, stat_date=#{statDate}, nav=#{nav}, return_daily=#{returnDaily}, return_total=#{returnTotal} WHERE id=#{id}")
    int update(StrategyPerformance performance);

    @Delete("DELETE FROM strategy_performance WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM strategy_performance WHERE strategy_id=#{strategyId}")
    List<StrategyPerformance> selectByStrategyId(Long strategyId);
}