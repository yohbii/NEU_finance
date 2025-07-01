package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.TradeOrder;
import java.util.List;

@Mapper
public interface TradeOrderMapper {
    @Insert("INSERT INTO trade_order (task_id, account_id, fund_code, trade_type, trade_amount, status, fail_reason, create_time, update_time) " +
            "VALUES (#{taskId}, #{accountId}, #{fundCode}, #{tradeType}, #{tradeAmount}, #{status}, #{failReason}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeOrder order);

    @Update("UPDATE trade_order SET status=#{status}, fail_reason=#{failReason}, update_time=#{updateTime} WHERE id=#{id}")
    int update(TradeOrder order);

    @Select("SELECT * FROM trade_order WHERE id=#{id}")
    TradeOrder selectById(Long id);

    @Select("SELECT * FROM trade_order WHERE task_id=#{taskId}")
    List<TradeOrder> selectByTaskId(Long taskId);

    @Select("SELECT * FROM trade_order WHERE account_id=#{accountId}")
    List<TradeOrder> selectByAccountId(Long accountId);

    @Delete("DELETE FROM trade_order WHERE id=#{id}")
    int deleteById(Long id);
}