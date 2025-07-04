package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.FactorInfo;
import com.advisor.entity.FactorAnalysisResult;
import com.advisor.mapper.FactorInfoMapper;
import com.advisor.mapper.FactorAnalysisResultMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * 因子服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FactorService {

    private final FactorInfoMapper factorInfoMapper;
    private final FactorAnalysisResultMapper factorAnalysisResultMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分页查询因子列表
     */
    public PageResult<FactorInfo> getFactorList(String factorType, String category, String keyword, 
                                               Integer current, Integer size) {
        current = current == null || current < 1 ? 1 : current;
        size = size == null || size < 1 ? 20 : size;
        
        Integer offset = (current - 1) * size;
        
        List<FactorInfo> records = factorInfoMapper.findList(factorType, category, keyword, offset, size);
        Long total = factorInfoMapper.countList(factorType, category, keyword);
        
        return PageResult.of(records, total, current, size);
    }

    /**
     * 根据ID获取因子详情
     */
    public FactorInfo getFactorById(Long id) {
        if (id == null) {
            throw new RuntimeException("因子ID不能为空");
        }
        
        FactorInfo factorInfo = factorInfoMapper.findById(id);
        if (factorInfo == null) {
            throw new RuntimeException("因子不存在");
        }
        
        return factorInfo;
    }

    /**
     * 根据因子代码获取因子详情
     */
    public FactorInfo getFactorByCode(String factorCode) {
        if (!StringUtils.hasText(factorCode)) {
            throw new RuntimeException("因子代码不能为空");
        }
        
        FactorInfo factorInfo = factorInfoMapper.findByFactorCode(factorCode);
        if (factorInfo == null) {
            throw new RuntimeException("因子不存在");
        }
        
        return factorInfo;
    }

    /**
     * 创建因子
     */
    public Long createFactor(FactorInfo factorInfo) {
        if (factorInfo == null) {
            throw new RuntimeException("因子信息不能为空");
        }
        if (!StringUtils.hasText(factorInfo.getFactorCode())) {
            throw new RuntimeException("因子代码不能为空");
        }
        if (!StringUtils.hasText(factorInfo.getFactorName())) {
            throw new RuntimeException("因子名称不能为空");
        }
        
        // 检查因子代码是否已存在
        FactorInfo existFactor = factorInfoMapper.findByFactorCode(factorInfo.getFactorCode());
        if (existFactor != null) {
            throw new RuntimeException("因子代码已存在");
        }
        
        factorInfo.setStatus(1);
        
        int result = factorInfoMapper.insert(factorInfo);
        if (result <= 0) {
            throw new RuntimeException("创建因子失败");
        }
        
        return factorInfo.getId();
    }

    /**
     * 更新因子
     */
    public void updateFactor(FactorInfo factorInfo) {
        if (factorInfo == null || factorInfo.getId() == null) {
            throw new RuntimeException("因子ID不能为空");
        }
        
        FactorInfo existFactor = factorInfoMapper.findById(factorInfo.getId());
        if (existFactor == null) {
            throw new RuntimeException("因子不存在");
        }
        
        int result = factorInfoMapper.update(factorInfo);
        if (result <= 0) {
            throw new RuntimeException("更新因子失败");
        }
    }

    /**
     * 删除因子
     */
    public void deleteFactor(Long id) {
        if (id == null) {
            throw new RuntimeException("因子ID不能为空");
        }
        
        FactorInfo existFactor = factorInfoMapper.findById(id);
        if (existFactor == null) {
            throw new RuntimeException("因子不存在");
        }
        
        int result = factorInfoMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除因子失败");
        }
    }

    /**
     * 获取所有因子类型
     */
    public List<String> getAllFactorTypes() {
        return factorInfoMapper.findAllFactorTypes();
    }

    /**
     * 获取所有因子分类
     */
    public List<String> getAllCategories() {
        return factorInfoMapper.findAllCategories();
    }

    /**
     * 执行因子分析
     */
    public Map<String, Object> runFactorAnalysis(Map<String, Object> analysisParams) {
        // 生成分析ID
        String analysisId = UUID.randomUUID().toString();
        
        try {
            // 创建分析结果记录
            FactorAnalysisResult analysisResult = new FactorAnalysisResult();
            analysisResult.setAnalysisId(analysisId);
            analysisResult.setAnalysisType((String) analysisParams.get("type"));
            analysisResult.setAnalysisName("因子分析-" + analysisResult.getAnalysisType());
            analysisResult.setAnalysisParams(objectMapper.writeValueAsString(analysisParams));
            analysisResult.setStatus(0); // 进行中
            analysisResult.setStartTime(LocalDateTime.now());
            
            // 保存到数据库
            factorAnalysisResultMapper.insert(analysisResult);
            
            // TODO: 实现真实的因子分析逻辑
            // 这里可以根据analysisParams中的参数进行实际的因子分析计算
            
            // 模拟分析过程完成，更新状态
            String resultData = generateAnalysisResultData(analysisParams);
            analysisResult.setResultData(resultData);
            analysisResult.setStatus(1); // 完成
            analysisResult.setEndTime(LocalDateTime.now());
            factorAnalysisResultMapper.updateByAnalysisId(analysisResult);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("analysisId", analysisId);
            result.put("status", "completed");
            result.put("message", "因子分析执行成功");
            
            // 解析并返回分析结果数据
            Map<String, Object> resultDataMap = objectMapper.readValue(resultData, Map.class);
            result.putAll(resultDataMap);
            
            return result;
            
        } catch (Exception e) {
            log.error("执行因子分析失败: {}", e.getMessage(), e);
            throw new RuntimeException("执行因子分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取因子分析结果
     */
    public Map<String, Object> getAnalysisResult(String analysisId) {
        try {
            // 从数据库获取分析结果
            FactorAnalysisResult analysisResult = factorAnalysisResultMapper.findByAnalysisId(analysisId);
            
            if (analysisResult == null) {
                throw new RuntimeException("分析结果不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("analysisId", analysisId);
            result.put("analysisType", analysisResult.getAnalysisType());
            result.put("analysisName", analysisResult.getAnalysisName());
            result.put("status", analysisResult.getStatus() == 1 ? "completed" : 
                              analysisResult.getStatus() == 0 ? "running" : "failed");
            result.put("startTime", analysisResult.getStartTime());
            result.put("endTime", analysisResult.getEndTime());
            result.put("message", "获取分析结果成功");
            
            // 解析分析结果数据
            if (analysisResult.getResultData() != null) {
                Map<String, Object> resultData = objectMapper.readValue(analysisResult.getResultData(), Map.class);
                result.putAll(resultData);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("获取因子分析结果失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取因子分析结果失败: " + e.getMessage());
        }
    }

    /**
     * 生成分析结果数据
     */
    private String generateAnalysisResultData(Map<String, Object> analysisParams) throws Exception {
        String analysisType = (String) analysisParams.get("type");
        Map<String, Object> resultData = new HashMap<>();
        
        // 根据分析类型生成对应的结果数据
        switch (analysisType) {
            case "correlation":
                String[] factors = {"市盈率", "市净率", "ROE", "营收增长率", "净利润增长率"};
                double[][] matrix = {
                    {1.0, 0.65, -0.23, 0.12, 0.18},
                    {0.65, 1.0, -0.31, 0.08, 0.15},
                    {-0.23, -0.31, 1.0, 0.45, 0.52},
                    {0.12, 0.08, 0.45, 1.0, 0.78},
                    {0.18, 0.15, 0.52, 0.78, 1.0}
                };
                
                // 转换为前端期望的格式
                List<Map<String, Object>> correlationMatrix = new ArrayList<>();
                for (int i = 0; i < factors.length; i++) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("factor", factors[i]);
                    for (int j = 0; j < factors.length; j++) {
                        row.put(factors[j], matrix[i][j]);
                    }
                    correlationMatrix.add(row);
                }
                
                resultData.put("factors", factors);
                resultData.put("correlationMatrix", correlationMatrix);
                break;
                
            case "effectiveness":
                List<Map<String, Object>> effectivenessTest = List.of(
                    Map.of("factorName", "市盈率", "icMean", 0.045, "icStd", 0.123, "icIR", 0.366, 
                           "winRate", 0.58, "tStat", 2.34, "pValue", 0.019, "effectiveness", "有效"),
                    Map.of("factorName", "市净率", "icMean", 0.038, "icStd", 0.135, "icIR", 0.281, 
                           "winRate", 0.54, "tStat", 1.89, "pValue", 0.059, "effectiveness", "一般"),
                    Map.of("factorName", "ROE", "icMean", 0.062, "icStd", 0.098, "icIR", 0.633, 
                           "winRate", 0.63, "tStat", 3.45, "pValue", 0.001, "effectiveness", "有效")
                );
                resultData.put("effectivenessTest", effectivenessTest);
                break;
                
            case "contribution":
                List<Map<String, Object>> contributionData = List.of(
                    Map.of("factorName", "价值因子", "contribution", 0.35, "cumulativeContribution", 0.35),
                    Map.of("factorName", "成长因子", "contribution", 0.28, "cumulativeContribution", 0.63),
                    Map.of("factorName", "质量因子", "contribution", 0.22, "cumulativeContribution", 0.85),
                    Map.of("factorName", "动量因子", "contribution", 0.15, "cumulativeContribution", 1.00)
                );
                resultData.put("contributionData", contributionData);
                break;
                
            case "stability":
                List<Map<String, Object>> stabilityMetrics = List.of(
                    Map.of("factorName", "价值因子", "stabilityScore", 0.78, "importance", "高"),
                    Map.of("factorName", "成长因子", "stabilityScore", 0.65, "importance", "中"),
                    Map.of("factorName", "质量因子", "stabilityScore", 0.82, "importance", "高")
                );
                resultData.put("stabilityMetrics", stabilityMetrics);
                break;
                
            default:
                // 如果类型未知，返回空结果
                break;
        }
        
        return objectMapper.writeValueAsString(resultData);
    }
} 