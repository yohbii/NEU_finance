from pydantic import BaseModel, Field
from typing import List

class BacktestResult(BaseModel):
    cumulative_return: float = Field(..., description="累计收益率")
    annualized_return: float = Field(..., description="年化收益率")
    max_drawdown: float = Field(..., description="最大回撤")
    sharpe_ratio: float = Field(..., description="夏普比率")
    equity_curve: List[float] = Field(..., description="净值曲线")
    dates: List[str] = Field(..., description="日期序列") 