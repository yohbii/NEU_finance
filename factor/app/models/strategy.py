from pydantic import BaseModel, Field
from typing import Optional, Dict, Any
from datetime import date, datetime

class BacktestRequest(BaseModel):
    strategy_id: str = Field(..., description="要回测的策略的唯一标识符")
    start_date: date = Field(..., description="回测开始日期")
    end_date: date = Field(..., description="回测结束日期")
    initial_capital: float = Field(1000000.0, description="初始资金")

class TaskResponse(BaseModel):
    task_id: str = Field(..., description="后台任务的唯一ID，用于后续查询")
    status: str = Field("pending", description="任务的初始状态")
    message: str = Field("任务已提交，正在后台处理中", description="提示信息")

class BacktestMetrics(BaseModel):
    annualized_return: float = Field(..., description="年化回报率")
    cumulative_return: float = Field(..., description="累计回报率")
    max_drawdown: float = Field(..., description="最大回撤")
    sharpe_ratio: float = Field(..., description="夏普比率")
    calmar_ratio: float = Field(..., description="卡玛比率")

class TaskStatusResponse(BaseModel):
    task_id: str
    status: str = Field(..., description="任务当前状态 (e.g., pending, running, success, failed)")
    submitted_at: datetime = Field(..., description="任务提交时间")
    started_at: Optional[datetime] = Field(None, description="任务开始执行时间")
    finished_at: Optional[datetime] = Field(None, description="任务完成时间")
    result: Optional[BacktestMetrics] = Field(None, description="如果任务成功完成，这里是回测结果")
    error_message: Optional[str] = Field(None, description="如果任务失败，这里是错误信息")