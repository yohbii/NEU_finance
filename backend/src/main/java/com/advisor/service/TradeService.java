package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.TradeRecord;
import com.advisor.mapper.TradeRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 交易记录服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRecordMapper tradeRecordMapper;

    /**
     * 分页查询交易记录列表
     */
    public PageResult<TradeRecord> getTradeList(Long portfolioId, String assetCode, Integer tradeType, 
                                               LocalDate startDate, LocalDate endDate,
                                               Integer current, Integer size) {
        current = current == null || current < 1 ? 1 : current;
        size = size == null || size < 1 ? 20 : size;
        
        Integer offset = (current - 1) * size;
        
        List<TradeRecord> records = tradeRecordMapper.findList(portfolioId, assetCode, tradeType, 
                                                              startDate, endDate, offset, size);
        Long total = tradeRecordMapper.countList(portfolioId, assetCode, tradeType, 
                                                startDate, endDate);
        
        return PageResult.of(records, total, current, size);
    }

    /**
     * 根据ID获取交易记录详情
     */
    public TradeRecord getTradeById(Long id) {
        if (id == null) {
            throw new RuntimeException("交易记录ID不能为空");
        }
        
        TradeRecord trade = tradeRecordMapper.findById(id);
        if (trade == null) {
            throw new RuntimeException("交易记录不存在");
        }
        
        return trade;
    }

    /**
     * 根据交易编号获取交易记录详情
     */
    public TradeRecord getTradeByTradeNo(String tradeNo) {
        if (!StringUtils.hasText(tradeNo)) {
            throw new RuntimeException("交易编号不能为空");
        }
        
        TradeRecord trade = tradeRecordMapper.findByTradeNo(tradeNo);
        if (trade == null) {
            throw new RuntimeException("交易记录不存在");
        }
        
        return trade;
    }

    /**
     * 创建交易记录
     */
    public Long createTrade(TradeRecord tradeRecord) {
        if (tradeRecord == null) {
            throw new RuntimeException("交易记录信息不能为空");
        }
        if (tradeRecord.getPortfolioId() == null) {
            throw new RuntimeException("组合ID不能为空");
        }
//        if (!StringUtils.hasText(tradeRecord.getAssetCode())) {
//            throw new RuntimeException("资产代码不能为空");
//        }
        if (tradeRecord.getTradeType() == null) {
            throw new RuntimeException("交易类型不能为空");
        }
        if (tradeRecord.getTradeAmount() == null) {
            throw new RuntimeException("交易金额不能为空");
        }
        if (tradeRecord.getTradeDate() == null) {
            throw new RuntimeException("交易日期不能为空");
        }
        
        // 生成交易编号
        if (!StringUtils.hasText(tradeRecord.getTradeNo())) {
            tradeRecord.setTradeNo(generateTradeNo());
        }
        
        // 检查交易编号是否已存在
        TradeRecord existTrade = tradeRecordMapper.findByTradeNo(tradeRecord.getTradeNo());
        if (existTrade != null) {
            throw new RuntimeException("交易编号已存在");
        }
        
        tradeRecord.setTradeStatus(1);
        
        int result = tradeRecordMapper.insert(tradeRecord);
        if (result <= 0) {
            throw new RuntimeException("创建交易记录失败");
        }
        
        return tradeRecord.getId();
    }

    /**
     * 更新交易记录
     */
    public void updateTrade(TradeRecord tradeRecord) {
        if (tradeRecord == null || tradeRecord.getId() == null) {
            throw new RuntimeException("交易记录ID不能为空");
        }
        
        TradeRecord existTrade = tradeRecordMapper.findById(tradeRecord.getId());
        if (existTrade == null) {
            throw new RuntimeException("交易记录不存在");
        }
        
        int result = tradeRecordMapper.update(tradeRecord);
        if (result <= 0) {
            throw new RuntimeException("更新交易记录失败");
        }
    }

    /**
     * 删除交易记录
     */
    public void deleteTrade(Long id) {
        if (id == null) {
            throw new RuntimeException("交易记录ID不能为空");
        }
        
        TradeRecord existTrade = tradeRecordMapper.findById(id);
        if (existTrade == null) {
            throw new RuntimeException("交易记录不存在");
        }
        
        int result = tradeRecordMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除交易记录失败");
        }
    }

    /**
     * 根据组合ID查询交易记录
     */
    public List<TradeRecord> getTradesByPortfolioId(Long portfolioId) {
        if (portfolioId == null) {
            throw new RuntimeException("组合ID不能为空");
        }
        
        return tradeRecordMapper.findList(portfolioId, null, null, null, null, 0, Integer.MAX_VALUE);
    }

    /**
     * 批量创建交易记录
     */
    public void batchCreateTrades(List<TradeRecord> tradeRecords) {
        if (tradeRecords == null || tradeRecords.isEmpty()) {
            throw new RuntimeException("交易记录列表不能为空");
        }
        
        // 逐个创建交易记录
        for (TradeRecord trade : tradeRecords) {
            if (!StringUtils.hasText(trade.getTradeNo())) {
                trade.setTradeNo(generateTradeNo());
            }
            trade.setTradeStatus(1);
            
            int result = tradeRecordMapper.insert(trade);
            if (result <= 0) {
                throw new RuntimeException("批量创建交易记录失败");
            }
        }
    }

    /**
     * 生成交易编号
     */
    private String generateTradeNo() {
        return "T" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 