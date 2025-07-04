-- 智能投顾系统数据库初始化脚本 (简化版)
CREATE DATABASE IF NOT EXISTS intelligent_advisor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE intelligent_advisor;

-- 1. 用户表 (简化)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 基金信息表
CREATE TABLE fund_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fund_code VARCHAR(20) NOT NULL UNIQUE COMMENT '基金代码',
    fund_name VARCHAR(200) NOT NULL COMMENT '基金名称',
    fund_type VARCHAR(50) NOT NULL COMMENT '基金类型',
    fund_company VARCHAR(200) NOT NULL COMMENT '基金公司',
    fund_manager VARCHAR(100) COMMENT '基金经理',
    establish_date DATE COMMENT '成立日期',
    unit_net_value DECIMAL(10,4) COMMENT '单位净值',
    accumulated_net_value DECIMAL(10,4) COMMENT '累计净值',
    risk_level TINYINT DEFAULT 1 COMMENT '风险等级 1-5',
    min_investment DECIMAL(15,2) COMMENT '最小投资金额',
    management_fee DECIMAL(8,4) COMMENT '管理费率',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0停用',
    description TEXT COMMENT '基金描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fund_code (fund_code),
    INDEX idx_fund_type (fund_type),
    INDEX idx_fund_company (fund_company)
);

-- 3. 基金净值表
CREATE TABLE fund_net_value (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fund_id BIGINT NOT NULL COMMENT '基金ID',
    trade_date DATE NOT NULL COMMENT '交易日期',
    unit_net_value DECIMAL(10,4) NOT NULL COMMENT '单位净值',
    accumulated_net_value DECIMAL(10,4) COMMENT '累计净值',
    daily_return DECIMAL(8,4) COMMENT '日收益率',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fund_date (fund_id, trade_date),
    INDEX idx_trade_date (trade_date),
    FOREIGN KEY (fund_id) REFERENCES fund_info(id) ON DELETE CASCADE
);

-- 4. 基金业绩表
CREATE TABLE fund_performance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fund_id BIGINT NOT NULL COMMENT '基金ID',
    performance_date DATE NOT NULL COMMENT '业绩日期',
    ytd_return DECIMAL(8,4) COMMENT '年初至今收益率',
    one_month_return DECIMAL(8,4) COMMENT '近1月收益率',
    three_month_return DECIMAL(8,4) COMMENT '近3月收益率',
    six_month_return DECIMAL(8,4) COMMENT '近6月收益率',
    one_year_return DECIMAL(8,4) COMMENT '近1年收益率',
    three_year_return DECIMAL(8,4) COMMENT '近3年收益率',
    max_drawdown DECIMAL(8,4) COMMENT '最大回撤',
    sharpe_ratio DECIMAL(8,4) COMMENT '夏普比率',
    volatility DECIMAL(8,4) COMMENT '波动率',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fund_perf_date (fund_id, performance_date),
    FOREIGN KEY (fund_id) REFERENCES fund_info(id) ON DELETE CASCADE
);

-- 5. 因子信息表
CREATE TABLE factor_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    factor_code VARCHAR(50) NOT NULL UNIQUE COMMENT '因子编码',
    factor_name VARCHAR(200) NOT NULL COMMENT '因子名称',
    factor_type VARCHAR(50) NOT NULL COMMENT '因子类型',
    factor_category VARCHAR(50) COMMENT '因子分类',
    description TEXT COMMENT '因子描述',
    calculation_formula TEXT COMMENT '计算公式',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_factor_code (factor_code),
    INDEX idx_factor_type (factor_type)
);

-- 6. 基金因子值表
CREATE TABLE fund_factor_value (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fund_id BIGINT NOT NULL COMMENT '基金ID',
    factor_id BIGINT NOT NULL COMMENT '因子ID',
    factor_date DATE NOT NULL COMMENT '因子日期',
    factor_value DECIMAL(15,6) COMMENT '因子值',
    percentile_rank DECIMAL(8,4) COMMENT '百分位排名',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_fund_factor_date (fund_id, factor_id, factor_date),
    INDEX idx_factor_date (factor_id, factor_date),
    FOREIGN KEY (fund_id) REFERENCES fund_info(id) ON DELETE CASCADE,
    FOREIGN KEY (factor_id) REFERENCES factor_info(id) ON DELETE CASCADE
);

-- 7. 策略信息表
CREATE TABLE strategy_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    strategy_code VARCHAR(50) NOT NULL UNIQUE COMMENT '策略编码',
    strategy_name VARCHAR(200) NOT NULL COMMENT '策略名称',
    strategy_type VARCHAR(50) NOT NULL COMMENT '策略类型',
    risk_level TINYINT DEFAULT 1 COMMENT '风险等级',
    target_return DECIMAL(8,4) COMMENT '目标收益率',
    max_drawdown DECIMAL(8,4) COMMENT '最大回撤',
    rebalance_frequency VARCHAR(20) COMMENT '再平衡频率',
    description TEXT COMMENT '策略描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1正常 0停用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_strategy_code (strategy_code),
    INDEX idx_strategy_type (strategy_type)
);

-- 8. 策略持仓表
CREATE TABLE strategy_holding (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    strategy_id BIGINT NOT NULL COMMENT '策略ID',
    fund_id BIGINT NOT NULL COMMENT '基金ID',
    target_weight DECIMAL(8,4) NOT NULL COMMENT '目标权重',
    current_weight DECIMAL(8,4) COMMENT '当前权重',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_strategy_fund (strategy_id, fund_id),
    FOREIGN KEY (strategy_id) REFERENCES strategy_info(id) ON DELETE CASCADE,
    FOREIGN KEY (fund_id) REFERENCES fund_info(id) ON DELETE CASCADE
);

-- 9. 策略回测表
CREATE TABLE strategy_backtest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    strategy_id BIGINT NOT NULL COMMENT '策略ID',
    backtest_name VARCHAR(200) COMMENT '回测名称',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    initial_capital DECIMAL(15,2) DEFAULT 1000000 COMMENT '初始资金',
    total_return DECIMAL(8,4) COMMENT '总收益率',
    annual_return DECIMAL(8,4) COMMENT '年化收益率',
    max_drawdown DECIMAL(8,4) COMMENT '最大回撤',
    sharpe_ratio DECIMAL(8,4) COMMENT '夏普比率',
    volatility DECIMAL(8,4) COMMENT '波动率',
    status TINYINT DEFAULT 1 COMMENT '状态 1完成 0进行中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_strategy (strategy_id),
    FOREIGN KEY (strategy_id) REFERENCES strategy_info(id) ON DELETE CASCADE
);

-- 10. 组合产品表
CREATE TABLE portfolio_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '产品编码',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称',
    product_type VARCHAR(50) NOT NULL COMMENT '产品类型',
    strategy_id BIGINT COMMENT '关联策略ID',
    risk_level TINYINT DEFAULT 1 COMMENT '风险等级',
    min_investment DECIMAL(15,2) COMMENT '最小投资金额',
    management_fee DECIMAL(8,4) COMMENT '管理费率',
    description TEXT COMMENT '产品描述',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_code (product_code),
    INDEX idx_strategy (strategy_id),
    FOREIGN KEY (strategy_id) REFERENCES strategy_info(id) ON DELETE SET NULL
);

-- 11. 交易记录表
CREATE TABLE trade_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trade_no VARCHAR(50) NOT NULL UNIQUE COMMENT '交易编号',
    portfolio_id BIGINT NOT NULL COMMENT '组合ID',
    fund_id BIGINT NOT NULL COMMENT '基金ID',
    trade_type TINYINT NOT NULL COMMENT '交易类型 1买入 2卖出',
    trade_amount DECIMAL(15,2) NOT NULL COMMENT '交易金额',
    trade_shares DECIMAL(15,4) COMMENT '交易份额',
    trade_price DECIMAL(10,4) COMMENT '交易价格',
    trade_fee DECIMAL(15,2) DEFAULT 0 COMMENT '交易费用',
    trade_date DATE NOT NULL COMMENT '交易日期',
    trade_status TINYINT DEFAULT 1 COMMENT '交易状态 1已成交 2部分成交 3已撤销',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trade_no (trade_no),
    INDEX idx_portfolio_fund (portfolio_id, fund_id),
    INDEX idx_trade_date (trade_date),
    FOREIGN KEY (portfolio_id) REFERENCES portfolio_product(id) ON DELETE CASCADE,
    FOREIGN KEY (fund_id) REFERENCES fund_info(id) ON DELETE CASCADE
);

-- 12. 调仓计划表
CREATE TABLE rebalance_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(200) NOT NULL COMMENT '计划名称',
    portfolio_id BIGINT NOT NULL COMMENT '组合ID',
    plan_date DATE NOT NULL COMMENT '计划日期',
    execution_date DATE COMMENT '执行日期',
    total_amount DECIMAL(15,2) COMMENT '总金额',
    plan_status TINYINT DEFAULT 0 COMMENT '计划状态 0待执行 1执行中 2已完成 3已取消',
    description TEXT COMMENT '计划描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_portfolio (portfolio_id),
    INDEX idx_plan_date (plan_date),
    INDEX idx_plan_status (plan_status),
    FOREIGN KEY (portfolio_id) REFERENCES portfolio_product(id) ON DELETE CASCADE
);

-- 13. 调仓明细表
CREATE TABLE rebalance_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    fund_id BIGINT NOT NULL COMMENT '基金ID',
    current_weight DECIMAL(8,4) COMMENT '当前权重',
    target_weight DECIMAL(8,4) COMMENT '目标权重',
    weight_diff DECIMAL(8,4) COMMENT '权重差异',
    trade_type TINYINT COMMENT '交易类型 1买入 2卖出',
    trade_amount DECIMAL(15,2) COMMENT '交易金额',
    trade_shares DECIMAL(15,4) COMMENT '交易份额',
    execution_status TINYINT DEFAULT 0 COMMENT '执行状态 0待执行 1已执行 2执行失败',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plan (plan_id),
    INDEX idx_fund (fund_id),
    FOREIGN KEY (plan_id) REFERENCES rebalance_plan(id) ON DELETE CASCADE,
    FOREIGN KEY (fund_id) REFERENCES fund_info(id) ON DELETE CASCADE
); 