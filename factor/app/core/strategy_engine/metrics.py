import pandas as pd
import numpy as np

TRADING_DAYS_PER_YEAR = 252

def calculate_performance_metrics(portfolio: pd.DataFrame) -> dict:
    print("指标计算: 开始计算性能指标...")
    
    returns = portfolio['strategy_return'].dropna()
    
    cumulative_return = (portfolio['portfolio_value'].iloc[-1] / portfolio['portfolio_value'].iloc[0]) - 1
    
    total_days = len(returns)
    annualized_return = (1 + cumulative_return) ** (TRADING_DAYS_PER_YEAR / total_days) - 1 if total_days > 0 else 0
    
    annualized_volatility = returns.std() * np.sqrt(TRADING_DAYS_PER_YEAR) if total_days > 0 else 0
    
    sharpe_ratio = (annualized_return / annualized_volatility) if annualized_volatility != 0 else 0
    
    cumulative = (1 + returns).cumprod()
    peak = cumulative.expanding(min_periods=1).max()
    drawdown = (cumulative - peak) / peak
    max_drawdown = drawdown.min()
    
    calmar_ratio = (annualized_return / abs(max_drawdown)) if max_drawdown != 0 else 0
    
    metrics = {
        "cumulative_return": cumulative_return,
        "annualized_return": annualized_return,
        "max_drawdown": max_drawdown,
        "sharpe_ratio": sharpe_ratio,
        "calmar_ratio": calmar_ratio
    }
    print(f"指标计算: 计算完成。{metrics}")
    
    return metrics