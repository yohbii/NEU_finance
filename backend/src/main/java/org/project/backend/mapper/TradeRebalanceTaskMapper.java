package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.TradeRebalanceTask;
import java.util.List;

@Mapper
public interface TradeRebalanceTaskMapper {

    @Insert("INSERT INTO trade_rebalance_task (combo_id, type, status, create_time, finish_time, remark) " +
            "VALUES (#{comboId}, #{type}, #{status}, #{createTime}, #{finishTime}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeRebalanceTask task);

    @Update("UPDATE trade_rebalance_task SET status=#{status}, finish_time=#{finishTime}, remark=#{remark} WHERE id=#{id}")
    int update(TradeRebalanceTask task);

    @Select("SELECT * FROM trade_rebalance_task WHERE id=#{id}")
    TradeRebalanceTask selectById(Long id);

    @Select("SELECT * FROM trade_rebalance_task WHERE combo_id=#{comboId}")
    List<TradeRebalanceTask> selectByComboId(Long comboId);

    @Delete("DELETE FROM trade_rebalance_task WHERE id=#{id}")
    int deleteById(Long id);
}