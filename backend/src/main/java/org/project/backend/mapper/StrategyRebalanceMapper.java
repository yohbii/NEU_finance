package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.StrategyRebalance;
import java.util.List;

@Mapper
public interface StrategyRebalanceMapper {

    @Insert("INSERT INTO strategy_rebalance (strategy_id, rebalance_time, rebalance_type, description) " +
            "VALUES (#{strategyId}, #{rebalanceTime}, #{rebalanceType}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StrategyRebalance rebalance);

    @Update("UPDATE strategy_rebalance SET strategy_id=#{strategyId}, rebalance_time=#{rebalanceTime}, rebalance_type=#{rebalanceType}, description=#{description} WHERE id=#{id}")
    int update(StrategyRebalance rebalance);

    @Delete("DELETE FROM strategy_rebalance WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM strategy_rebalance WHERE id=#{id}")
    StrategyRebalance selectById(Long id);

    @Select("SELECT * FROM strategy_rebalance WHERE strategy_id=#{strategyId}")
    List<StrategyRebalance> selectByStrategyId(Long strategyId);
}