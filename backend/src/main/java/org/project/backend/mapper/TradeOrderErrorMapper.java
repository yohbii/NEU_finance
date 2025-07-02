package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.TradeOrderError;
import java.util.List;

@Mapper
public interface TradeOrderErrorMapper {
    @Insert("INSERT INTO trade_order_error (order_id, account_id, fail_type, fail_reason, process_status, new_fund_code, create_time, update_time) " +
            "VALUES (#{orderId}, #{accountId}, #{failType}, #{failReason}, #{processStatus}, #{newFundCode}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeOrderError error);

    @Update("UPDATE trade_order_error SET process_status=#{processStatus}, new_fund_code=#{newFundCode}, update_time=#{updateTime} WHERE id=#{id}")
    int update(TradeOrderError error);

    @Select("SELECT * FROM trade_order_error WHERE id=#{id}")
    TradeOrderError selectById(Long id);

    @Select("SELECT * FROM trade_order_error WHERE order_id=#{orderId}")
    List<TradeOrderError> selectByOrderId(Long orderId);

    @Delete("DELETE FROM trade_order_error WHERE id=#{id}")
    int deleteById(Long id);
}