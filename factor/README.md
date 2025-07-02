# 智能投顾策略测试平台（Python后端）

## 项目简介
本项目为智能投顾平台的策略测试后端，支持接收前端传来的Python策略代码，基于本地Mock数据进行回测，并返回收益率、夏普比率、净值曲线等指标。

## 目录结构
```
factor/
  app/
    main.py                # FastAPI 启动入口
    api/
      strategy.py          # 策略测试API
    core/
      runner.py            # 策略执行与回测核心
      data_provider/
        mock_data.py       # 本地mock数据生成
    models/
      result.py            # 回测结果数据结构
    utils/
      sandbox.py           # 沙箱执行用户策略
  requirements.txt
  README.md
```

## 启动方法
```bash
cd factor
pip install -r requirements.txt
uvicorn app.main:app --reload
```

## API 用法
POST `/api/strategy/run`
- code: Python策略脚本（需定义 generate_signal(price_df, params)）
- params: 策略参数（可选）
- symbol, start_date, end_date, initial_capital: 回测参数

返回：回测指标与净值曲线