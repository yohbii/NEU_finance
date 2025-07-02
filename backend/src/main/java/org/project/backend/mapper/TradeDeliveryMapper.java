package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.TradeDelivery;
import java.util.List;

@Mapper
public interface TradeDeliveryMapper {

    @Insert("INSERT INTO trade_delivery (order_id, account_id, fund_code, trade_type, trade_amount, confirm_amount, confirm_status, confirm_time, message) " +
            "VALUES (#{orderId}, #{accountId}, #{fundCode}, #{tradeType}, #{tradeAmount}, #{confirmAmount}, #{confirmStatus}, #{confirmTime}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TradeDelivery delivery);

    @Update("UPDATE trade_delivery SET confirm_amount=#{confirmAmount}, confirm_status=#{confirmStatus}, confirm_time=#{confirmTime}, message=#{message} WHERE id=#{id}")
    int update(TradeDelivery delivery);

    @Select("SELECT * FROM trade_delivery WHERE id=#{id}")
    TradeDelivery selectById(Long id);

    @Select("SELECT * FROM trade_delivery WHERE order_id=#{orderId}")
    List<TradeDelivery> selectByOrderId(Long orderId);

    @Select("SELECT * FROM trade_delivery WHERE account_id=#{accountId}")
    List<TradeDelivery> selectByAccountId(Long accountId);

    @Delete("DELETE FROM trade_delivery WHERE id=#{id}")
    int deleteById(Long id);
}