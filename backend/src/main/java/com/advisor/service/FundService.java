package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.FundInfo;
import com.advisor.entity.FundPerformance;
import com.advisor.mapper.FundInfoMapper;
import com.advisor.mapper.FundPerformanceMapper;
import com.advisor.mapper.FundNetValueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基金服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FundService {

    private final FundInfoMapper fundInfoMapper;
    private final FundPerformanceMapper fundPerformanceMapper;
    private final FundNetValueMapper fundNetValueMapper;

    /**
     * 分页查询基金列表
     */
    public PageResult<FundInfo> getFundList(String fundType, String fundCompany, String fundManager, 
                                           Integer riskLevel, java.math.BigDecimal minInvestment, String keyword, 
                                           Integer current, Integer size) {
        // 参数校验
        current = current == null || current < 1 ? 1 : current;
        size = size == null || size < 1 ? 20 : size;
        
        // 计算偏移量
        Integer offset = (current - 1) * size;
        
        // 查询数据
        List<FundInfo> records = fundInfoMapper.findList(fundType, fundCompany, fundManager, riskLevel, minInvestment, keyword, offset, size);
        Long total = fundInfoMapper.countList(fundType, fundCompany, fundManager, riskLevel, minInvestment, keyword);
        
        return PageResult.of(records, total, current, size);
    }

    /**
     * 根据ID获取基金详情
     */
    public FundInfo getFundById(Long id) {
        if (id == null) {
            throw new RuntimeException("基金ID不能为空");
        }
        
        FundInfo fundInfo = fundInfoMapper.findById(id);
        if (fundInfo == null) {
            throw new RuntimeException("基金不存在");
        }
        
        return fundInfo;
    }

    /**
     * 根据基金代码获取基金详情
     */
    public FundInfo getFundByCode(String fundCode) {
        if (!StringUtils.hasText(fundCode)) {
            throw new RuntimeException("基金代码不能为空");
        }
        
        FundInfo fundInfo = fundInfoMapper.findByFundCode(fundCode);
        if (fundInfo == null) {
            throw new RuntimeException("基金不存在");
        }
        
        return fundInfo;
    }

    /**
     * 创建基金
     */
    public Long createFund(FundInfo fundInfo) {
        // 参数校验
        if (fundInfo == null) {
            throw new RuntimeException("基金信息不能为空");
        }
        if (!StringUtils.hasText(fundInfo.getFundCode())) {
            throw new RuntimeException("基金代码不能为空");
        }
        if (!StringUtils.hasText(fundInfo.getFundName())) {
            throw new RuntimeException("基金名称不能为空");
        }
        
        // 检查基金代码是否已存在
        FundInfo existFund = fundInfoMapper.findByFundCode(fundInfo.getFundCode());
        if (existFund != null) {
            throw new RuntimeException("基金代码已存在");
        }
        
        // 设置默认状态
        fundInfo.setStatus(1);
        
        int result = fundInfoMapper.insert(fundInfo);
        if (result <= 0) {
            throw new RuntimeException("创建基金失败");
        }
        
        return fundInfo.getId();
    }

    /**
     * 更新基金
     */
    public void updateFund(FundInfo fundInfo) {
        if (fundInfo == null || fundInfo.getId() == null) {
            throw new RuntimeException("基金ID不能为空");
        }
        
        // 检查基金是否存在
        FundInfo existFund = fundInfoMapper.findById(fundInfo.getId());
        if (existFund == null) {
            throw new RuntimeException("基金不存在");
        }
        
        int result = fundInfoMapper.update(fundInfo);
        if (result <= 0) {
            throw new RuntimeException("更新基金失败");
        }
    }

    /**
     * 删除基金
     */
    public void deleteFund(Long id) {
        if (id == null) {
            throw new RuntimeException("基金ID不能为空");
        }
        
        // 检查基金是否存在
        FundInfo existFund = fundInfoMapper.findById(id);
        if (existFund == null) {
            throw new RuntimeException("基金不存在");
        }
        
        int result = fundInfoMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除基金失败");
        }
    }

    /**
     * 获取所有基金类型
     */
    public List<String> getAllFundTypes() {
        return fundInfoMapper.findAllFundTypes();
    }

    /**
     * 获取所有基金公司
     */
    public List<String> getAllFundCompanies() {
        return fundInfoMapper.findAllFundCompanies();
    }

    /**
     * 获取所有基金经理
     */
    public List<String> getAllFundManagers() {
        return fundInfoMapper.findAllFundManagers();
    }

    /**
     * 获取基金业绩数据
     */
    public FundPerformance getFundPerformance(Long fundId) {
        if (fundId == null) {
            throw new RuntimeException("基金ID不能为空");
        }
        
        // 先获取基金信息
        FundInfo fundInfo = getFundById(fundId);
        
        // 根据基金代码查询最新业绩
        FundPerformance performance = fundPerformanceMapper.findLatestByFundCode(fundInfo.getFundCode());
        
        return performance; // 可以为null，前端会处理
    }

    /**
     * 获取基金详细信息（包含业绩）
     */
    public Map<String, Object> getFundDetail(Long fundId) {
        Map<String, Object> detail = new HashMap<>();
        
        // 获取基金基本信息
        FundInfo fundInfo = getFundById(fundId);
        detail.put("fundInfo", fundInfo);
        
        // 获取业绩数据
        FundPerformance performance = getFundPerformance(fundId);
        detail.put("performance", performance);
        
        return detail;
    }

    /**
     * 获取基金净值数据
     */
    public List<Map<String, Object>> getFundNetValue(Long fundId, String period) {
        if (fundId == null) {
            throw new RuntimeException("基金ID不能为空");
        }
        
        // 根据时间段计算查询参数
        int days = switch (period) {
            case "1M" -> 30;
            case "3M" -> 90;
            case "6M" -> 180;
            case "1Y" -> 365;
            default -> 365;
        };
        
        // 计算开始日期
        java.time.LocalDate endDate = java.time.LocalDate.now();
        java.time.LocalDate startDate = endDate.minusDays(days);
        
        // 查询净值数据
        List<com.advisor.entity.FundNetValue> netValues = fundNetValueMapper.findByFundId(
            fundId, startDate, endDate, 0, 1000);
        
        // 转换为Map格式
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (com.advisor.entity.FundNetValue netValue : netValues) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", netValue.getTradeDate().toString());
            item.put("unitNetValue", netValue.getUnitNetValue());
            item.put("accumulatedNetValue", netValue.getAccumulatedNetValue());
            item.put("dailyReturn", netValue.getDailyReturn());
            result.add(item);
        }
        
        return result;
    }
} 