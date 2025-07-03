package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.StrategyInfo;
import com.advisor.entity.StrategyHolding;
import com.advisor.mapper.StrategyInfoMapper;
import com.advisor.mapper.StrategyHoldingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 策略服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyService {

    private final StrategyInfoMapper strategyInfoMapper;
    private final StrategyHoldingMapper strategyHoldingMapper;

    /**
     * 分页查询策略列表
     */
    public PageResult<StrategyInfo> getStrategyList(String strategyType, String keyword, 
                                                   Integer current, Integer size) {
        current = current == null || current < 1 ? 1 : current;
        size = size == null || size < 1 ? 20 : size;
        
        Integer offset = (current - 1) * size;
        
        List<StrategyInfo> records = strategyInfoMapper.findList(strategyType, keyword, offset, size);
        Long total = strategyInfoMapper.countList(strategyType, keyword);
        
        return PageResult.of(records, total, current, size);
    }

    /**
     * 根据ID获取策略详情
     */
    public StrategyInfo getStrategyById(Long id) {
        if (id == null) {
            throw new RuntimeException("策略ID不能为空");
        }
        
        StrategyInfo strategyInfo = strategyInfoMapper.findById(id);
        if (strategyInfo == null) {
            throw new RuntimeException("策略不存在");
        }
        
        return strategyInfo;
    }

    /**
     * 创建策略
     */
    @Transactional
    public Long createStrategy(StrategyInfo strategyInfo) {
        if (strategyInfo == null) {
            throw new RuntimeException("策略信息不能为空");
        }
        if (!StringUtils.hasText(strategyInfo.getStrategyCode())) {
            throw new RuntimeException("策略代码不能为空");
        }
        if (!StringUtils.hasText(strategyInfo.getStrategyName())) {
            throw new RuntimeException("策略名称不能为空");
        }
        
        // 检查策略代码是否已存在
        StrategyInfo existStrategy = strategyInfoMapper.findByStrategyCode(strategyInfo.getStrategyCode());
        if (existStrategy != null) {
            throw new RuntimeException("策略代码已存在");
        }
        
        strategyInfo.setStatus(1);
        
        int result = strategyInfoMapper.insert(strategyInfo);
        if (result <= 0) {
            throw new RuntimeException("创建策略失败");
        }
        
        return strategyInfo.getId();
    }

    /**
     * 更新策略
     */
    public void updateStrategy(StrategyInfo strategyInfo) {
        if (strategyInfo == null || strategyInfo.getId() == null) {
            throw new RuntimeException("策略ID不能为空");
        }
        
        StrategyInfo existStrategy = strategyInfoMapper.findById(strategyInfo.getId());
        if (existStrategy == null) {
            throw new RuntimeException("策略不存在");
        }
        
        int result = strategyInfoMapper.update(strategyInfo);
        if (result <= 0) {
            throw new RuntimeException("更新策略失败");
        }
    }

    /**
     * 删除策略
     */
    @Transactional
    public void deleteStrategy(Long id) {
        if (id == null) {
            throw new RuntimeException("策略ID不能为空");
        }
        
        StrategyInfo existStrategy = strategyInfoMapper.findById(id);
        if (existStrategy == null) {
            throw new RuntimeException("策略不存在");
        }
        
        // 删除策略持仓
        strategyHoldingMapper.deleteByStrategyId(id);
        
        // 删除策略
        int result = strategyInfoMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除策略失败");
        }
    }

    /**
     * 获取策略持仓列表
     */
    public List<StrategyHolding> getStrategyHoldings(Long strategyId) {
        if (strategyId == null) {
            throw new RuntimeException("策略ID不能为空");
        }
        
        return strategyHoldingMapper.findByStrategyId(strategyId);
    }

    /**
     * 更新策略持仓
     */
    @Transactional
    public void updateStrategyHoldings(Long strategyId, List<StrategyHolding> holdings) {
        if (strategyId == null) {
            throw new RuntimeException("策略ID不能为空");
        }
        
        // 删除原有持仓
        strategyHoldingMapper.deleteByStrategyId(strategyId);
        
        // 插入新持仓
        if (holdings != null && !holdings.isEmpty()) {
            holdings.forEach(holding -> holding.setStrategyId(strategyId));
            strategyHoldingMapper.batchInsert(holdings);
        }
    }

    /**
     * 获取所有策略类型
     */
    public List<String> getAllStrategyTypes() {
        return strategyInfoMapper.findAllStrategyTypes();
    }
}