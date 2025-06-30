import pandas as pd
import numpy as np

def run_vectorized_backtest(
    price_data: pd.DataFrame, 
    signal: pd.Series, 
    initial_capital: float
) -> pd.DataFrame:
    print("回测引擎: 开始执行矢量化回测...")
    
    portfolio = pd.DataFrame(index=price_data.index)
    portfolio['signal'] = signal
    portfolio['close'] = price_data['close']
    
    portfolio['positions'] = portfolio['signal'].diff().fillna(0)
    
    portfolio['daily_return'] = portfolio['close'].pct_change()
    
    portfolio['strategy_return'] = portfolio['daily_return'] * portfolio['signal'].shift(1)
    
    portfolio['cumulative_return'] = (1 + portfolio['strategy_return']).cumprod()
    portfolio['portfolio_value'] = initial_capital * portfolio['cumulative_return']
    portfolio['portfolio_value'].fillna(initial_capital, inplace=True)

    print("回测引擎: 回测执行完毕。")
    return portfolio