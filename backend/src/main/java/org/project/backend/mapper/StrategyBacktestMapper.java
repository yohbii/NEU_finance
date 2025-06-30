package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.StrategyBacktest;
import java.util.List;

@Mapper
public interface StrategyBacktestMapper {

    @Insert("INSERT INTO strategy_backtest (strategy_id, backtest_start, backtest_end, performance_json, created_at) " +
            "VALUES (#{strategyId}, #{backtestStart}, #{backtestEnd}, #{performanceJson}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StrategyBacktest backtest);

    @Update("UPDATE strategy_backtest SET strategy_id=#{strategyId}, backtest_start=#{backtestStart}, backtest_end=#{backtestEnd}, performance_json=#{performanceJson}, created_at=#{createdAt} WHERE id=#{id}")
    int update(StrategyBacktest backtest);

    @Delete("DELETE FROM strategy_backtest WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM strategy_backtest WHERE strategy_id=#{strategyId}")
    List<StrategyBacktest> selectByStrategyId(Long strategyId);
}