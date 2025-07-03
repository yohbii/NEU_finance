-- 因子分析结果表
CREATE TABLE factor_analysis_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    analysis_id VARCHAR(50) NOT NULL UNIQUE COMMENT '分析ID',
    analysis_type VARCHAR(50) NOT NULL COMMENT '分析类型 correlation/effectiveness/contribution/stability',
    analysis_name VARCHAR(200) COMMENT '分析名称',
    analysis_params JSON COMMENT '分析参数',
    result_data JSON COMMENT '分析结果数据',
    status TINYINT DEFAULT 1 COMMENT '状态 1完成 0进行中 -1失败',
    start_time TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_analysis_id (analysis_id),
    INDEX idx_analysis_type (analysis_type),
    INDEX idx_status (status)
);

-- 插入一些示例数据
INSERT INTO factor_analysis_result (analysis_id, analysis_type, analysis_name, analysis_params, result_data, status, start_time, end_time) VALUES
('test-correlation-001', 'correlation', '因子相关性分析测试', 
 '{"type":"correlation","dateRange":["2023-01-01","2023-12-31"],"fundPool":"all","selectedFactors":["value","growth","quality"]}',
 '{"factors":["市盈率","市净率","ROE","营收增长率","净利润增长率"],"correlationMatrix":[[1.0,0.65,-0.23,0.12,0.18],[0.65,1.0,-0.31,0.08,0.15],[-0.23,-0.31,1.0,0.45,0.52],[0.12,0.08,0.45,1.0,0.78],[0.18,0.15,0.52,0.78,1.0]]}',
 1, '2024-01-01 10:00:00', '2024-01-01 10:05:00'),

('test-effectiveness-001', 'effectiveness', '因子有效性检验测试',
 '{"type":"effectiveness","dateRange":["2023-01-01","2023-12-31"],"fundPool":"stock","selectedFactors":["value","growth"]}',
 '{"effectivenessTest":[{"factorName":"市盈率","icMean":0.045,"icStd":0.123,"icIR":0.366,"winRate":0.58,"tStat":2.34,"pValue":0.019,"effectiveness":"有效"},{"factorName":"市净率","icMean":0.038,"icStd":0.135,"icIR":0.281,"winRate":0.54,"tStat":1.89,"pValue":0.059,"effectiveness":"一般"},{"factorName":"ROE","icMean":0.062,"icStd":0.098,"icIR":0.633,"winRate":0.63,"tStat":3.45,"pValue":0.001,"effectiveness":"有效"}]}',
 1, '2024-01-01 11:00:00', '2024-01-01 11:03:00'),

('test-contribution-001', 'contribution', '因子贡献度分析测试',
 '{"type":"contribution","dateRange":["2023-01-01","2023-12-31"],"fundPool":"all","selectedFactors":["value","growth","quality","momentum"]}',
 '{"contributionData":[{"factorName":"价值因子","contribution":0.35,"cumulativeContribution":0.35},{"factorName":"成长因子","contribution":0.28,"cumulativeContribution":0.63},{"factorName":"质量因子","contribution":0.22,"cumulativeContribution":0.85},{"factorName":"动量因子","contribution":0.15,"cumulativeContribution":1.00}]}',
 1, '2024-01-01 12:00:00', '2024-01-01 12:02:00'),

('test-stability-001', 'stability', '因子稳定性分析测试',
 '{"type":"stability","dateRange":["2023-01-01","2023-12-31"],"fundPool":"all","selectedFactors":["value","growth","quality"]}',
 '{"stabilityMetrics":[{"factorName":"价值因子","stabilityScore":0.78,"importance":"高"},{"factorName":"成长因子","stabilityScore":0.65,"importance":"中"},{"factorName":"质量因子","stabilityScore":0.82,"importance":"高"}]}',
 1, '2024-01-01 13:00:00', '2024-01-01 13:01:00'); 