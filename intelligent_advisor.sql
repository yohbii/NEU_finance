/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80038 (8.0.38)
 Source Host           : localhost:3306
 Source Schema         : intelligent_advisor

 Target Server Type    : MySQL
 Target Server Version : 80038 (8.0.38)
 File Encoding         : 65001

 Date: 03/07/2025 20:52:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for factor_analysis_result
-- ----------------------------
DROP TABLE IF EXISTS `factor_analysis_result`;
CREATE TABLE `factor_analysis_result`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `analysis_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分析ID',
  `analysis_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分析类型 correlation/effectiveness/contribution/stability',
  `analysis_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分析名称',
  `analysis_params` json NULL COMMENT '分析参数',
  `result_data` json NULL COMMENT '分析结果数据',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1完成 0进行中 -1失败',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `analysis_id`(`analysis_id` ASC) USING BTREE,
  INDEX `idx_analysis_id`(`analysis_id` ASC) USING BTREE,
  INDEX `idx_analysis_type`(`analysis_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of factor_analysis_result
-- ----------------------------
INSERT INTO `factor_analysis_result` VALUES (1, 'test-correlation-001', 'correlation', '因子相关性分析测试', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2023-01-01\", \"2023-12-31\"], \"selectedFactors\": [\"value\", \"growth\", \"quality\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [[1.0, 0.65, -0.23, 0.12, 0.18], [0.65, 1.0, -0.31, 0.08, 0.15], [-0.23, -0.31, 1.0, 0.45, 0.52], [0.12, 0.08, 0.45, 1.0, 0.78], [0.18, 0.15, 0.52, 0.78, 1.0]]}', 1, '2024-01-01 10:00:00', '2024-01-01 10:05:00', '2025-06-30 18:41:40', '2025-06-30 18:41:40');
INSERT INTO `factor_analysis_result` VALUES (2, 'test-effectiveness-001', 'effectiveness', '因子有效性检验测试', '{\"type\": \"effectiveness\", \"fundPool\": \"stock\", \"dateRange\": [\"2023-01-01\", \"2023-12-31\"], \"selectedFactors\": [\"value\", \"growth\"]}', '{\"effectivenessTest\": [{\"icIR\": 0.366, \"icStd\": 0.123, \"tStat\": 2.34, \"icMean\": 0.045, \"pValue\": 0.019, \"winRate\": 0.58, \"factorName\": \"市盈率\", \"effectiveness\": \"有效\"}, {\"icIR\": 0.281, \"icStd\": 0.135, \"tStat\": 1.89, \"icMean\": 0.038, \"pValue\": 0.059, \"winRate\": 0.54, \"factorName\": \"市净率\", \"effectiveness\": \"一般\"}, {\"icIR\": 0.633, \"icStd\": 0.098, \"tStat\": 3.45, \"icMean\": 0.062, \"pValue\": 0.001, \"winRate\": 0.63, \"factorName\": \"ROE\", \"effectiveness\": \"有效\"}]}', 1, '2024-01-01 11:00:00', '2024-01-01 11:03:00', '2025-06-30 18:41:40', '2025-06-30 18:41:40');
INSERT INTO `factor_analysis_result` VALUES (3, 'test-contribution-001', 'contribution', '因子贡献度分析测试', '{\"type\": \"contribution\", \"fundPool\": \"all\", \"dateRange\": [\"2023-01-01\", \"2023-12-31\"], \"selectedFactors\": [\"value\", \"growth\", \"quality\", \"momentum\"]}', '{\"contributionData\": [{\"factorName\": \"价值因子\", \"contribution\": 0.35, \"cumulativeContribution\": 0.35}, {\"factorName\": \"成长因子\", \"contribution\": 0.28, \"cumulativeContribution\": 0.63}, {\"factorName\": \"质量因子\", \"contribution\": 0.22, \"cumulativeContribution\": 0.85}, {\"factorName\": \"动量因子\", \"contribution\": 0.15, \"cumulativeContribution\": 1.0}]}', 1, '2024-01-01 12:00:00', '2024-01-01 12:02:00', '2025-06-30 18:41:40', '2025-06-30 18:41:40');
INSERT INTO `factor_analysis_result` VALUES (4, 'test-stability-001', 'stability', '因子稳定性分析测试', '{\"type\": \"stability\", \"fundPool\": \"all\", \"dateRange\": [\"2023-01-01\", \"2023-12-31\"], \"selectedFactors\": [\"value\", \"growth\", \"quality\"]}', '{\"stabilityMetrics\": [{\"factorName\": \"价值因子\", \"importance\": \"高\", \"stabilityScore\": 0.78}, {\"factorName\": \"成长因子\", \"importance\": \"中\", \"stabilityScore\": 0.65}, {\"factorName\": \"质量因子\", \"importance\": \"高\", \"stabilityScore\": 0.82}]}', 1, '2024-01-01 13:00:00', '2024-01-01 13:01:00', '2025-06-30 18:41:40', '2025-06-30 18:41:40');
INSERT INTO `factor_analysis_result` VALUES (5, 'd40f2c79-3414-44ca-a387-dd39eb021aa4', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [[1.0, 0.65, -0.23, 0.12, 0.18], [0.65, 1.0, -0.31, 0.08, 0.15], [-0.23, -0.31, 1.0, 0.45, 0.52], [0.12, 0.08, 0.45, 1.0, 0.78], [0.18, 0.15, 0.52, 0.78, 1.0]]}', 1, '2025-06-30 18:44:46', '2025-06-30 18:44:46', '2025-06-30 18:44:45', '2025-06-30 18:44:46');
INSERT INTO `factor_analysis_result` VALUES (6, '82ba4683-aed7-47bb-b0ac-93b8f80c6c30', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [[1.0, 0.65, -0.23, 0.12, 0.18], [0.65, 1.0, -0.31, 0.08, 0.15], [-0.23, -0.31, 1.0, 0.45, 0.52], [0.12, 0.08, 0.45, 1.0, 0.78], [0.18, 0.15, 0.52, 0.78, 1.0]]}', 1, '2025-06-30 18:45:09', '2025-06-30 18:45:09', '2025-06-30 18:45:08', '2025-06-30 18:45:08');
INSERT INTO `factor_analysis_result` VALUES (7, '6f449302-35a6-4a2f-b4f5-5f6236c851f7', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [{\"ROE\": -0.23, \"factor\": \"市盈率\", \"市净率\": 0.65, \"市盈率\": 1.0, \"营收增长率\": 0.12, \"净利润增长率\": 0.18}, {\"ROE\": -0.31, \"factor\": \"市净率\", \"市净率\": 1.0, \"市盈率\": 0.65, \"营收增长率\": 0.08, \"净利润增长率\": 0.15}, {\"ROE\": 1.0, \"factor\": \"ROE\", \"市净率\": -0.31, \"市盈率\": -0.23, \"营收增长率\": 0.45, \"净利润增长率\": 0.52}, {\"ROE\": 0.45, \"factor\": \"营收增长率\", \"市净率\": 0.08, \"市盈率\": 0.12, \"营收增长率\": 1.0, \"净利润增长率\": 0.78}, {\"ROE\": 0.52, \"factor\": \"净利润增长率\", \"市净率\": 0.15, \"市盈率\": 0.18, \"营收增长率\": 0.78, \"净利润增长率\": 1.0}]}', 1, '2025-06-30 18:48:04', '2025-06-30 18:48:05', '2025-06-30 18:48:05', '2025-06-30 18:48:05');
INSERT INTO `factor_analysis_result` VALUES (8, '1ca6387a-fde4-4431-8cd2-d69d6dd408a5', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [{\"ROE\": -0.23, \"factor\": \"市盈率\", \"市净率\": 0.65, \"市盈率\": 1.0, \"营收增长率\": 0.12, \"净利润增长率\": 0.18}, {\"ROE\": -0.31, \"factor\": \"市净率\", \"市净率\": 1.0, \"市盈率\": 0.65, \"营收增长率\": 0.08, \"净利润增长率\": 0.15}, {\"ROE\": 1.0, \"factor\": \"ROE\", \"市净率\": -0.31, \"市盈率\": -0.23, \"营收增长率\": 0.45, \"净利润增长率\": 0.52}, {\"ROE\": 0.45, \"factor\": \"营收增长率\", \"市净率\": 0.08, \"市盈率\": 0.12, \"营收增长率\": 1.0, \"净利润增长率\": 0.78}, {\"ROE\": 0.52, \"factor\": \"净利润增长率\", \"市净率\": 0.15, \"市盈率\": 0.18, \"营收增长率\": 0.78, \"净利润增长率\": 1.0}]}', 1, '2025-06-30 21:12:59', '2025-06-30 21:12:59', '2025-06-30 21:12:59', '2025-06-30 21:12:59');
INSERT INTO `factor_analysis_result` VALUES (9, 'e0b5ab2f-1c67-401b-bb55-4b23986444d9', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [{\"ROE\": -0.23, \"factor\": \"市盈率\", \"市净率\": 0.65, \"市盈率\": 1.0, \"营收增长率\": 0.12, \"净利润增长率\": 0.18}, {\"ROE\": -0.31, \"factor\": \"市净率\", \"市净率\": 1.0, \"市盈率\": 0.65, \"营收增长率\": 0.08, \"净利润增长率\": 0.15}, {\"ROE\": 1.0, \"factor\": \"ROE\", \"市净率\": -0.31, \"市盈率\": -0.23, \"营收增长率\": 0.45, \"净利润增长率\": 0.52}, {\"ROE\": 0.45, \"factor\": \"营收增长率\", \"市净率\": 0.08, \"市盈率\": 0.12, \"营收增长率\": 1.0, \"净利润增长率\": 0.78}, {\"ROE\": 0.52, \"factor\": \"净利润增长率\", \"市净率\": 0.15, \"市盈率\": 0.18, \"营收增长率\": 0.78, \"净利润增长率\": 1.0}]}', 1, '2025-06-30 21:13:51', '2025-06-30 21:13:51', '2025-06-30 21:13:51', '2025-06-30 21:13:51');
INSERT INTO `factor_analysis_result` VALUES (10, 'bb5a95b9-9f3e-441e-afcb-bffb5bbca625', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\", \"leverage\", \"quality\", \"liquidity\", \"beta\", \"volatility\", \"momentum\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [{\"ROE\": -0.23, \"factor\": \"市盈率\", \"市净率\": 0.65, \"市盈率\": 1.0, \"营收增长率\": 0.12, \"净利润增长率\": 0.18}, {\"ROE\": -0.31, \"factor\": \"市净率\", \"市净率\": 1.0, \"市盈率\": 0.65, \"营收增长率\": 0.08, \"净利润增长率\": 0.15}, {\"ROE\": 1.0, \"factor\": \"ROE\", \"市净率\": -0.31, \"市盈率\": -0.23, \"营收增长率\": 0.45, \"净利润增长率\": 0.52}, {\"ROE\": 0.45, \"factor\": \"营收增长率\", \"市净率\": 0.08, \"市盈率\": 0.12, \"营收增长率\": 1.0, \"净利润增长率\": 0.78}, {\"ROE\": 0.52, \"factor\": \"净利润增长率\", \"市净率\": 0.15, \"市盈率\": 0.18, \"营收增长率\": 0.78, \"净利润增长率\": 1.0}]}', 1, '2025-06-30 21:16:43', '2025-06-30 21:16:43', '2025-06-30 21:16:43', '2025-06-30 21:16:43');
INSERT INTO `factor_analysis_result` VALUES (11, 'aa28fa5d-810d-47f8-a42c-f062f9543c59', 'stability', '因子分析-stability', '{\"type\": \"stability\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\", \"leverage\", \"quality\", \"liquidity\", \"beta\", \"volatility\", \"momentum\"]}', '{\"stabilityMetrics\": [{\"factorName\": \"价值因子\", \"importance\": \"高\", \"stabilityScore\": 0.78}, {\"factorName\": \"成长因子\", \"importance\": \"中\", \"stabilityScore\": 0.65}, {\"factorName\": \"质量因子\", \"importance\": \"高\", \"stabilityScore\": 0.82}]}', 1, '2025-06-30 21:16:53', '2025-06-30 21:16:53', '2025-06-30 21:16:52', '2025-06-30 21:16:52');
INSERT INTO `factor_analysis_result` VALUES (12, 'c3a5544e-648f-45f4-8928-cee61c5881c4', 'effectiveness', '因子分析-effectiveness', '{\"type\": \"effectiveness\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\", \"leverage\", \"quality\", \"liquidity\", \"beta\", \"volatility\", \"momentum\"]}', '{\"effectivenessTest\": [{\"icIR\": 0.366, \"icStd\": 0.123, \"tStat\": 2.34, \"icMean\": 0.045, \"pValue\": 0.019, \"winRate\": 0.58, \"factorName\": \"市盈率\", \"effectiveness\": \"有效\"}, {\"icIR\": 0.281, \"icStd\": 0.135, \"tStat\": 1.89, \"icMean\": 0.038, \"pValue\": 0.059, \"winRate\": 0.54, \"factorName\": \"市净率\", \"effectiveness\": \"一般\"}, {\"icIR\": 0.633, \"icStd\": 0.098, \"tStat\": 3.45, \"icMean\": 0.062, \"pValue\": 0.001, \"winRate\": 0.63, \"factorName\": \"ROE\", \"effectiveness\": \"有效\"}]}', 1, '2025-06-30 21:17:04', '2025-06-30 21:17:04', '2025-06-30 21:17:03', '2025-06-30 21:17:03');
INSERT INTO `factor_analysis_result` VALUES (13, '5bb20028-0d79-45c0-bc42-9f6df4219998', 'contribution', '因子分析-contribution', '{\"type\": \"contribution\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\", \"leverage\", \"quality\", \"liquidity\", \"beta\", \"volatility\", \"momentum\"]}', '{\"contributionData\": [{\"factorName\": \"价值因子\", \"contribution\": 0.35, \"cumulativeContribution\": 0.35}, {\"factorName\": \"成长因子\", \"contribution\": 0.28, \"cumulativeContribution\": 0.63}, {\"factorName\": \"质量因子\", \"contribution\": 0.22, \"cumulativeContribution\": 0.85}, {\"factorName\": \"动量因子\", \"contribution\": 0.15, \"cumulativeContribution\": 1.0}]}', 1, '2025-06-30 21:17:10', '2025-06-30 21:17:10', '2025-06-30 21:17:09', '2025-06-30 21:17:09');
INSERT INTO `factor_analysis_result` VALUES (14, 'f0976189-ace1-49d2-837d-dbff6bf02a7c', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [{\"ROE\": -0.23, \"factor\": \"市盈率\", \"市净率\": 0.65, \"市盈率\": 1.0, \"营收增长率\": 0.12, \"净利润增长率\": 0.18}, {\"ROE\": -0.31, \"factor\": \"市净率\", \"市净率\": 1.0, \"市盈率\": 0.65, \"营收增长率\": 0.08, \"净利润增长率\": 0.15}, {\"ROE\": 1.0, \"factor\": \"ROE\", \"市净率\": -0.31, \"市盈率\": -0.23, \"营收增长率\": 0.45, \"净利润增长率\": 0.52}, {\"ROE\": 0.45, \"factor\": \"营收增长率\", \"市净率\": 0.08, \"市盈率\": 0.12, \"营收增长率\": 1.0, \"净利润增长率\": 0.78}, {\"ROE\": 0.52, \"factor\": \"净利润增长率\", \"市净率\": 0.15, \"市盈率\": 0.18, \"营收增长率\": 0.78, \"净利润增长率\": 1.0}]}', 1, '2025-06-30 21:26:11', '2025-06-30 21:26:11', '2025-06-30 21:26:10', '2025-06-30 21:26:10');
INSERT INTO `factor_analysis_result` VALUES (15, '97d30fa6-58ba-46b3-95f8-f488cec39ce8', 'correlation', '因子分析-correlation', '{\"type\": \"correlation\", \"fundPool\": \"all\", \"dateRange\": [\"2024-06-30\", \"2025-06-30\"], \"selectedFactors\": [\"size\", \"value\", \"profitability\", \"growth\"]}', '{\"factors\": [\"市盈率\", \"市净率\", \"ROE\", \"营收增长率\", \"净利润增长率\"], \"correlationMatrix\": [{\"ROE\": -0.23, \"factor\": \"市盈率\", \"市净率\": 0.65, \"市盈率\": 1.0, \"营收增长率\": 0.12, \"净利润增长率\": 0.18}, {\"ROE\": -0.31, \"factor\": \"市净率\", \"市净率\": 1.0, \"市盈率\": 0.65, \"营收增长率\": 0.08, \"净利润增长率\": 0.15}, {\"ROE\": 1.0, \"factor\": \"ROE\", \"市净率\": -0.31, \"市盈率\": -0.23, \"营收增长率\": 0.45, \"净利润增长率\": 0.52}, {\"ROE\": 0.45, \"factor\": \"营收增长率\", \"市净率\": 0.08, \"市盈率\": 0.12, \"营收增长率\": 1.0, \"净利润增长率\": 0.78}, {\"ROE\": 0.52, \"factor\": \"净利润增长率\", \"市净率\": 0.15, \"市盈率\": 0.18, \"营收增长率\": 0.78, \"净利润增长率\": 1.0}]}', 1, '2025-06-30 21:26:38', '2025-06-30 21:26:38', '2025-06-30 21:26:38', '2025-06-30 21:26:38');

-- ----------------------------
-- Table structure for factor_info
-- ----------------------------
DROP TABLE IF EXISTS `factor_info`;
CREATE TABLE `factor_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `factor_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '因子编码',
  `factor_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '因子名称',
  `factor_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '因子类型',
  `factor_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '因子分类',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '因子描述',
  `calculation_formula` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '计算公式',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `factor_code`(`factor_code` ASC) USING BTREE,
  INDEX `idx_factor_code`(`factor_code` ASC) USING BTREE,
  INDEX `idx_factor_type`(`factor_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of factor_info
-- ----------------------------
INSERT INTO `factor_info` VALUES (1, 'BETA', 'Beta系数', 'RISK', '风险因子', '衡量基金相对于市场的系统性风险', 'BETA = COV(R_fund, R_market) / VAR(R_market)', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `factor_info` VALUES (2, 'ALPHA', 'Alpha系数', 'RETURN', '收益因子', '衡量基金的超额收益能力', 'ALPHA = R_fund - (R_f + BETA * (R_market - R_f))', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `factor_info` VALUES (3, 'SHARPE', '夏普比率', 'RISK_ADJUSTED', '风险调整收益', '衡量基金的风险调整后收益', 'SHARPE = (R_fund - R_f) / STD(R_fund)', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `factor_info` VALUES (4, 'VOLATILITY', '波动率', 'RISK', '风险因子', '衡量基金收益的波动程度', 'VOLATILITY = STD(R_fund) * SQRT(252)', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `factor_info` VALUES (5, 'MAX_DRAWDOWN', '最大回撤', 'RISK', '风险因子', '衡量基金的最大损失幅度', 'MAX_DRAWDOWN = MAX((PEAK - TROUGH) / PEAK)', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `factor_info` VALUES (6, 'TREYNOR', '特雷诺比率', 'RISK_ADJUSTED', '风险调整收益', '衡量基金单位系统风险的超额收益', 'TREYNOR = (R_fund - R_f) / BETA', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `factor_info` VALUES (7, 'INFO_RATIO', '信息比率', 'RISK_ADJUSTED', '风险调整收益', '衡量基金相对基准的风险调整收益', 'INFO_RATIO = (R_fund - R_benchmark) / TRACKING_ERROR', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for fund_factor_value
-- ----------------------------
DROP TABLE IF EXISTS `fund_factor_value`;
CREATE TABLE `fund_factor_value`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `factor_id` bigint NOT NULL COMMENT '因子ID',
  `factor_date` date NOT NULL COMMENT '因子日期',
  `factor_value` decimal(15, 6) NULL DEFAULT NULL COMMENT '因子值',
  `percentile_rank` decimal(8, 4) NULL DEFAULT NULL COMMENT '百分位排名',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_fund_factor_date`(`fund_id` ASC, `factor_id` ASC, `factor_date` ASC) USING BTREE,
  INDEX `idx_factor_date`(`factor_id` ASC, `factor_date` ASC) USING BTREE,
  CONSTRAINT `fund_factor_value_ibfk_1` FOREIGN KEY (`fund_id`) REFERENCES `fund_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fund_factor_value_ibfk_2` FOREIGN KEY (`factor_id`) REFERENCES `factor_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fund_factor_value
-- ----------------------------
INSERT INTO `fund_factor_value` VALUES (1, 1, 1, '2024-01-05', 1.123400, 65.2300, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (2, 2, 1, '2024-01-05', 0.987600, 45.6700, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (3, 3, 1, '2024-01-05', 0.234500, 15.4300, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (4, 4, 1, '2024-01-05', 1.345600, 85.6700, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (5, 5, 1, '2024-01-05', 1.098700, 58.9000, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (6, 6, 1, '2024-01-05', 1.234500, 72.3400, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (7, 7, 1, '2024-01-05', 0.999900, 50.0000, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (9, 1, 2, '2024-01-05', 0.023400, 72.3400, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (10, 2, 2, '2024-01-05', 0.012300, 56.7800, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (11, 3, 2, '2024-01-05', 0.005600, 34.5600, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (12, 4, 2, '2024-01-05', -0.009800, 23.4500, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (13, 5, 2, '2024-01-05', 0.034500, 78.9000, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (14, 6, 2, '2024-01-05', 0.019800, 65.4300, '2025-06-30 14:17:49');
INSERT INTO `fund_factor_value` VALUES (15, 7, 2, '2024-01-05', 0.008700, 45.6700, '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for fund_info
-- ----------------------------
DROP TABLE IF EXISTS `fund_info`;
CREATE TABLE `fund_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fund_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '基金代码',
  `fund_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '基金名称',
  `fund_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '基金类型',
  `fund_company` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '基金公司',
  `fund_manager` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '基金经理',
  `establish_date` date NULL DEFAULT NULL COMMENT '成立日期',
  `unit_net_value` decimal(10, 4) NULL DEFAULT NULL COMMENT '单位净值',
  `accumulated_net_value` decimal(10, 4) NULL DEFAULT NULL COMMENT '累计净值',
  `risk_level` tinyint NULL DEFAULT 1 COMMENT '风险等级 1-5',
  `min_investment` decimal(15, 2) NULL DEFAULT NULL COMMENT '最小投资金额',
  `management_fee` decimal(8, 4) NULL DEFAULT NULL COMMENT '管理费率',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1正常 0停用',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '基金描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `fund_code`(`fund_code` ASC) USING BTREE,
  INDEX `idx_fund_code`(`fund_code` ASC) USING BTREE,
  INDEX `idx_fund_type`(`fund_type` ASC) USING BTREE,
  INDEX `idx_fund_company`(`fund_company` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fund_info
-- ----------------------------
INSERT INTO `fund_info` VALUES (1, '000001', '华夏成长混合', '债券型', '华夏基金', '张三', '2020-01-01', 1.2345, 1.2345, 3, 1000.00, 0.0150, 1, '专注成长股投资的混合型基金', '2025-06-30 14:17:49', '2025-06-30 17:12:56');
INSERT INTO `fund_info` VALUES (2, '000002', '易方达价值精选', '股票型', '易方达基金', '李四', '2019-06-15', 2.1234, 2.1234, 4, 1000.00, 0.0150, 1, '价值投资理念的股票型基金', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_info` VALUES (3, '000003', '南方稳健债券', '债券型', '南方基金', '王五', '2018-03-20', 1.0567, 1.2567, 2, 100.00, 0.0080, 1, '稳健收益的债券型基金', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_info` VALUES (4, '000004', '广发科技创新', '股票型', '广发基金', '赵六', '2021-09-10', 1.8765, 1.8765, 5, 1000.00, 0.0150, 1, '专注科技创新领域的股票基金', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_info` VALUES (5, '000005', '招商消费升级', '混合型', '招商基金', '钱七', '2020-12-01', 1.5432, 1.5432, 3, 1000.00, 0.0150, 1, '消费升级主题混合基金', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_info` VALUES (6, '000006', '博时医疗保健', '股票型', '博时基金', '孙八', '2019-03-15', 2.3456, 2.3456, 4, 1000.00, 0.0150, 1, '医疗保健行业股票基金', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_info` VALUES (7, '000007', '嘉实沪深300ETF', '指数型', '嘉实基金', '周九', '2018-12-01', 1.1234, 1.1234, 3, 100.00, 0.0050, 1, '跟踪沪深300指数的ETF基金', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_info` VALUES (17, '0000001', '华夏成长混合', '混合型基金', '华夏基金管理有限公司', '张明', '2020-01-15', 1.2580, 1.2580, 3, 100.00, 0.0150, 1, '专注于成长性企业投资的混合型基金', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (18, '0000002', '易方达价值精选', '股票型基金', '易方达基金管理有限公司', '李华', '2019-06-20', 2.1450, 2.3450, 4, 1000.00, 0.0150, 1, '价值投资理念，精选优质股票', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (19, '0000003', '南方稳健债券', '债券型基金', '南方基金管理股份有限公司', '王强', '2018-03-10', 1.0850, 1.2350, 2, 100.00, 0.0080, 1, '稳健的债券投资策略', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (20, '0000004', '博时货币市场', '货币基金', '博时基金管理有限公司', '赵敏', '2017-12-01', 1.0000, 1.0000, 1, 1.00, 0.0030, 1, '流动性强的货币市场基金', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (21, '0000005', '广发小盘成长', '股票型基金', '广发基金管理有限公司', '陈刚', '2020-09-15', 1.8920, 1.8920, 4, 100.00, 0.0150, 1, '专注小盘成长股投资', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (22, '0000006', '招商中证500', '指数型基金', '招商基金管理有限公司', '刘洋', '2019-02-28', 1.4560, 1.4560, 3, 100.00, 0.0120, 1, '跟踪中证500指数', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (23, '0000007', '工银瑞信红利', '股票型基金', '工银瑞信基金管理有限公司', '孙丽', '2018-11-20', 1.6780, 1.9780, 3, 1000.00, 0.0150, 1, '专注高股息率股票投资', '2025-06-30 19:27:28', '2025-06-30 19:27:28');
INSERT INTO `fund_info` VALUES (24, '0000008', '建信优选成长', '混合型基金', '建信基金管理有限责任公司', '马超', '2020-05-12', 1.3450, 1.3450, 3, 100.00, 0.0150, 1, '优选成长性企业投资', '2025-06-30 19:27:28', '2025-06-30 19:27:28');

-- ----------------------------
-- Table structure for fund_net_value
-- ----------------------------
DROP TABLE IF EXISTS `fund_net_value`;
CREATE TABLE `fund_net_value`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `unit_net_value` decimal(10, 4) NOT NULL COMMENT '单位净值',
  `accumulated_net_value` decimal(10, 4) NULL DEFAULT NULL COMMENT '累计净值',
  `daily_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '日收益率',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_fund_date`(`fund_id` ASC, `trade_date` ASC) USING BTREE,
  INDEX `idx_trade_date`(`trade_date` ASC) USING BTREE,
  CONSTRAINT `fund_net_value_ibfk_1` FOREIGN KEY (`fund_id`) REFERENCES `fund_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fund_net_value
-- ----------------------------
INSERT INTO `fund_net_value` VALUES (1, 1, '2024-01-01', 1.2000, 1.2000, 0.0000, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (2, 1, '2024-01-02', 1.2050, 1.2050, 0.0042, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (3, 1, '2024-01-03', 1.2100, 1.2100, 0.0041, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (4, 1, '2024-01-04', 1.2080, 1.2080, -0.0017, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (5, 1, '2024-01-05', 1.2345, 1.2345, 0.0219, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (6, 2, '2024-01-01', 2.1000, 2.1000, 0.0000, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (7, 2, '2024-01-02', 2.1100, 2.1100, 0.0048, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (8, 2, '2024-01-03', 2.1150, 2.1150, 0.0024, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (9, 2, '2024-01-04', 2.1200, 2.1200, 0.0024, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (10, 2, '2024-01-05', 2.1234, 2.1234, 0.0016, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (11, 3, '2024-01-01', 1.0500, 1.2500, 0.0000, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (12, 3, '2024-01-02', 1.0520, 1.2520, 0.0019, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (13, 3, '2024-01-03', 1.0540, 1.2540, 0.0019, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (14, 3, '2024-01-04', 1.0555, 1.2555, 0.0014, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (15, 3, '2024-01-05', 1.0567, 1.2567, 0.0011, '2025-06-30 14:17:49');
INSERT INTO `fund_net_value` VALUES (16, 1, '2024-01-31', 1.1800, 1.1800, 0.0120, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (17, 1, '2024-02-29', 1.2100, 1.2100, 0.0254, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (18, 1, '2024-03-31', 1.1950, 1.1950, -0.0124, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (19, 1, '2024-04-30', 1.2200, 1.2200, 0.0209, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (20, 1, '2024-05-31', 1.2450, 1.2450, 0.0205, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (21, 1, '2024-06-30', 1.2300, 1.2300, -0.0121, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (22, 1, '2024-07-31', 1.2580, 1.2580, 0.0228, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (23, 1, '2024-08-31', 1.2420, 1.2420, -0.0127, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (24, 1, '2024-09-30', 1.2650, 1.2650, 0.0185, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (25, 1, '2024-10-31', 1.2480, 1.2480, -0.0134, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (26, 1, '2024-11-30', 1.2720, 1.2720, 0.0192, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (27, 1, '2024-12-31', 1.2580, 1.2580, -0.0110, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (28, 2, '2024-01-31', 2.0500, 2.2500, 0.0180, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (29, 2, '2024-02-29', 2.0850, 2.2850, 0.0171, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (30, 2, '2024-03-31', 2.0650, 2.2650, -0.0096, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (31, 2, '2024-04-30', 2.0980, 2.2980, 0.0160, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (32, 2, '2024-05-31', 2.1250, 2.3250, 0.0129, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (33, 2, '2024-06-30', 2.1100, 2.3100, -0.0071, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (34, 2, '2024-07-31', 2.1380, 2.3380, 0.0133, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (35, 2, '2024-08-31', 2.1200, 2.3200, -0.0084, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (36, 2, '2024-09-30', 2.1450, 2.3450, 0.0118, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (37, 2, '2024-10-31', 2.1300, 2.3300, -0.0070, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (38, 2, '2024-11-30', 2.1520, 2.3520, 0.0103, '2025-06-30 19:27:28');
INSERT INTO `fund_net_value` VALUES (39, 2, '2024-12-31', 2.1450, 2.3450, -0.0033, '2025-06-30 19:27:28');

-- ----------------------------
-- Table structure for fund_performance
-- ----------------------------
DROP TABLE IF EXISTS `fund_performance`;
CREATE TABLE `fund_performance`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `performance_date` date NOT NULL COMMENT '业绩日期',
  `ytd_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '年初至今收益率',
  `one_month_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '近1月收益率',
  `three_month_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '近3月收益率',
  `six_month_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '近6月收益率',
  `one_year_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '近1年收益率',
  `three_year_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '近3年收益率',
  `max_drawdown` decimal(8, 4) NULL DEFAULT NULL COMMENT '最大回撤',
  `sharpe_ratio` decimal(8, 4) NULL DEFAULT NULL COMMENT '夏普比率',
  `volatility` decimal(8, 4) NULL DEFAULT NULL COMMENT '波动率',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_fund_perf_date`(`fund_id` ASC, `performance_date` ASC) USING BTREE,
  CONSTRAINT `fund_performance_ibfk_1` FOREIGN KEY (`fund_id`) REFERENCES `fund_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fund_performance
-- ----------------------------
INSERT INTO `fund_performance` VALUES (1, 1, '2024-01-05', 0.0287, 0.0287, 0.0856, 0.1245, 0.2345, 0.4567, -0.1234, 1.2345, 0.1567, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_performance` VALUES (2, 2, '2024-01-05', 0.0111, 0.0111, 0.0654, 0.1123, 0.1987, 0.3456, -0.0987, 1.4567, 0.1345, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_performance` VALUES (3, 3, '2024-01-05', 0.0064, 0.0064, 0.0234, 0.0456, 0.0567, 0.1234, -0.0234, 0.8765, 0.0456, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_performance` VALUES (4, 4, '2024-01-05', -0.0123, -0.0123, 0.0987, 0.1876, 0.3456, 0.6789, -0.2345, 1.5678, 0.2345, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_performance` VALUES (5, 5, '2024-01-05', 0.0543, 0.0543, 0.1234, 0.1876, 0.2567, 0.4321, -0.1456, 1.3456, 0.1678, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_performance` VALUES (6, 6, '2024-01-05', 0.0234, 0.0234, 0.0876, 0.1567, 0.2876, 0.5432, -0.1876, 1.4321, 0.1987, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `fund_performance` VALUES (7, 7, '2024-01-05', 0.0123, 0.0123, 0.0456, 0.0789, 0.1234, 0.2345, -0.0567, 0.9876, 0.0987, '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for portfolio_product
-- ----------------------------
DROP TABLE IF EXISTS `portfolio_product`;
CREATE TABLE `portfolio_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品编码',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品名称',
  `product_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '产品类型',
  `strategy_id` bigint NULL DEFAULT NULL COMMENT '关联策略ID',
  `risk_level` tinyint NULL DEFAULT 1 COMMENT '风险等级',
  `min_investment` decimal(15, 2) NULL DEFAULT NULL COMMENT '最小投资金额',
  `management_fee` decimal(8, 4) NULL DEFAULT NULL COMMENT '管理费率',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '产品描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `product_code`(`product_code` ASC) USING BTREE,
  INDEX `idx_product_code`(`product_code` ASC) USING BTREE,
  INDEX `idx_strategy`(`strategy_id` ASC) USING BTREE,
  CONSTRAINT `portfolio_product_ibfk_1` FOREIGN KEY (`strategy_id`) REFERENCES `strategy_info` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of portfolio_product
-- ----------------------------
INSERT INTO `portfolio_product` VALUES (1, 'PRODUCT_001', '稳健增长组合', 'FOF', 1, 3, 10000.00, 0.0120, '适合稳健型投资者的均衡配置产品', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `portfolio_product` VALUES (2, 'PRODUCT_002', '成长精选组合', 'FOF', 2, 4, 50000.00, 0.0150, '追求长期成长的价值投资产品', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `portfolio_product` VALUES (3, 'PRODUCT_003', '固收增强组合', 'FOF', 3, 2, 5000.00, 0.0080, '以债券为主的稳健收益产品', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `portfolio_product` VALUES (4, 'PRODUCT_004', '科技先锋组合', 'FOF', 4, 5, 100000.00, 0.0180, '专注科技创新的高收益产品', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `portfolio_product` VALUES (5, 'PRODUCT_005', '量化智选组合', 'FOF', 5, 4, 50000.00, 0.0160, '基于量化模型的智能投资产品', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `portfolio_product` VALUES (6, 'PD001', '稳健增长组合', '混合型', 1, 3, 10000.00, 0.0120, '适合稳健型投资者的均衡配置产品', 1, '2025-06-30 19:27:51', '2025-06-30 19:27:51');
INSERT INTO `portfolio_product` VALUES (7, 'PD002', '成长先锋组合', '股票型', 2, 4, 50000.00, 0.0150, '专注成长股投资的高收益产品', 1, '2025-06-30 19:27:51', '2025-06-30 19:27:51');
INSERT INTO `portfolio_product` VALUES (8, 'PD003', '固收增强组合', '债券型', 3, 2, 5000.00, 0.0080, '以债券为主的稳健收益产品', 1, '2025-06-30 19:27:51', '2025-06-30 19:27:51');
INSERT INTO `portfolio_product` VALUES (9, 'PD004', '量化精选组合', '量化型', 4, 3, 20000.00, 0.0180, '运用量化模型精选投资标的', 1, '2025-06-30 19:27:51', '2025-06-30 19:27:51');
INSERT INTO `portfolio_product` VALUES (10, 'PD005', '主题投资组合', '主题型', 5, 4, 30000.00, 0.0200, '把握主题投资机会的灵活配置产品', 1, '2025-06-30 19:27:51', '2025-06-30 19:27:51');

-- ----------------------------
-- Table structure for rebalance_detail
-- ----------------------------
DROP TABLE IF EXISTS `rebalance_detail`;
CREATE TABLE `rebalance_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `current_weight` decimal(8, 4) NULL DEFAULT NULL COMMENT '当前权重',
  `target_weight` decimal(8, 4) NULL DEFAULT NULL COMMENT '目标权重',
  `weight_diff` decimal(8, 4) NULL DEFAULT NULL COMMENT '权重差异',
  `trade_type` tinyint NULL DEFAULT NULL COMMENT '交易类型 1买入 2卖出',
  `trade_amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '交易金额',
  `trade_shares` decimal(15, 4) NULL DEFAULT NULL COMMENT '交易份额',
  `execution_status` tinyint NULL DEFAULT 0 COMMENT '执行状态 0待执行 1已执行 2执行失败',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_plan`(`plan_id` ASC) USING BTREE,
  INDEX `idx_fund`(`fund_id` ASC) USING BTREE,
  CONSTRAINT `rebalance_detail_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `rebalance_plan` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `rebalance_detail_ibfk_2` FOREIGN KEY (`fund_id`) REFERENCES `fund_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rebalance_detail
-- ----------------------------
INSERT INTO `rebalance_detail` VALUES (1, 1, 1, 0.2000, 0.2500, 0.0500, 1, 5000.00, 4048.5800, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_detail` VALUES (2, 1, 2, 0.2000, 0.1500, -0.0500, 2, 5000.00, 2354.6000, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_detail` VALUES (3, 1, 3, 0.3000, 0.3000, 0.0000, NULL, 0.00, 0.0000, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_detail` VALUES (5, 2, 1, 0.3000, 0.3500, 0.0500, 1, 25000.00, 20242.9100, 0, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_detail` VALUES (6, 2, 2, 0.3000, 0.2500, -0.0500, 2, 25000.00, 11773.0100, 0, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_detail` VALUES (7, 2, 4, 0.4000, 0.4000, 0.0000, NULL, 0.00, 0.0000, 0, '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for rebalance_plan
-- ----------------------------
DROP TABLE IF EXISTS `rebalance_plan`;
CREATE TABLE `rebalance_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
  `portfolio_id` bigint NOT NULL COMMENT '组合ID',
  `plan_date` date NOT NULL COMMENT '计划日期',
  `execution_date` date NULL DEFAULT NULL COMMENT '执行日期',
  `total_amount` decimal(15, 2) NULL DEFAULT NULL COMMENT '总金额',
  `plan_status` tinyint NULL DEFAULT 0 COMMENT '计划状态 0待执行 1执行中 2已完成 3已取消',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '计划描述',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_portfolio`(`portfolio_id` ASC) USING BTREE,
  INDEX `idx_plan_date`(`plan_date` ASC) USING BTREE,
  INDEX `idx_plan_status`(`plan_status` ASC) USING BTREE,
  CONSTRAINT `rebalance_plan_ibfk_1` FOREIGN KEY (`portfolio_id`) REFERENCES `portfolio_product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rebalance_plan
-- ----------------------------
INSERT INTO `rebalance_plan` VALUES (1, '2024年1月调仓计划', 1, '2024-01-15', '2024-01-15', 100000.00, 2, '月度例行调仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_plan` VALUES (2, '2024年2月调仓计划', 2, '2024-02-01', NULL, 500000.00, 0, '季度调仓计划', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `rebalance_plan` VALUES (3, '2024年1月债券调仓', 3, '2024-01-10', '2024-01-10', 50000.00, 2, '债券组合调仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for strategy_backtest
-- ----------------------------
DROP TABLE IF EXISTS `strategy_backtest`;
CREATE TABLE `strategy_backtest`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `strategy_id` bigint NOT NULL COMMENT '策略ID',
  `backtest_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回测名称',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `initial_capital` decimal(15, 2) NULL DEFAULT 1000000.00 COMMENT '初始资金',
  `total_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '总收益率',
  `annual_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '年化收益率',
  `max_drawdown` decimal(8, 4) NULL DEFAULT NULL COMMENT '最大回撤',
  `sharpe_ratio` decimal(8, 4) NULL DEFAULT NULL COMMENT '夏普比率',
  `volatility` decimal(8, 4) NULL DEFAULT NULL COMMENT '波动率',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1完成 0进行中',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_strategy`(`strategy_id` ASC) USING BTREE,
  CONSTRAINT `strategy_backtest_ibfk_1` FOREIGN KEY (`strategy_id`) REFERENCES `strategy_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of strategy_backtest
-- ----------------------------
INSERT INTO `strategy_backtest` VALUES (1, 1, '均衡配置策略-2023年回测', '2023-01-01', '2023-12-31', 1000000.00, 0.0856, 0.0856, -0.0567, 1.2345, 0.0987, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_backtest` VALUES (2, 2, '成长价值策略-2023年回测', '2023-01-01', '2023-12-31', 1000000.00, 0.1234, 0.1234, -0.0876, 1.4567, 0.1234, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_backtest` VALUES (3, 3, '稳健债券策略-2023年回测', '2023-01-01', '2023-12-31', 1000000.00, 0.0456, 0.0456, -0.0234, 0.8765, 0.0456, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_backtest` VALUES (4, 4, '科技创新策略-2023年回测', '2023-01-01', '2023-12-31', 1000000.00, 0.1876, 0.1876, -0.1567, 1.6789, 0.1876, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_backtest` VALUES (5, 5, '量化多因子策略-2023年回测', '2023-01-01', '2023-12-31', 1000000.00, 0.1098, 0.1098, -0.0789, 1.3456, 0.1098, 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for strategy_holding
-- ----------------------------
DROP TABLE IF EXISTS `strategy_holding`;
CREATE TABLE `strategy_holding`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `strategy_id` bigint NOT NULL COMMENT '策略ID',
  `current_amount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `target_amount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `asset_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `asset_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `asset_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资产代码',
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `target_weight` decimal(8, 4) NOT NULL COMMENT '目标权重',
  `current_weight` decimal(8, 4) NULL DEFAULT NULL COMMENT '当前权重',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_strategy_fund`(`strategy_id` ASC, `fund_id` ASC) USING BTREE,
  INDEX `fund_id`(`fund_id` ASC) USING BTREE,
  CONSTRAINT `strategy_holding_ibfk_1` FOREIGN KEY (`strategy_id`) REFERENCES `strategy_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `strategy_holding_ibfk_2` FOREIGN KEY (`fund_id`) REFERENCES `fund_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of strategy_holding
-- ----------------------------
INSERT INTO `strategy_holding` VALUES (1, 1, NULL, NULL, NULL, NULL, NULL, 1, 0.2000, 0.2000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (2, 1, NULL, NULL, NULL, NULL, NULL, 2, 0.2000, 0.2000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (3, 1, NULL, NULL, NULL, NULL, NULL, 3, 0.3000, 0.3000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (5, 2, NULL, NULL, NULL, NULL, NULL, 1, 0.3000, 0.3000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (6, 2, NULL, NULL, NULL, NULL, NULL, 2, 0.3000, 0.3000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (7, 2, NULL, NULL, NULL, NULL, NULL, 4, 0.4000, 0.4000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (8, 3, NULL, NULL, NULL, NULL, NULL, 3, 0.7000, 0.7000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (10, 4, NULL, NULL, NULL, NULL, NULL, 4, 0.6000, 0.6000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (11, 4, NULL, NULL, NULL, NULL, NULL, 6, 0.4000, 0.4000, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (12, 5, NULL, NULL, NULL, NULL, NULL, 1, 0.2500, 0.2500, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (13, 5, NULL, NULL, NULL, NULL, NULL, 2, 0.2500, 0.2500, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (14, 5, NULL, NULL, NULL, NULL, NULL, 4, 0.2500, 0.2500, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_holding` VALUES (15, 5, NULL, NULL, NULL, NULL, NULL, 5, 0.2500, 0.2500, '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for strategy_info
-- ----------------------------
DROP TABLE IF EXISTS `strategy_info`;
CREATE TABLE `strategy_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `strategy_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '策略编码',
  `strategy_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '策略名称',
  `strategy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '策略类型',
  `risk_level` tinyint NULL DEFAULT 1 COMMENT '风险等级',
  `target_return` decimal(8, 4) NULL DEFAULT NULL COMMENT '目标收益率',
  `max_drawdown` decimal(8, 4) NULL DEFAULT NULL COMMENT '最大回撤',
  `rebalance_frequency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '再平衡频率',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '策略描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1正常 0停用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `strategy_code`(`strategy_code` ASC) USING BTREE,
  INDEX `idx_strategy_code`(`strategy_code` ASC) USING BTREE,
  INDEX `idx_strategy_type`(`strategy_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of strategy_info
-- ----------------------------
INSERT INTO `strategy_info` VALUES (1, 'STRATEGY_001', '均衡配置策略', 'ASSET_ALLOCATION', 3, 0.0800, NULL, 'MONTHLY', '通过多资产配置实现风险收益平衡', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_info` VALUES (2, 'STRATEGY_002', '成长价值策略', 'FOF', 4, 0.1200, NULL, 'QUARTERLY', '精选成长和价值风格基金的FOF策略', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_info` VALUES (3, 'STRATEGY_003', '稳健债券策略', 'BOND', 2, 0.0500, NULL, 'MONTHLY', '以债券基金为主的稳健收益策略', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_info` VALUES (4, 'STRATEGY_004', '科技创新策略', 'SECTOR', 5, 0.1500, NULL, 'WEEKLY', '专注科技创新领域的高收益策略', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_info` VALUES (5, 'STRATEGY_005', '量化多因子策略', 'QUANTITATIVE', 4, 0.1000, NULL, 'DAILY', '基于多因子模型的量化投资策略', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `strategy_info` VALUES (6, 'ST001', '价值投资策略', '价值型', 3, 0.1200, 0.0800, '季度', '专注于低估值、高股息的价值股投资', 1, '2025-06-30 19:27:45', '2025-06-30 19:27:45');
INSERT INTO `strategy_info` VALUES (7, 'ST002', '成长投资策略', '成长型', 4, 0.1800, 0.1200, '月度', '专注于高成长性企业的投资策略', 1, '2025-06-30 19:27:45', '2025-06-30 19:27:45');
INSERT INTO `strategy_info` VALUES (8, 'ST003', '稳健配置策略', '平衡型', 2, 0.0800, 0.0500, '半年', '股债平衡配置，追求稳健收益', 1, '2025-06-30 19:27:45', '2025-06-30 19:27:45');
INSERT INTO `strategy_info` VALUES (9, 'ST004', '量化选股策略', '量化型', 3, 0.1500, 0.1000, '月度', '基于量化模型的股票选择策略', 1, '2025-06-30 19:27:45', '2025-06-30 19:27:45');
INSERT INTO `strategy_info` VALUES (10, 'ST005', '行业轮动策略', '主题型', 4, 0.2000, 0.1500, '季度', '根据经济周期进行行业配置', 1, '2025-06-30 19:27:45', '2025-06-30 19:27:45');

-- ----------------------------
-- Table structure for trade_record
-- ----------------------------
DROP TABLE IF EXISTS `trade_record`;
CREATE TABLE `trade_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交易编号',
  `portfolio_id` bigint NOT NULL COMMENT '组合ID',
  `fund_id` bigint NOT NULL COMMENT '基金ID',
  `trade_type` tinyint NOT NULL COMMENT '交易类型 1买入 2卖出',
  `trade_amount` decimal(15, 2) NOT NULL COMMENT '交易金额',
  `trade_shares` decimal(15, 4) NULL DEFAULT NULL COMMENT '交易份额',
  `trade_price` decimal(10, 4) NULL DEFAULT NULL COMMENT '交易价格',
  `trade_fee` decimal(15, 2) NULL DEFAULT 0.00 COMMENT '交易费用',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `trade_status` tinyint NULL DEFAULT 1 COMMENT '交易状态 1已成交 2部分成交 3已撤销',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `trade_no`(`trade_no` ASC) USING BTREE,
  INDEX `idx_trade_no`(`trade_no` ASC) USING BTREE,
  INDEX `idx_portfolio_fund`(`portfolio_id` ASC, `fund_id` ASC) USING BTREE,
  INDEX `idx_trade_date`(`trade_date` ASC) USING BTREE,
  INDEX `fund_id`(`fund_id` ASC) USING BTREE,
  CONSTRAINT `trade_record_ibfk_1` FOREIGN KEY (`portfolio_id`) REFERENCES `portfolio_product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `trade_record_ibfk_2` FOREIGN KEY (`fund_id`) REFERENCES `fund_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trade_record
-- ----------------------------
INSERT INTO `trade_record` VALUES (1, 'T20240101001', 1, 1, 1, 20000.00, 16194.3300, 1.2345, 20.00, '2024-01-01', 1, '初始建仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `trade_record` VALUES (2, 'T20240101002', 1, 2, 1, 20000.00, 9418.4000, 2.1234, 20.00, '2024-01-01', 1, '初始建仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `trade_record` VALUES (3, 'T20240101003', 1, 3, 1, 30000.00, 28396.8500, 1.0567, 15.00, '2024-01-01', 1, '初始建仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `trade_record` VALUES (5, 'T20240102001', 2, 1, 1, 150000.00, 121459.4800, 1.2345, 150.00, '2024-01-02', 1, '初始建仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `trade_record` VALUES (6, 'T20240102002', 2, 2, 1, 150000.00, 70638.0300, 2.1234, 150.00, '2024-01-02', 1, '初始建仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `trade_record` VALUES (7, 'T20240102003', 2, 4, 1, 200000.00, 106601.6800, 1.8765, 200.00, '2024-01-02', 1, '初始建仓', '2025-06-30 14:17:49', '2025-06-30 14:17:49');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$7JB720yubVSOfvVMe6/YiOzQeYmQQgJKzlGDhJUNYyLhGqYdTVLe2', 'admin@example.com', '系统管理员', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `users` VALUES (2, 'user1', '$2a$10$7JB720yubVSOfvVMe6/YiOzQeYmQQgJKzlGDhJUNYyLhGqYdTVLe2', 'user1@example.com', '投资顾问', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `users` VALUES (3, 'user2', '$2a$10$7JB720yubVSOfvVMe6/YiOzQeYmQQgJKzlGDhJUNYyLhGqYdTVLe2', 'user2@example.com', '策略分析师', 1, '2025-06-30 14:17:49', '2025-06-30 14:17:49');
INSERT INTO `users` VALUES (4, 'user', '$2a$10$DJWlH/azoJAPGm68sdivduTZUzyBs/PXfDHdiX72xcUsB3fgGqFCi', 'user@qq.com', NULL, 1, '2025-06-30 15:54:34', '2025-06-30 15:54:34');
INSERT INTO `users` VALUES (6, 'abcd', '$2a$10$kLve0xPAtvzh/bpDXjZKNeGm0Mm8BNCq03pIUvFeCNiowzixqPeP.', 'user@qq.com', NULL, 1, '2025-07-03 19:31:39', '2025-07-03 19:31:39');

SET FOREIGN_KEY_CHECKS = 1;
