-- 1. 因子主表
-- is_derived 字段现在仅作为标记，具体的计算逻辑由外部（如Python脚本）处理
CREATE TABLE factor
(
    factor_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    factor_code   VARCHAR(100) NOT NULL UNIQUE,                             -- 英文唯一编码
    factor_name   VARCHAR(200) NOT NULL,                                    -- 中文名
    description   TEXT,
    data_type     ENUM ('NUMERIC','STRING','DATE')      DEFAULT 'NUMERIC',
    source_type   ENUM ('INTERNAL','PYTHON','EXTERNAL') DEFAULT 'INTERNAL', -- 接入方式
    is_derived    TINYINT(1)                            DEFAULT 0,          -- 标记是否为衍生因子
    create_time   TIMESTAMP                             DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP                             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Python脚本存储表
-- 用于存储 source_type = 'PYTHON' 的因子的具体计算脚本
CREATE TABLE factor_python_script
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    factor_id   BIGINT NOT NULL UNIQUE,         -- 关联的因子ID
    script_body MEDIUMTEXT,                     -- 存储Python脚本内容
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (factor_id) REFERENCES factor(factor_id) ON DELETE CASCADE
);

-- 3. 因子树 (简化后)
-- 使用 tree_body 字段存储整个树的JSON结构，方便前后端交互
CREATE TABLE factor_tree
(
    tree_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    tree_name   VARCHAR(100) NOT NULL UNIQUE, -- 如“量化研究分类树”
    description TEXT,
    tree_body   JSON,                         -- 存储树结构的JSON
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 因子风格分类
-- 用于给因子打上“风格”标签，如成长、价值等
CREATE TABLE factor_style
(
    style_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_code  VARCHAR(100) UNIQUE NOT NULL,
    style_name  VARCHAR(100)        NOT NULL,
    description TEXT
);
CREATE TABLE factor_style_map
(
    style_id  BIGINT NOT NULL,
    factor_id BIGINT NOT NULL,
    PRIMARY KEY (style_id, factor_id),
    FOREIGN KEY (style_id) REFERENCES factor_style (style_id),
    FOREIGN KEY (factor_id) REFERENCES factor (factor_id)
);
